var express = require('express');
var router = express.Router();
const Product = require('../sequelize/sequelize.js').Product;
const sequelize = require('../sequelize/sequelize.js').sequelize;

/* Everything related to Products and Products in Transactions*/

//Finds all products in a transaction
router.post('/transaction', function(req, res, next) {
  let query = "SELECT p.name, p.value, tp.count FROM Products p INNER JOIN TransactionProducts tp on tp.ProductId = p.id WHERE tp.TransactionId = :id";
  sequelize.query(query, { replacements: { id: req.body[0].TransactionId } })
    .then(([results, metadata]) => {
      console.log(results);
      res.json(results);  
  })
  .catch(function(err) {
    console.log(err);
  });
});

//Returns information of a product 
router.get('/:id', function(req, res, next) {
  Product.findOne({ where: {id: req.params.id} })
    .then(product => res.json(product))
    .catch(function(err) {
      console.log(err);
    });
});

module.exports = router;
