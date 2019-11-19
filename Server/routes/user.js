var express = require('express');
var router = express.Router();
const sequelize = require('../sequelize/sequelize.js').sequelize;

const User = require('../sequelize/sequelize.js').User;
const Transaction = require('../sequelize/sequelize.js').Transaction;
const Voucher = require('../sequelize/sequelize.js').Voucher;
const TransactionProduct = require('../sequelize/sequelize.js').TransactionProduct;


//Register 
router.post('/register', function(req, res, next) {
  User.create(req.body)
  .then(user => res.json({user}))
  .catch(function(err) {
    console.log(err);
  });
});

//Logs in
router.post('/login', function(req, res, next) {
  User.findOne({ where: {username: req.body.username, password:req.body.password} })
    .then(user => res.json({user}))
    .catch(function(err) {
      console.log(err);
    });
});

//Returns Unused Vouchers of a User
router.post('/vouchers', function(req, res, next) {
  Voucher.findAll({ where: {UserId: req.body[0].UserId, used:false} })
  .then(vouchers => res.json(vouchers))
  .catch(function(err) {
    console.log(err);
  });
}); 

//Returns all transactions of a user
router.post('/transactionsAll', function(req, res, next) {
  Transaction.findAll({ where: {UserId: req.body[0].UserId} })
    .then(transactions => res.json(transactions))
    .catch(function(err) {
      console.log(err);
    });
});

//Returns info of User
router.get('/:name', function(req, res, next) {
    User.findOne({ where: {username: req.params.name} })
    .then(users => res.json(users))
    .catch(function(err) {
      console.log(err);
    });
});

//Return a transaction of a user
router.get('/transactions/:id', function(req, res, next) {
  Transaction.findOne({ where: {UserId: req.body.UserId, id: req.params.id} })
    .then(transactions => res.json(transactions))
    .catch(function(err) {
      console.log(err);
    });
});

router.post('/checkout', function(req, res, next) {
  // Create Transaction
  let transaction = {
      id: create_UUID(),
      discount: req.body.discount,
      UserId: req.body.UserId
  };

  transaction.voucher = req.body.voucher;

  let total_spent = 0;
  req.body.products.forEach(product => {
      total_spent += parseFloat(product.price);
  });
  transaction.total_value = total_spent;

  let usedProducts = new Map();
  Transaction.create(transaction)
      .then(createdTransaction => {
          req.body.products.forEach(product => {
              let count;
              if (!usedProducts.has(product.id))
                count = 1;
              else
                count = usedProducts.get(product.id) + 1;
              usedProducts.set(product.id, count);
          });
          usedProducts.forEach(function(count, id) {
            let trans_prod = {
              id: create_UUID(),
              ProductId: id,
              TransactionId: createdTransaction.id,
              count: count
          };
          TransactionProduct.create(trans_prod);
        });
          // Update voucher if used
          let additive_discount = createdTransaction.total_value - createdTransaction.discount;
          if (req.body.voucher != null) {
              Voucher.update({
                  used: true
              }, {
                  where: {
                      id: req.body.voucher
                  },
                  returning: true, // needed for affectedRows to be populated
              });
              additive_discount *= 0.15;
          }

          let initialUserTotalSpent;
          User.findOne({
                  where: {
                      id: req.body.UserId
                  }
              })
              .then(user => {
                  initialUserTotalSpent = user.total_spent;

                  let query = "UPDATE Users SET total_spent = total_spent + :total, stored_discount = stored_discount + :discount WHERE id = :id";
                  sequelize.query(query, {
                          replacements: {
                              total: createdTransaction.total_value - createdTransaction.discount,
                              discount: additive_discount,
                              id: user.id
                          }
                      })
                      .then(([results, metadata]) => {
                        
                          let finalUserTotalSpent = initialUserTotalSpent + createdTransaction.total_value - createdTransaction.discount;
                          let diff = parseInt((finalUserTotalSpent - initialUserTotalSpent) / 100);

                          if (diff > 0){
                              for (let i = 0; i < diff; i++) {
                                  let voucher = {
                                      id: create_UUID(),
                                      used: false,
                                      UserId: req.body.UserId,
                                      TransactionId: null
                                  }
                                  Voucher.create(voucher);
                              }
                          }
                          res.send({
                              "ACK": finalUserTotalSpent - initialUserTotalSpent
                          });
                      })
              })
      })
      .catch(function(err) {
          console.log(err);
      });


});


function create_UUID(){
  var dt = new Date().getTime();
  var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
      var r = (dt + Math.random()*16)%16 | 0;
      dt = Math.floor(dt/16);
      return (c=='x' ? r :(r&0x3|0x8)).toString(16);
  });
  return uuid;
}


module.exports = router;