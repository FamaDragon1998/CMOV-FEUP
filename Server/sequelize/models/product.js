module.exports = (sequelize, type) => {
    return sequelize.define('Product', {
        id: {
            type: type.UUID,
            primaryKey: true
        },
        name:{
            type: type.STRING,
            allowNull: true
        },
        value:{
            type: type.FLOAT,
            allowNull: false
        }
    })
}