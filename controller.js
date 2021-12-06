const db = require("../models");
const Tutorial = db.tutorials;
const QrTab = db.qrtabs;
const Op = db.Sequelize.Op;
const crypto = require('crypto');
var QRCode = require('qrcode');
var currentUser;
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
// Create and Save a new User
exports.createUser = (req, res) => {
    // Validate request
    if (!req.body.login && req.body.password) {
        res.status(400).send({
            message: "Content can not be empty!"
        });
        return;
    }
    const user = {
        login: req.body.login,
        password: req.body.password,
    };
    Tutorial.create(user)
        .then(data => {
            res.send(data);
        })
        .catch(err => {
            res.status(500).send({
                message:
                    err.message || "Some error occurred while creating the UserTab."
            });
        });
};
exports.addToQR = (req, res) => {
    const arrayOfKeys = Array.from({
        length: req.body.howMany
    }, () => encrypt(req.body.sekret));
    let strData = JSON.stringify(arrayOfKeys);
    console.log("This is strData: " + strData);

    const arrayOfQr = [];
    for (var i = 0; i < req.body.howMany; i++) {
        QRCode.toDataURL(arrayOfKeys[i], function (err, url) {
            arrayOfQr.push(url.toString());
            console.log(arrayOfQr);
        })
    }
    console.log(arrayOfQr);
    var step, qrtab;
    for (step = 0; step < req.body.howMany; step++) {
        qrtab = {
            secret: req.body.sekret,
            keyValue: arrayOfKeys[step],
            authorId: currentUser,
            minThre: req.body.minThreshold,
        }
        console.log(qrtab);
        QrTab.create(qrtab)
            .then(data => {
                //res.send(data);
                //console.log(data);
            })
            .catch(err => {
                res.status(500).send({
                    message:
                        err.message || "Some error occurred while creating the UserTab."
                }); console.log(err);
            });
    }

    res.status(200).json({

    });
};
// Retrieve all Tutorials from the database.
exports.findAll = (req, res) => {
    const login = req.query.login;
    var condition = login ? { login: { [Op.like]: `%${login}%` } } : null;

    Tutorial.findAll({ where: condition })
        .then(data => {
            res.send(data);
        })
        .catch(err => {
            res.status(500).send({
                message:
                    err.message || "Some error occurred while retrieving users."
            });
        });
};

// Find a single user with an login
exports.findOne = (req, res) => {
    const login = req.body.login;
    const password = req.body.password;

    Tutorial.findOne({
        where: {
            login: {
                [Op.eq]: login
            },
            password: {
                [Op.eq]: password
            }
        }
    }).then(data => {
        if (data) {
            currentUser = data.id
            res.send(data);
        } else {
            res.status(404).send({
                message: `Cannot find User with login=${login}.`
            });
        }
    }).catch(err => {
        res.status(500).send({
            message: "Error retrieving User with login=" + login
        });
    });
};

// exports.test = (req, res) => {
//     const inputtedText = req.body.test;

//     QRCode.toDataURL(inputtedText, function (err, url) {
//         console.log(url);
//         console.log(err);
//         res.status(200).json({
//             url
//         });
//       });
// }