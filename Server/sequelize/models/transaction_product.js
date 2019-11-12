module.exports = (sequelize, type) => {
    return sequelize.define('TransactionProduct', {
        ProductId: {
            type: type.UUID,
            allowNull: false,
            references: {
              model: 'Products',
              key: 'id'
            }
          },
          TransactionId: {
            type: type.UUID,
            allowNull: false,
            references: {
              model: 'Transactions',
              key: 'id'
            }
          }
    })
}