module.exports = (sequelize, Sequelize) => {
    const QrTab = sequelize.define("secTab", {
        id: {
            type: Sequelize.INTEGER,
            primaryKey: true,
            autoIncrement: true
        },
        secret: {
            type: Sequelize.STRING,
            allowNull: false
        },
        keyValue: {
            type: Sequelize.STRING,
            allowNull: false
        },
        minThre: {
            type: Sequelize.INTEGER,
            allowNull: false
        },
        createdAt: {
            type: Sequelize.DATE,
            allowNull: true
        },
        updatedAt: {
            type: Sequelize.DATE,
            allowNull: true
        }
    });

    return QrTab;
};