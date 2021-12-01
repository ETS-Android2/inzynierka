module.exports = {
    HOST: "localhost",
    USER: "filip",
    PASSWORD: "admin",
    DB: "filip",
    dialect: "postgres",
    pool: {
        max: 5,
        min: 0,
        acquire: 30000,
        idle: 10000
    }
};