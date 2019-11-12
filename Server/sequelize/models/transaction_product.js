module.exports = (sequelize, type) => {
    return sequelize.define('TransactionProduct', {
        ProductId: {
            type: DataTypes.UUID,
            allowNull: false,
            references: {
              model: 'Product',
              key: 'id'
            }
          },
          TransactionId: {
            type: DataTypes.UUID,
            allowNull: false,
            references: {
              model: 'Group',
              key: 'id'
            }
          }
    })
}