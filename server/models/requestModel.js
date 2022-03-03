module.exports = (sequelize, DataTypes) => {
    const RequestTable = sequelize.define("requestTable", {
        id: {
            type: DataTypes.INTEGER,
            primaryKey: true,
            autoIncrement: true
        },
        userID: {
            type: DataTypes.INTEGER,
            allowNull: false
        },
        userIDOrganization: {
            type: DataTypes.INTEGER,
            allowNull: false
        },
        acceptanceThreshold: {
            type: DataTypes.INTEGER,
            allowNull: false
        },
        counter: {
            type: DataTypes.INTEGER,
            allowNull: false
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

    return RequestTable;
};