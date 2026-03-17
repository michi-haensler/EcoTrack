const { execFileSync } = require('child_process');
const fs = require('fs');
const path = require('path');

const WINDOWS_DRIVE_LETTERS = ['Z', 'Y', 'X', 'W', 'V', 'U', 'T', 'S', 'R', 'Q', 'P'];

function normalizeWindowsPath(value) {
  return path.resolve(value).replace(/[\\/]+$/, '').toLowerCase();
}

function listSubstMappings() {
  try {
    const output = execFileSync('subst', {
      encoding: 'utf8',
      stdio: ['ignore', 'pipe', 'ignore'],
    });

    return output
      .split(/\r?\n/)
      .map((line) => line.trim())
      .filter(Boolean)
      .map((line) => line.match(/^([A-Z]:)\\: => (.+)$/i))
      .filter(Boolean)
      .map(([, drive, target]) => ({
        drive: drive.toUpperCase(),
        target: path.resolve(target.trim()),
      }));
  } catch {
    return [];
  }
}

function ensureShortProjectRoot(projectRoot = process.cwd()) {
  const resolvedProjectRoot = path.resolve(projectRoot);

  if (process.platform !== 'win32' || resolvedProjectRoot.length < 50) {
    return {
      projectRoot: resolvedProjectRoot,
      alias: null,
    };
  }

  const normalizedProjectRoot = normalizeWindowsPath(resolvedProjectRoot);
  const mappings = listSubstMappings();
  const existingMapping = mappings.find(
    (mapping) => normalizeWindowsPath(mapping.target) === normalizedProjectRoot,
  );

  if (existingMapping) {
    return {
      projectRoot: `${existingMapping.drive}\\`,
      alias: existingMapping.drive,
    };
  }

  for (const letter of WINDOWS_DRIVE_LETTERS) {
    const drive = `${letter}:`;

    if (fs.existsSync(`${drive}\\`)) {
      continue;
    }

    try {
      execFileSync('subst', [drive, resolvedProjectRoot], {
        stdio: ['ignore', 'ignore', 'ignore'],
      });

      return {
        projectRoot: `${drive}\\`,
        alias: drive,
      };
    } catch {
      // Try the next drive letter.
    }
  }

  return {
    projectRoot: resolvedProjectRoot,
    alias: null,
  };
}

module.exports = {
  ensureShortProjectRoot,
};
