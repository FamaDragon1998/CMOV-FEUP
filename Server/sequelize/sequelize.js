const Sequelize = require('sequelize')
const UserModel = require('./models/user')
const TransactionModel = require('./models/transaction')

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
      {id: 1, username: "John", email: "cena@email.com", password:"coiso", card_number:123123, card_cvs: 101, total_spent: 10, stored_discount:0},
      {id: 2, username: "Kimbolas", email: "cenas", password:"coiso", card_number:123123, card_cvs: 101, total_spent: 10, stored_discount:0},
    ])
    .catch(error => {
      console.log("error", error);
    })
  })
/*   .then(() => {
    User.bulkCreate([
      {id: 1, username: "John", email: "cenas", password:"coiso", card_number:123123, card_cvs: 101, total_spent: 10, stored_discount:0},
      {id: 2, username: "Kimbolas", email: "cenas", password:"coiso", card_number:123123, card_cvs: 101, total_spent: 10, stored_discount:0},
    ])
    console.log("connection");
  }) */

/* var data = {
  tables: {
    user: [
    
    transaction: [
      {id: 1, voucher: 745747, total_value: 120, flag: true},
      {id: 2, voucher: 111111, total_value: 1, flag: false},
      {id: 3, voucher: null, total_value: 55, flag: false},
    ],
  },
} */

//const Tag = TagModel(sequelize, Sequelize)
/* 
Blog.belongsToMany(Tag, { through: BlogTag, unique: false })
Tag.belongsToMany(Blog, { through: BlogTag, unique: false }) */
//Transaction.belongsTo(User);

module.exports = {
  User,
  //Transaction,
  sequelize
}