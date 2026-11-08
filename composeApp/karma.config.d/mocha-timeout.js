// The browser-back test waits on real history navigation; mocha's 2 s default is too tight.
config.client = config.client || {};
config.client.mocha = Object.assign(config.client.mocha || {}, { timeout: 10000 });
