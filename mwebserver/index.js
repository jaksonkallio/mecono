const express = require('express');
const app = express()
const port = 1982

app.get('/test', (req, res) => {
	res.send({online: true});
});

app.get('/mecono/:node_address/:endpoint', (req, res) => {
	res.send({
		node_address: req.params.node_address,
		endpoint: req.params.endpoint
	});
});

app.listen(port, () => console.log("MWebServer running on port " + port))
