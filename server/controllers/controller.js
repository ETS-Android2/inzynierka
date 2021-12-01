const db = require("../models");
const Tutorial = db.tutorials;
const QrTab = db.tutorials;
const Op = db.Sequelize.Op;

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

// Create and Save a new Tutorial
exports.create = (req, res) => {
    // Validate request
    if (!req.body.login) {
        res.status(400).send({
            message: "Content can not be empty!"
        });
        return;
    }

    // Create a Tutorial
    const tutorial = {
        login: req.body.login,
        password: req.body.password,
    };
    console.log(tutorial);
    // Save Tutorial in the database
    Tutorial.create(tutorial)
        .then(data => {
            res.send(data);
        })
        .catch(err => {
            res.status(500).send({
                message:
                    err.message || "Some error occurred while creating the UserTab."
            }); console.log(err);
        });
};
exports.createQRTable = (req, res) => {
    if (!req.body.secret && req.body.howMany && req.body.minThreshold) {
        res.status(400).send({
            message: "Content can not be empty!"
        });
        return;
    }
    const arrayOfKeys = Array.from({
        length: req.body.howMany
    }, () => encrypt(req.body.secret));
    res.status(200).json({
        arrayOfKeys
    });

    const qrtab = {
        secret: req.body.sekret,
        keyValue: arrayOfKeys,
        minThre: req.body.minThreshold
    }
    QrTab.createQRTable(qrtab)
        .then(data => {
            res.send(data);
        })
        .catch(err => {
            res.status(500).send({
                message:
                    err.message || "Some error occurred while creating the QrTab",
            }); console.log(err);
        })
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