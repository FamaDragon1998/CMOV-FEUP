module.exports = (sequelize, type) => {
    return sequelize.define('Voucher', {
        id: {
            type: type.UUID,
            primaryKey: true
        },
        flag: {
            type: type.BOOLEAN,
            allowNull: false,
            defaultValue: false
        }

    })
}