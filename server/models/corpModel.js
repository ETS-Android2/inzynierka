module.exports = (sequelize, DataTypes) => {
    const Corp = sequelize.define("corp", {
        id: {
            type: DataTypes.INTEGER,
            primaryKey: true,
            autoIncrement: true
        },
        corpName: {
            type: DataTypes.STRING,
            allowNull: false
        },
        secret: {
            type: DataTypes.STRING,
            allowNull: false
        },
        howManyUsers: {
            type: DataTypes.INTEGER,
            allowNull: false
        },
        thresholdToDecode: {
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

    return Corp;
};