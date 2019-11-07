module.exports = (sequelize, type) => {
    return sequelize.define('Transaction', {
        id: {
            type: type.UUID,
            primaryKey: true,
            autoIncrement: true
        },
        voucher:{
            type: type.UUID,
            allowNull: true
        },
        total_value:{
            type: type.FLOAT,
            allowNull: false
        },
        flag: {
            type: type.BOOLEAN,
            allowNull: false,
            defaultValue: false
        }

    })
}