var express = require('express');
var router = express.Router();

const User = require('../sequelize/sequelize.js').User;
const Transaction = require('../sequelize/sequelize.js').Transaction;


//Register 
router.post('/register', function(req, res, next) {
router.get('/:id/vouchers', function(req, res, next) {

//Returns info of User
router.get('/:name', function(req, res, next) {
    User.findOne({ where: {username: req.params.name} })
    .then(users => res.json(users))
});

//Returns all transactions of a user
router.get('/transactions', function(req, res, next) {
  Transaction.findOne({ where: {user_id: req.body.user_id} })
    .then(transactions => res.json(transactions))
});

//Return a transaction of a user
router.get('/transactions/:id', function(req, res, next) {
  Transaction.findOne({ where: {user_id: req.body.user_id, transaction_id: req.params.id} })
    .then(transactions => res.json(transactions))
});

//Checkout basket
router.post('/checkout', function(req, res, next) {
  res.send('respond with a resource');
});


module.exports = router;