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
        {id: "b0e76929-9762-45b7-be1f-2f37d2edf33c", username: "John", name: "John Cena", password:"coiso", card_number:123123, card_cvs: 101, total_spent: 0, stored_discount:0},
        {id: "32bf576f-1d83-4141-9009-8d4c6435d10e", username: "Kimbolas", name: "Ricardo Lopes", password:"coiso", card_number:123123, card_cvs: 101, total_spent: 250, stored_discount:40},
      ])
    })
    .then(() => {
      Transaction.bulkCreate([
        {id: "82d7e51b-9b63-4570-bf32-da837ec09981", voucher: "b1a3k3v1-cte1-4ab3-b1ce-67cf4d3935ac", total_value: 220, discount: 10, UserId:"32bf576f-1d83-4141-9009-8d4c6435d10e"},
      ])
    })
      .then(() => {
        Product.bulkCreate([
          {id: "d93402fb-8af4-40e0-8c0d-7a05485405f3", name: "Anvil", value: 40},
          {id: "c19817fe-2b3b-4c48-877e-7ea98f081e74", name: "Aspirin", value: 3},
          {id: "a807813a-70a6-45d1-b27f-42a406ff2321", name: "Birdseeds", value: 2},
          {id: "6b6e76f5-85cf-4b65-9697-979524ae0c19", name: "Glue", value: 5},
          {id: "32b03bd2-14ab-47e5-afe4-88fbef2926fa", name: "Kitekit", value: 20},
          {id: "2f18bb3d-a096-477f-9b54-0ac5d3a32ee1", name: "Matches", value: 1},
          {id: "1fdbb746-cff2-428e-a720-162b3759c6ec", name: "Motorbike", value: 300},
          {id: "5c3d170b-ecc3-4fa2-83a9-651d6110f571", name: "Rollerskates", value: 90},
          {id: "c639744a-b4e4-430d-bcde-9ca85b023273", name: "Toaster", value: 8},
          {id: "ae45ceab-8d16-4336-93f5-1a9082a8de00", name: "Vitamins", value: 4},
          {id: "4ef95bff-74d8-4dc8-ada9-c693fa76b7a9", name: "Invisible Paint", value: 12},

        ])
      })
      .then(() => {
        Voucher.bulkCreate([
          {id: "b1a3k3v1-cte1-4ab3-b1ce-67cf4d3935ac", used:true, UserId:"32bf576f-1d83-4141-9009-8d4c6435d10e", TransactionId:"82d7e51b-9b63-4570-bf32-da837ec09981"},
          {id: "b1a3k3v1-cfd0-4ab3-b1ce-67cf4d3935ac", used:false, UserId:"32bf576f-1d83-4141-9009-8d4c6435d10e", TransactionId:null},

        ])
      })
        .then(() => {
          TransactionProduct.bulkCreate([
            {ProductId: "d93402fb-8af4-40e0-8c0d-7a05485405f3", TransactionId:"82d7e51b-9b63-4570-bf32-da837ec09981", count: 1},
            {ProductId: "5c3d170b-ecc3-4fa2-83a9-651d6110f571", TransactionId:"82d7e51b-9b63-4570-bf32-da837ec09981", count: 2},
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