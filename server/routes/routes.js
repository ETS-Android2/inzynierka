module.exports = app => {
    const temp = require("../controllers/serverController");

    var router = require("express").Router();

    //Create CorpTab if not exists and input data
    router.all("/addCorp", temp.createCorp);

    //Create UsersTab if not exists and input data
    router.all("/addUsers", temp.createUsersAcc);

    //Create temporary png from Base64
    router.all("/checkQR", temp.decodeQR);

    //Check if the user sent a valid qr code that belongs to his/her account
    router.all("/checkQrValidation", temp.checkIfQRIsValid);
   
    //Make request to decrypt secret
    router.all("/request", temp.makeRequest);

    //Change user isLogIn statement
    router.all("/changeIsLoggedStatement", temp.userLogOutOrCloseApp);

    //Check to which corporation user belongs
    router.all("/corpName", temp.findCorpName);

    //Send secret to client
    router.all("/checkForSecret", temp.sendSecretToClient);

    app.use('/SERVER/temp', router);
};