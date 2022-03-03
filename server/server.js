const express = require("express");
const cors = require("cors");
const db = require("./models");
const routes = require("./routes/routes");
const { tutorials } = require("./models");
var serviceAccount = require('./engineeringthesis-f-derbin-firebase-adminsdk-d7lrq-0efa4d63d9.json');
var admin = require("firebase-admin");
const { response } = require("express");

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
});

const app = express();
var corsOptions = {
    origin: "http://localhost:8081"
};
app.use(cors(corsOptions));
db.sequelize.sync();
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
require("./routes/routes")(app);
const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}.`);
});