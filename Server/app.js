var createError = require('http-errors');
var express = require('express');
const bodyParser = require('body-parser')

var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');
//var db = require('./database/db')


var indexRouter = require('./routes/index');
var userRouter = require('./routes/user');
var productRouter = require('./routes/product');
var transactionRouter = require('./routes/transaction');

const { User} = require('./sequelize/sequelize')

 var app = express();
/*
var data = {
  tables: {
    user: [
     {id: 1, username: "John", email: "cenas", password:"coiso", card_number:123123, card_cvs: 101, total_spent: 10, stored_discount:0},
     {id: 2, username: "Kimbolas", email: "cenas", password:"coiso", card_number:123123, card_cvs: 101, total_spent: 10, stored_discount:0},    ],
    transaction: [
      {id: 1, voucher: 745747, total_value: 120, flag: true},
      {id: 2, voucher: 111111, total_value: 1, flag: false},
      {id: 3, voucher: null, total_value: 55, flag: false},
    ],
  },
}

db.connect(db.MODE_PRODUCTION, function() {
  db.fixtures(data, function(err) {
    if (err) return console.log(err)
    console.log('Data has been loaded...')
  })
}) */

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'pug');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(bodyParser.json())
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', indexRouter);
app.use('/user', userRouter);
app.use('/product', productRouter);
app.use('/transaction', transactionRouter);

/* db.connect(db.MODE_PRODUCTION, function(err) {
  if (err) {
    console.log('Unable to connect to MySQL.')
    process.exit(1)
  } else {
    app.listen(3306, function() {
      console.log('Listening on port 3306...')
    })
  }
}) */


// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});


module.exports = app;
