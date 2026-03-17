const http = require('http');
const net = require('net');

const DEFAULT_PORT = 8088;
const PORT_RANGE_SIZE = 10;
const METRO_STATUS_PATH = '/status';
const METRO_STATUS_TEXT = 'packager-status:running';
const REQUEST_TIMEOUT_MS = 300;

function normalizePort(value) {
  const port = Number(value);

  if (!Number.isInteger(port) || port <= 0 || port > 65535) {
    return null;
  }

  return port;
}

function parsePortArg(argv) {
  for (let index = 0; index < argv.length; index += 1) {
    const arg = argv[index];

    if (arg === '--port') {
      return {
        explicit: true,
        port: normalizePort(argv[index + 1]),
      };
    }

    if (arg.startsWith('--port=')) {
      return {
        explicit: true,
        port: normalizePort(arg.slice('--port='.length)),
      };
    }
  }

  return {
    explicit: false,
    port: null,
  };
}

function stripPortArgs(argv) {
  const nextArgs = [];

  for (let index = 0; index < argv.length; index += 1) {
    const arg = argv[index];

    if (arg === '--port') {
      index += 1;
      continue;
    }

    if (arg.startsWith('--port=')) {
      continue;
    }

    nextArgs.push(arg);
  }

  return nextArgs;
}

function hasFlag(argv, ...flags) {
  return argv.some((arg) => flags.includes(arg));
}

function getCandidatePorts(preferredPort) {
  const ports = [];
  const preferred = normalizePort(preferredPort);

  if (preferred != null) {
    ports.push(preferred);
  }

  for (let offset = 0; offset < PORT_RANGE_SIZE; offset += 1) {
    ports.push(DEFAULT_PORT + offset);
  }

  return ports.filter((port, index) => ports.indexOf(port) === index);
}

function isMetroRunning(port) {
  return new Promise((resolve) => {
    const request = http.get(
      {
        host: '127.0.0.1',
        path: METRO_STATUS_PATH,
        port,
        timeout: REQUEST_TIMEOUT_MS,
      },
      (response) => {
        let body = '';

        response.setEncoding('utf8');
        response.on('data', (chunk) => {
          body += chunk;
        });
        response.on('end', () => {
          resolve(
            response.statusCode === 200 && body.includes(METRO_STATUS_TEXT),
          );
        });
      },
    );

    request.on('error', () => {
      resolve(false);
    });

    request.on('timeout', () => {
      request.destroy();
      resolve(false);
    });
  });
}

function isPortFree(port) {
  return new Promise((resolve) => {
    const server = net.createServer();

    server.unref();

    server.once('error', () => {
      resolve(false);
    });

    server.listen(port, '127.0.0.1', () => {
      server.close(() => {
        resolve(true);
      });
    });
  });
}

async function resolveMetroPort(options = {}) {
  const candidatePorts = getCandidatePorts(options.preferredPort);
  const strictPort = options.strictPort === true;

  if (strictPort) {
    const [preferredPort] = candidatePorts;

    if (preferredPort == null) {
      throw new Error('The provided Metro port is invalid.');
    }

    if (await isMetroRunning(preferredPort)) {
      return { existing: true, port: preferredPort };
    }

    if (await isPortFree(preferredPort)) {
      return { existing: false, port: preferredPort };
    }

    throw new Error(`Port ${preferredPort} is already in use by another process.`);
  }

  for (const port of candidatePorts) {
    if (await isMetroRunning(port)) {
      return { existing: true, port };
    }
  }

  for (const port of candidatePorts) {
    if (await isPortFree(port)) {
      return { existing: false, port };
    }
  }

  throw new Error(
    `No free Metro port was found in ${candidatePorts.join(', ')}.`,
  );
}

module.exports = {
  DEFAULT_PORT,
  hasFlag,
  parsePortArg,
  resolveMetroPort,
  stripPortArgs,
};
