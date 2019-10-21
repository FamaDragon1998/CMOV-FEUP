var express = require('express');
var router = express.Router();

/* Everything related to Transactions*/

//Returns Transactions of a User
router.get('/:id/transactions', function(req, res, next) {
  res.send('respond with a resource');
});


//Checkout basket
router.post('/checkout', function(req, res, next) {
  res.send('respond with a resource');
});


module.exports = router;
