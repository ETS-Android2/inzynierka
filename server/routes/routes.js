module.exports = app => {
    const tutorials = require("../controllers/controller.js");

    var router = require("express").Router();

    // Create UserTab if not exists and input data
    router.post("/addUser", tutorials.create);

    // Create QrTab if not exists and input data
    router.post("/addValue", tutorials.createQRTable);
    
    // Retrieve all Tutorials
    router.get("/", tutorials.findAll);

    // Retrieve a single Tutorial with id
    router.get("/:login", tutorials.findOne);

    // Check if sent user data is valid
    router.all("/acc", tutorials.findOne);

    app.use('/SERVER/tutorials', router);
};