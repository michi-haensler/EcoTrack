const { spawn } = require('child_process');
const fs = require('fs');
const path = require('path');

const {
  hasFlag,
  parsePortArg,
  resolveMetroPort,
  stripPortArgs,
} = require('./metro-port');

async function main() {
  const projectRoot = path.resolve(__dirname, '..');
  const cliArgs = process.argv.slice(2);
  const { explicit, port: requestedPort } = parsePortArg(cliArgs);
  const { existing, port } = await resolveMetroPort({
    preferredPort: requestedPort,
    strictPort: explicit,
  });

  if (existing) {
    if (hasFlag(cliArgs, '--reset-cache', '--resetCache')) {
      throw new Error(
        `Metro is already running on port ${port}. Stop it before using --reset-cache.`,
      );
    }

    console.log(`[metro] Reusing existing Metro on port ${port}`);
    return;
  }

  console.log(`[metro] Starting Metro on port ${port}`);

  const env = {
    ...process.env,
    RCT_METRO_PORT: String(port),
  };
  const reactNativeCli = path.join(projectRoot, 'node_modules', 'react-native', 'cli.js');
  const args = [
    fs.existsSync(reactNativeCli) ? reactNativeCli : require.resolve('react-native/cli'),
    'start',
    ...stripPortArgs(cliArgs),
    '--port',
    String(port),
  ];

  const child = spawn(process.execPath, args, {
    cwd: projectRoot,
    env,
    shell: false,
    stdio: 'inherit',
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
