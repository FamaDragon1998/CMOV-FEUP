module.exports = (sequelize, type) => {
    return sequelize.define('Product', {
        id: {
            type: type.UUID,
            primaryKey: true
        },
        name:{
            type: type.UUID,
            allowNull: true
        },
        value:{
            type: type.FLOAT,
            allowNull: false
        }
    })
}