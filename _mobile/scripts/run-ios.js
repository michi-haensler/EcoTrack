const { spawn } = require('child_process');

const {
  hasFlag,
  parsePortArg,
  resolveMetroPort,
  stripPortArgs,
} = require('./metro-port');

async function main() {
  const cliArgs = process.argv.slice(2);
  const { explicit, port: requestedPort } = parsePortArg(cliArgs);
  const { existing, port } = await resolveMetroPort({
    preferredPort: requestedPort,
    strictPort: explicit,
  });

  const env = {
    ...process.env,
    RCT_METRO_PORT: String(port),
  };
  const reactNativeCli = require.resolve('react-native/cli');
  const args = [
    reactNativeCli,
    'run-ios',
    ...stripPortArgs(cliArgs),
    '--port',
    String(port),
  ];

  if (existing && !hasFlag(cliArgs, '--no-packager')) {
    args.push('--no-packager');
  }

  console.log(
    `[ios] Using Metro on port ${port}${existing ? ' (existing server)' : ''}`,
  );

  const child = spawn(process.execPath, args, {
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
