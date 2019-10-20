var express = require('express');
var router = express.Router();

/* Everything related to Users*/

//Register 
router.post('/register', function(req, res, next) {
  res.send('REGISTER');
});

//Logs in
router.post('/login', function(req, res, next) {
  res.send('respond with a resource');
});

//Returns Unused Vouchers of a User
router.get('/{id}/vouchers', function(req, res, next) {
  res.send('respond with a resource');
});

module.exports = router;
