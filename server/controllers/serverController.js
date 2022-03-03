const db = require("../models");
const express = require("express");
const User = db.users;
const Corp = db.corp;
const RequestTable = db.requestTab;
const Op = db.Sequelize.Op;
const crypto = require('crypto');
const QRCode = require('qrcode');
const QrCodeDecoder = require('qrcode-reader');
let admin = require("firebase-admin");
const fs = require('fs');
const jimp = require('jimp');
const cronNode = require('node-cron');
const CronJob = require('cron').CronJob;
const { resolve } = require("path");
const OneSignal = require('onesignal-node');
const { text } = require("express");
const client = new OneSignal.Client('7ad6bdb1-1e9a-4380-9945-1d1dfc4fdf52', 'Zjc1MTM3ODYtZGJjYi00YWUzLTgzMzItOWZjNGJmOGNjYmU1');
let currentCorp, currentCorpName, currentCorpID, currentUser, currentUserID, accThreshold, j = 0;

// Create AES keys
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
};
const createQrCode = async (str) => await QRCode.toDataURL(str);
// Create corp accunt
exports.createCorp = (req, res) => {
    if (!req.body.corpName && req.body.secret
        && req.body.howManyUsers && req.body.thresholdToDecode) {
        res.status(400).send({
            message: `.`
        });
        return;
    }
    const corp = {
        corpName: req.body.corpName,
        secret: req.body.secret,
        howManyUsers: req.body.howManyUsers,
        thresholdToDecode: req.body.thresholdToDecode
    };

    Corp.create(corp)
        .then(data => {
            currentCorp = data.id;
            currentCorpName = data.corpName;
            accThreshold = data.thresholdToDecode;
            res.send(data);
        }).catch(err => {
            res.status(500).send({
                message:
                    err.message || `.`
            });
        });
};
// Create request to decode secret
exports.createRequest = (req, res) => {
    if (!req.body.userID && req.body.acceptanceThreshold
        && req.body.counter && req.body.uIDOrg) {
        res.status(400).send({
            message: `.`
        });
        return;
    }
    const reqTab = {
        userID: req.body.userID,
        userIDOrganization: req.body.uIDOrg,
        acceptanceThreshold: req.body.acceptanceThreshold,
        counter: req.body.counter
    };
    RequestTable.create(reqTab)
        .then(data => {
            res.send(data);
        }).catch(err => {
            res.status(500).send({
                message:
                    err.message || `.`
            });
        });
};
// Create user accounts and attach AES keys for them
exports.createUsersAcc = (req, res) => {
    if (!req.body.corpName && req.body.secret && req.body.howManyUsers) {
        res.status(400).send({
            message: `.`
        });
        return;
    }
    const arrayOfKeys = Array.from(
        {
            length: req.body.howManyUsers
        },
        () => encrypt(req.body.secret)
    );
    let arrayOfQrs = [];
    let usersAcc = [];
    createArrayOfQrs(arrayOfKeys).then((arr) => {
        arrayOfQrs = [...arr];
        let step, users;
        for (step = 0; step < req.body.howManyUsers; step++) {
            users = {
                login: req.body.corpName + "_" + step,
                password: req.body.corpName + "_" + step,
                aesFromSecret: arrayOfKeys[step],
                organizationID: currentCorp,
                isUserLogIn: req.body.isLogin
            }
            usersAcc.push(users.login);
            User.create(users)
                .catch(err => {
                    res.status(500).send({
                        message:
                            err.message || `.`
                    });
                });
        }
        res.status(200).json({
            arrayOfQrs, usersAcc
        })
    })
};
const createArrayOfQrs = async (array) =>
    await Promise.all(array.map(async (key) => await QRCode.toDataURL(key)));
