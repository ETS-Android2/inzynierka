module.exports = app => {
    const temp = require("../controllers/controller.js");

    var router = require("express").Router();

    // Create UserTab if not exists and input data
    router.all("/addUser", temp.createUser);

    // Create QrTab if not exists and input data
    router.all("/addValue", temp.addToQR);
    
    // Retrieve all temp
    router.get("/", temp.findAll);

    // Retrieve a single Tutorial with id
    router.get("/:login", temp.findOne);

    // Check if sent user data is valid
    router.all("/acc", temp.findOne);

    // router.all("/test", temp.test);

    app.use('/SERVER/temp', router);
};