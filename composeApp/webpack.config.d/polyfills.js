const webpack = require('webpack');

config.resolve.fallback = {
	...config.resolve.fallback,
	os: require.resolve('os-browserify/browser'),
	path: require.resolve('path-browserify'),
	fs: false
};
