const http = require('http');
const crypto = require('crypto');
var express = require('express');
var bodyParser = require('body-parser');
const { json } = require('express');
var app = express();

app.use(bodyParser.json());
app.post('/', (req, res) => {
    console.log(req.body);
    const arrayOfC = Array.from({
        length: req.body.howMany
    }, () => encrypt(req.body.secret));
    console.log(arrayOfC);
    const encryptedText = encrypt(req.body.secret);
    res.status(200).json({
        arrayOfC
    })
})
app.listen(8080, () => {
    console.log("Server stoi na porcie 8080");
})
const app = express();

const db = require("./app/models");
db.sequelize.sync();

function encrypt(text) {
    let textOutput = "";
    const algorithm = 'aes-256-cbc';
    const key = crypto.randomBytes(32);
    const iv = crypto.randomBytes(16);
    let cipher = crypto.createCipheriv(algorithm, Buffer.from(key),
        iv);
    let encrypted = cipher.update(text);
    encrypted = Buffer.concat([encrypted, cipher.final()]);
    textOutput = encrypted.toString('hex');
    return textOutput;
}