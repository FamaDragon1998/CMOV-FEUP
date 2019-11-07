module.exports = (sequelize, type) => {
    return sequelize.define('User', {
        id: {
            type: type.UUID,
            primaryKey: true,
            autoIncrement: true
        },
        username: {
            type: type.STRING,
            allowNull: false,
            unique: true
        },
        email:{
            type: type.STRING,
            allowNull: false,
            unique: true
        },
        password:{
            type: type.STRING,
            allowNull: false
        },
        card_number:{
            type: type.BIGINT,
            allowNull: false
        },
        card_cvs:{
            type: type.STRING,
            allowNull: false
        },
        total_spent:{
            type: type.FLOAT,
            allowNull: false
        },
        stored_discount:{
            type: type.FLOAT,
            allowNull: false,
            defaultValue: 0
        }

    })
}