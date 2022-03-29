module.exports = (sequelize, DataTypes) => {
    const Users = sequelize.define("users", {
        id: {
            type: DataTypes.INTEGER,
            primaryKey: true,
            autoIncrement: true
        },
        login: {
            type: DataTypes.STRING,
            allowNull: false
        },
        password: {
            type: DataTypes.STRING,
            allowNull: false
        },
        aesFromSecret: {
            type: DataTypes.STRING,
            allowNull: false
        },
        iv: {
            type: DataTypes.STRING,
            allowNull: false
        },
        organizationID: {
            type: DataTypes.INTEGER,
            allowNull: false
        },
        isUserLogIn: {
            type: DataTypes.BOOLEAN,
            allowNull: true
        },
        createdAt: {
            type: DataTypes.DATE,
            allowNull: true
        },
        updatedAt: {
            type: DataTypes.DATE,
            allowNull: true
        }
    });

    return Users;
};