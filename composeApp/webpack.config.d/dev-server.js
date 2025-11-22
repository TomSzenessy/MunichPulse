if (config.devServer) {
	config.devServer.historyApiFallback = true;
	config.devServer.open = false; // Prevent opening a new tab every time
	config.devServer.hot = true;
	config.devServer.headers = {
		'Access-Control-Allow-Origin': '*',
		'Content-Security-Policy':
			"default-src * 'unsafe-inline' 'unsafe-eval' data: blob:;"
	};
}
