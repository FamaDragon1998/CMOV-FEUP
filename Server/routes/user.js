var express = require('express');
var router = express.Router();

const User = require('../sequelize/sequelize.js').User;
const Transaction = require('../sequelize/sequelize.js').Transaction;
const Voucher = require('../sequelize/sequelize.js').Voucher;


//Register 
router.post('/register', function(req, res, next) {
  User.create(req.body)
  .then(user => res.json(user))
});

//Logs in
router.post('/login', function(req, res, next) {
  User.findOne({ where: {username: req.body.username, password:req.body.password} })
    .then(user => res.json({user}))
});

//Returns Unused Vouchers of a User
router.get('/vouchers', function(req, res, next) {
  Voucher.findAll({ where: {UserId: req.body.UserId, used:false} })
  .then(vouchers => res.json(vouchers))
}); 

//Returns all transactions of a user
router.post('/transactionsAll', function(req, res, next) {
  console.log(req.body[0]);
  Transaction.findAll({ where: {UserId: req.body[0]} })
    .then(transactions => res.json(transactions))
});

//Returns info of User
router.get('/:name', function(req, res, next) {
    User.findOne({ where: {username: req.params.name} })
    .then(users => res.json(users))
});

//Return a transaction of a user
router.get('/transactions/:id', function(req, res, next) {
  Transaction.findOne({ where: {UserId: req.body.UserId, id: req.params.id} })
    .then(transactions => res.json(transactions))
});

//Checkout basket

//productId, value | productId, value , voucherId, valueOfDiscount
router.post('/checkout', function(req, res, next) {
  res.send('respond with a resource');
});


module.exports = router;