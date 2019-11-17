const Sequelize = require('sequelize')
const UserModel = require('./models/user')
const TransactionModel = require('./models/transaction')
const VoucherModel = require('./models/voucher')
const ProductModel = require('./models/product')
const TransactionProductModel = require('./models/transaction_product')

const sequelize = new Sequelize('codementor', 'root', 'root', {
  host: 'localhost',
  dialect: 'sqlite',
  pool: {
    max: 10,
    min: 1,
    acquire: 30000,
    idle: 10000
  },
  storage: 'database/database.sqlite'
})

const User = UserModel(sequelize, Sequelize)
const Transaction = TransactionModel(sequelize, Sequelize)
const Voucher = VoucherModel(sequelize, Sequelize);
const Product = ProductModel(sequelize, Sequelize);
const TransactionProduct = TransactionProductModel(sequelize, Sequelize);

User.hasMany(Transaction);
Transaction.belongsTo(User);

User.hasMany(Voucher);
Voucher.belongsTo(User);
Voucher.belongsTo(Transaction);

sequelize
  .authenticate()
  .then(() => {
    console.log("connection");
  })
  .catch(error => {
    console.log("error", error);
  })
  
  sequelize
  .sync({force:true})
    .then(() => {
      User.bulkCreate([
        {id: 1, username: "John", name: "cena@email.com", password:"coiso", card_number:123123, card_cvs: 101, total_spent: 10, stored_discount:0},
        {id: 2, username: "Kimbolas", name: "cenas", password:"coiso", card_number:123123, card_cvs: 101, total_spent: 10, stored_discount:40},
      ])
    })
    .then(() => {
      Transaction.bulkCreate([
        {id: 1, voucher: 745747, total_value: 120, discount: 10, UserId:2},
        {id: 2, voucher: 111111, total_value: 1, discount: 0,UserId:1},
        {id: 4, voucher: null, total_value: 1, discount: 0, UserId:1},

      ])
    })
      .then(() => {
        Product.bulkCreate([
          {id: 1, name: "Explosives", value: 10},
          {id: 2, name: "Yo", value: 1},
          {id: 3, name: "Yo2", value: 3},
        ])
      })
      .then(() => {
        Voucher.bulkCreate([
          {id: 12123, used:true, UserId:1, TransactionId:1},
          {id: 12121212, used:false, UserId:1, TransactionId:null},
          {id: 12, used:false, UserId:2, TransactionId:null},

        ])
      })
        .then(() => {
          TransactionProduct.bulkCreate([
            {ProductId: 1, TransactionId:1},
            {ProductId: 2, TransactionId:1},
            {ProductId: 3, TransactionId:2},
          ])
          })
    .catch(error => {
      console.log("error", error);
    })

module.exports = {
  User,
  Transaction,
  Voucher,
  Product,
  TransactionProduct,
  sequelize
}