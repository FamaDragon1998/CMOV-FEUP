var express = require('express');
var router = express.Router();

const {User} = require('../sequelize/models/User')

/* Everything related to Users*/

//Register 
router.post('/register', function(req, res, next) {
  User.create(req.body)
  .then(user => res.json(user))
});

//Logs in
router.post('/login', function(req, res, next) {
  res.send('respond with a resource');
});

//Returns Unused Vouchers of a User
router.get('/:id/vouchers', function(req, res, next) {
  res.send('respond with a voucher');
}); 

//Returns info of User
router.get('/:name', function(req, res, next) {
    User.findAll({ where: {name: req.params.name} })
    .then(users => res.json(users))
  });

module.exports = router;
