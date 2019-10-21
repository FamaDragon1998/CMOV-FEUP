var express = require('express');
var router = express.Router();

/* Everything related to Products*/

//Returns information of a product 
router.get('/:id', function(req, res, next) {
  res.send('respond with product info');
});

//Logs in
router.post('/user/login', function(req, res, next) {
  res.send('respond with a resource');
});

//Returns Transactions of a User
router.get('/user/:id/transactions', function(req, res, next) {
  res.send('respond with a resource');
});

//Returns Unused Vouchers of a User
router.get('/user', function(req, res, next) {
  res.send('respond with a resource');
});

module.exports = router;
