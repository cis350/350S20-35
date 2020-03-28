var express = require('express');
var app = express();


var MongoClient = require('mongodb').MongoClient;
var url = "mongodb+srv://tannera:cis350squad@pennbuddies-aavlp.mongodb.net/test?retryWrites=true&w=majority";

MongoClient.connect(url, function(err, db) {
  if (err) throw err;
  var dbo = db.db("people");
  var alexandra = {
    username: "alexandra",
    password: "tanner",
    id: "12345678",
    name: { "first": "Alexandra", "last": "Tanner" },
    phone: "3011234567",
    email: "tannera@seas.upenn.edu",
    birthdate: "04-06-2000",
    height: { "feet": "5", "inches": "7" },
    weight: "50",
    hair: "brown",
    eyes: "hazel"
  };
  var divya = {
    username: "divya",
    password: "somayajula",
    id: "12121212",
    name: { "first": "Divya", "last": "Somayajula" },
    phone: "7031234567",
    email: "divyas22@seas.upenn.edu",
    birthdate: "07-04-2000",
    height: { "feet": "5", "inches": "5" },
    weight: "50",
    hair: "black",
    eyes: "brown"
  }

  var myobjs = [ alexandra , divya ];

  var obj = db.db("Hours");
  var huntsman = {
    name: "Jon M. Huntsman Hall",
    hours: "7 am - 2 am",
  };
  var drl = {
    name: "David Rittenhouse Laboratory",
    hours: "idk have to update",
  }

  var myobjs = [ huntsman , drl ];



  dbo.collection("user").insertMany(myobjs, function(err, res) {
    if (err) throw err;
    console.log("Number of documents inserted: " + res.insertedCount);
    db.close();
  });

  obj.collection("building").insertMany(myobjs, function(err, res) {
    if (err) throw err;
    console.log("Number of documents inserted: " + res.insertedCount);
    db.close();
  });

});

//renders login
app.use('/home', (req, res) => {
  res.render('main.ejs', {req: req, message: null});
});

//renders signup page
app.use('/signup', (req, res) => {
  res.render('signup.ejs', {req: req, message: null});
});

//checks if username is taken, if not signs user up
app.use('/checksignup', (req, res) => {
  res.render('signup.ejs', {req: req, message: null});
});

//renders homepage
app.use('/checklogin', (req, res) => {
  res.render('homepage.ejs', {req: req, message: null});
});

//renders building hours page
app.use('/buildinghours', (req, res) => {
  res.render('buildinghours.ejs', {req: req, message: null});
});

//renders building hours page
app.use('/campusresourceshours', (req, res) => {
  res.render('campusresourceshours.ejs', {req: req, message: null});
});


// This starts the web server on port 3000.
app.listen(3000, () => {
    console.log('Listening on port 3000');
});