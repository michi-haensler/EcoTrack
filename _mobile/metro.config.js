const fs = require('fs');
const path = require('path');
const { getDefaultConfig, mergeConfig } = require('@react-native/metro-config');

/**
 * Metro configuration
 * https://reactnative.dev/docs/metro
 *
 * @type {import('@react-native/metro-config').MetroConfig}
 */
const escapePathSegment = (value) => value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
const projectRoot = fs.realpathSync.native(__dirname);
const blockedPaths = [
  new RegExp(`[\\\\/]${escapePathSegment('.cxx')}[\\\\/].*`),
  new RegExp(`[\\\\/]${escapePathSegment('CMakeFiles')}[\\\\/]${escapePathSegment('CMakeTmp')}[\\\\/].*`),
  new RegExp(`[\\\\/]android[\\\\/]build[\\\\/].*`),
  new RegExp(`[\\\\/]android[\\\\/]app[\\\\/]build[\\\\/].*`),
];
const blockList = new RegExp(blockedPaths.map((pattern) => `(${pattern.source})`).join('|'));

const config = {
  resolver: {
    blockList,
  },
  watchFolders: [projectRoot],
};

module.exports = mergeConfig(getDefaultConfig(projectRoot), config);
