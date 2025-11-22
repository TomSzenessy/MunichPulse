const CopyWebpackPlugin = require('copy-webpack-plugin');
const path = require('path');

// Adjust the path to point to your src/webMain/resources
// The webpack config is usually run from build/js/packages/MunichPulse-composeApp
const resourcesPath = path.resolve(
	__dirname,
	'../../../../composeApp/src/webMain/resources'
);

const kotlinPath = path.resolve(__dirname, 'kotlin');

config.plugins.push(
	new CopyWebpackPlugin({
		patterns: [
			{
				from: resourcesPath,
				to: '.',
				noErrorOnMissing: true
			},
			{
				from: path.resolve(kotlinPath, 'skiko.wasm'),
				to: '.',
				noErrorOnMissing: true
			}
		]
	})
);
