const { spawn } = require('child_process');
const { execFileSync } = require('child_process');
const fs = require('fs');
const os = require('os');
const path = require('path');
const {
  hasFlag,
  parsePortArg,
  resolveMetroPort,
  stripPortArgs,
} = require('./metro-port');
const { ensureShortProjectRoot } = require('./project-root');

function findAndroidSdk() {
  const candidates = [];

  if (process.env.ANDROID_SDK_ROOT) {
    candidates.push(process.env.ANDROID_SDK_ROOT);
  }
  if (process.env.ANDROID_HOME) {
    candidates.push(process.env.ANDROID_HOME);
  }

  if (process.platform === 'win32' && process.env.LOCALAPPDATA) {
    candidates.push(path.join(process.env.LOCALAPPDATA, 'Android', 'Sdk'));
  } else if (process.platform === 'darwin') {
    candidates.push(path.join(os.homedir(), 'Library', 'Android', 'sdk'));
  } else {
    candidates.push(path.join(os.homedir(), 'Android', 'Sdk'));
  }

  return candidates.find((candidate) => candidate && fs.existsSync(candidate));
}

function prependPath(env, value) {
  if (!value || !fs.existsSync(value)) {
    return;
  }

  const separator = path.delimiter;
  const entries = (env.PATH || '').split(separator).filter(Boolean);
  if (!entries.includes(value)) {
    env.PATH = [value, ...entries].join(separator);
  }
}

function hasOption(argv, optionName) {
  return argv.some((arg) => arg === optionName || arg.startsWith(`${optionName}=`));
}

function readOptionValue(argv, optionName) {
  for (let index = 0; index < argv.length; index += 1) {
    const arg = argv[index];

    if (arg === optionName) {
      return argv[index + 1] || null;
    }

    if (arg.startsWith(`${optionName}=`)) {
      return arg.slice(optionName.length + 1) || null;
    }
  }

  return null;
}

function getAdbBinary(sdkRoot) {
  if (sdkRoot) {
    const adbFromSdk = path.join(sdkRoot, 'platform-tools', 'adb.exe');

    if (fs.existsSync(adbFromSdk)) {
      return adbFromSdk;
    }
  }

  return process.platform === 'win32' ? 'adb.exe' : 'adb';
}

function listConnectedDevices(adbBinary, env) {
  try {
    const output = execFileSync(adbBinary, ['devices'], {
      encoding: 'utf8',
      env,
      stdio: ['ignore', 'pipe', 'ignore'],
    });

    return output
      .split(/\r?\n/)
      .slice(1)
      .map((line) => line.trim())
      .filter(Boolean)
      .map((line) => line.split(/\s+/))
      .filter((parts) => parts.length >= 2 && parts[1] === 'device')
      .map((parts) => parts[0]);
  } catch {
    return [];
  }
}

async function main() {
  const sdkRoot = findAndroidSdk();
  const env = { ...process.env };
  const { projectRoot, alias } = ensureShortProjectRoot();
  const cliArgs = process.argv.slice(2);
  const { explicit, port: requestedPort } = parsePortArg(cliArgs);
  const { existing, port } = await resolveMetroPort({
    preferredPort: requestedPort,
    strictPort: explicit,
  });
  const adbBinary = getAdbBinary(sdkRoot);
  const connectedDevices = listConnectedDevices(adbBinary, env);

  env.RCT_METRO_PORT = String(port);
  env.NODE_BINARY = process.execPath;
  env.REACT_NATIVE_PROJECT_ROOT = projectRoot;
  prependPath(env, path.dirname(process.execPath));

  if (sdkRoot) {
    env.ANDROID_HOME = env.ANDROID_HOME || sdkRoot;
    env.ANDROID_SDK_ROOT = env.ANDROID_SDK_ROOT || sdkRoot;
    prependPath(env, path.join(sdkRoot, 'platform-tools'));
    prependPath(env, path.join(sdkRoot, 'emulator'));
    console.log(`[android] Using Android SDK at ${sdkRoot}`);
  }

  console.log(
    `[android] Using Metro on port ${port}${existing ? ' (existing server)' : ''}`,
  );
  if (alias) {
    console.log(`[android] Using short Windows path ${projectRoot}`);
  }

  const reactNativeCli = path.join(projectRoot, 'node_modules', 'react-native', 'cli.js');

  const args = [
    fs.existsSync(reactNativeCli) ? reactNativeCli : require.resolve('react-native/cli'),
    'run-android',
    ...stripPortArgs(cliArgs),
    '--port',
    String(port),
  ];

  const hasExplicitDeviceSelection =
    hasOption(cliArgs, '--device') ||
    hasOption(cliArgs, '--deviceId') ||
    hasOption(cliArgs, '--list-devices') ||
    hasOption(cliArgs, '--interactive');

  if (!hasExplicitDeviceSelection && connectedDevices.length > 0) {
    args.push('--device', connectedDevices[0]);
    console.log(`[android] Target device ${connectedDevices[0]}`);
  } else if (readOptionValue(cliArgs, '--device')) {
    console.log(`[android] Target device ${readOptionValue(cliArgs, '--device')}`);
  }

  if (existing && !hasFlag(cliArgs, '--no-packager')) {
    args.push('--no-packager');
  }

  const child = spawn(process.execPath, args, {
    cwd: projectRoot,
    stdio: 'inherit',
    env,
    shell: false,
  });

  child.on('exit', (code, signal) => {
    if (signal) {
      process.kill(process.pid, signal);
      return;
    }

    process.exit(code ?? 1);
  });

  child.on('error', (error) => {
    console.error(error.message);
    process.exit(1);
  });
}

main().catch((error) => {
  console.error(error.message);
  process.exit(1);
});
