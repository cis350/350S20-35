var express = require('express');
var bodyParser = require('body-parser');
var session = require('express-session');
var app = express();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }))
app.use(bodyParser.raw({ type: 'application/vnd.custom-type' }));
app.use(bodyParser.text({ type: 'text/html' }));
app.use(session({resave: true, secret:"top-secret", saveUninitialized: true}));

var MongoClient = require('mongodb').MongoClient;
var url = "mongodb+srv://tannera:cis350squad@pennbuddies-aavlp.mongodb.net/test?retryWrites=true&w=majority";

var database;

MongoClient.connect(url, function(err, db) {
  if (err) throw err;
  database = db;

  //the code was adding the buildings to the database everytime I run it so I commented it out

  // var huntsman = {
  //   name: "Jon M. Huntsman Hall",
  //   hours: "7 am - 2 am",
  // };
  // var drl = {
  //   name: "David Rittenhouse Laboratory",
  //   hours: "idk have to update",
  // }

  // var myobjs2 = [ huntsman , drl ];
  //
  // dbo.collection("user").insertMany(myobjs, function(err, res) {
  //   if (err) throw err;
  //   console.log("Number of documents inserted: " + res.insertedCount);
  //   db.close();
  // });
  //
  // obj.collection("building").insertMany(myobjs2, function(err, res) {
  //   if (err) throw err;
  //   console.log("Number of documents inserted: " + res.insertedCount);
  //   db.close();
  // });

});

//renders homepage
app.get('/', (req, res) => {
  if (req.session.loggedIn) {
    res.render('homepage.ejs', {req: req, message: null});
  } else {
    res.render('main.ejs', {req: req});
  }
});

//renders signup page
app.get('/signup', (req, res) => {
  res.render('signup.ejs', {req: req, message: null});
});

//checks if username is taken, if not signs user up
app.post('/checksignup', (req, res) => {
  people = database.db("people");

  var query = { username: req.body.username };
  var id = req.body._id;
  var username = req.body.username;
  var password = req.body.password;
  var name = req.body.name;
  var phone = req.body.phone;
  var email = req.body.email;

  people.collection("user").find(query).toArray(function(err, result) {
    if (err) {
      throw err;
    } else if (result == "" && id != "" && username != "" && password != "" && name != "" && phone != "" && email != "") {
      people.collection("user").insertOne(req.body, function(err, result2) {
        if (err) throw err;
        console.log(username + " signed up");
        req.session.loggedIn = true;
    	  req.session.currUser = username;
        res.render('homepage.ejs', {req: req, message: null});
      });
    } else {
      console.log(username + " username taken");
      res.redirect("/signup?message=User already exists or not all fields inputted.");
    }
  });
});

//renders homepage if login is correct
app.post('/checklogin', (req, res) => {
  people = database.db("people");

  var query = { username: req.body.username,password: req.body.password };
  var username = req.body.username;

  people.collection("user").find(query).toArray(function(err, result) {
    if (err) {
      throw err;
    } else if (result != "") {
      console.log(username + " logged in");
      req.session.loggedIn = true;
      req.session.currUser = username;
      res.render('homepage.ejs', {req: req, message: null});
    } else {
      res.redirect("/?message=Incorrect Username or Password");
    }
  });
});

//renders edit profile page
app.use('/editprofile', (req, res) => {
  res.render('editprofile.ejs', {req: req, message: null});
});

//renders building hours page
app.use('/buildinghours', (req, res) => {
  res.render('buildinghours.ejs', {req: req, message: null});
});

//renders building hours page
app.use('/campusresourceshours', (req, res) => {
  res.render('campusresourceshours.ejs', {req: req, message: null});
});

//logs out
app.get('/logout', (req, res) => {
  req.session.loggedIn = false;
  req.session.currUser = null;
  res.redirect("/");
});

// This starts the web server on port 3000.
app.listen(3000, () => {
    console.log('Listening on port 3000');
});
