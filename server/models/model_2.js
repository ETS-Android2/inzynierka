module.exports = (sequelize, DataTypes) => {
    const QrTab = sequelize.define("secTab", {
        id: {
            type: DataTypes.INTEGER,
            primaryKey: true,
            autoIncrement: true
        },
        secret: {
            type: DataTypes.STRING,
            allowNull: false
        },
        keyValue: {
            type: DataTypes.STRING,
            allowNull: false
        },
        minThre: {
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

    return QrTab;
};