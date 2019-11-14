var express = require('express');
var router = express.Router();
const Product = require('../sequelize/sequelize.js').Product;
const TransactionProduct = require('../sequelize/sequelize.js').TransactionProduct;
const sequelize = require('../sequelize/sequelize.js').sequelize;

/* Everything related to Products and Products in Transactions*/

//Finds all products in a transaction
router.post('/transaction', function(req, res, next) {
    let query = "SELECT name, value FROM Products WHERE id in (Select ProductId from TransactionProducts WHERE TransactionId = :id)";
    console.log(req.body);
    sequelize.query(query, { replacements: { id: req.body.TransactionId } })
    .then(([results, metadata]) => {
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