//Function that decode QRcode from filePath
async function run(PIC_PATH) {
    const img = await jimp.read(fs.readFileSync(PIC_PATH));
    const qr = new QrCodeDecoder();
    const value = await new Promise((resolve, reject) => {
        qr.callback = (err, v) => err != null ? reject(err) : resolve(v);
        qr.decode(img.bitmap);
    });
    return value.result;
};
// Find a user who belongs to the corporation  
exports.findCorpName = (req, res) => {
    const login = req.body.login;
    const password = req.body.password;
    let isUserLogged = req.body.isLogged;
    let currentUserIDOrganization;
    User.findOne({
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
            currentUser = data.login;
            currentUserID = data.id;
            currentUserIDOrganization = data.organizationID;
            db.users.update({
                isUserLogIn: isUserLogged
            }, {
                where: {
                    login: login,
                    password: password
                }
            });
            Corp.findOne({
                where: {
                    id: {
                        [Op.eq]: currentUserIDOrganization
                    }
                }
            }).then(data => {
                currentCorpName = data.corpName;
                accThreshold = data.thresholdToDecode;
                currentCorpID = data.id;
                res.status(200).json({
                    currentCorpName, currentUserID, currentCorpID, currentUserIDOrganization
                })
            })
        } else {
            res.status(404).send({
                message: `.`
            });
        }
    }).catch(err => {
        res.status(500).send({
            message: `.`
        });
    });
};
// Change statement in database if user is login or not
exports.userLogOutOrCloseApp = (req, res) => {
    const login = req.body.login;
    const password = req.body.password;
    let isUserLogged = req.body.isLogged;
    User.findOne({
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
            db.users.update({
                isUserLogIn: isUserLogged
            }, {
                where: {
                    login: login,
                    password: password
                }
            });
        } else {
            res.status(404).send({
                message: `.`
            });
        }
    }).catch(err => {
        res.status(500).send({
            message: `.`
        });
    });
};
// Find a single user with an login
exports.findOne = (req, res) => {
    const login = req.body.login;
    const password = req.body.password;
    User.findOne({
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
                message: `.`
            });
        }
    }).catch(err => {
        res.status(500).send({
            message: `.`
        });
    });
};
// function to create file from base64 encoded string
exports.checkIfQRIsValid = (req, reso) => {
    run('./result.png').then(res => {
        User.findOne({
            where: {
                aesFromSecret: {
                    [Op.eq]: res
                },
                login: {
                    [Op.eq]: req.body.login
                },
                organizationID: {
                    [Op.eq]: req.body.userOrgID
                }
            }
        }).then(data => {
            if (data) {
                let tempUserID = data.id;
                if (req.body.tNumber == 0) deleteImage();
                if (req.body.tNumber == 1) {
                    RequestTable.findOne({
                        where: {
                            id: req.body.reqID,
                            userID: data.id,
                            userIDOrganization: req.body.userOrgID
                        }
                    }).then(data => {
                        let temp = data.acceptanceThreshold;
                        if (data.userIDOrganization == req.body.userOrgID) {
                            temp -= 1;
                            db.requestTab.update({
                                acceptanceThreshold: temp
                            }, {
                                where: {
                                    userID: tempUserID,
                                    userIDOrganization: data.userIDOrganization
                                }
                            }).then(result => {
                                deleteImage();
                            }).catch(err => {
                                console.error(err);
                            });
                        }
                    }).catch(err => {
                        console.error(err);
                    });
                }
                reso.status(200).json({
                    message: '.'
                });
            }
        }).catch(err => {
            console.error(err);
        });
    });
};
// After users send their QRcodes, they will be decoded and after that images will be deleted
function deleteImage() {
    try {
        fs.unlinkSync('./result.png');
    } catch (err) {
        console.error(err);
    }
}
// Decode QRcode from created temporary file
exports.decodeQR = (req, res) => {
    let base64Data = req.body.picPath;
    base64Data.replace(/^data:image\/png;base64,/, "");
    fs.writeFile("./result.png", base64Data, 'base64', function (err) {
    });
};
// Create request and send notification to user that belongs to the same organization
exports.makeRequest = (req, res) => {
    let currentRequestID;
    Corp.findOne({
        where: {
            id: req.body.corpID
        }
    }).then(data => {
        const ReqTab = {
            userID: req.body.userID,
            userIDOrganization: req.body.uIDOrg,
            acceptanceThreshold: data.thresholdToDecode,
            counter: 60
        };
        let tempCorpName = data.corpName;
        let reqUserID = req.body.userID;
        let tempCorpID = data.id;
        RequestTable.create(ReqTab)
            .then(data => {
                currentRequestID = data.id;
                cronTask60(currentRequestID, reqUserID);
                sendPushNotification(tempCorpName);
                res.status(200).json({
                    tempCorpID, currentRequestID
                });
            }).catch(err => {
                console.error(err);
            });
    }).catch(err => {
        console.error(err);
    })
};
// Clients use this to check if everyone sends their QRcode
exports.sendSecretToClient = (req, res) => {
    let secret = '';
    let id;
    if (j == 1 && currentUserID == req.body.userID) {
        User.findOne({
            where: {
                id: req.body.userID
            }
        }).then(data => {
            Corp.findOne({
                where: {
                    id: data.organizationID
                }
            }).then(data => {
                secret = data.secret;
                id = data.id;
                res.status(200).json({
                    secret, id
                });
            }).catch(err => {
                console.error(err);
            })
        }).catch(err => {
            console.error(err);
        })
    } else {
        res.status(200).json({
            message: 'There is still someone who didint send QRcode.'
        })
    }
};
// Function that run task ( 60 min ), update request table every 1 min and decrease counter 
function cronTask60(curReqID, curReqUserID) {
    let i = 60;
    j = 0;
    let job = new CronJob('0 */1 * * * *', function () {
        RequestTable.findOne({
            where: {
                userID: curReqUserID,
                id: curReqID
            }
        }).then(data => {
            let tempAcceptanceThreshold = data.acceptanceThreshold;
            if (i > 0 && tempAcceptanceThreshold > 0) {
                i -= 1;
                console.log("Task will run for : " + i + " minutes.");
                db.requestTab.update({
                    counter: i
                }, {
                    where: {
                        id: curReqID,
                        userID: curReqUserID
                    }
                }).catch(err => {
                    console.error(err);
                });
            } else {
                db.requestTab.destroy({
                    where: {
                        id: curReqID,
                        userID: curReqUserID
                    }
                });
                j = 1;
                currentUserID = curReqUserID;
                job.stop();
            }
        }).catch(err => {
            console.error(err);
        })
    }, null, true);
};
// Function that send push notification to devices
function sendPushNotification(text) {
    return new Promise((res, rej) => {
        const notification = {
            headings: {
                'en': text
            },
            contents: {
                'en': 'Click Accept and choose your QR to decode company secret or Click Deny to do nothing.'
            },
            included_segments: ['Subscribed Users'],
            buttons:
                [{
                    'id': 'id1',
                    'text': 'Accept'
                },
                {
                    'id': 'id2',
                    'text': 'Deny'
                }]
        };
        try {
            const response = client.createNotification(notification);
        } catch (e) {
            if (e)
                console.log(e);
        }
    })
};