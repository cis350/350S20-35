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
var assert = require('assert');
var url = "mongodb+srv://tannera:cis350squad@pennbuddies-aavlp.mongodb.net/test?retryWrites=true&w=majority";

var database;

MongoClient.connect(url, function(err, db) {
  if (err) throw err;
  database = db;
});

app.use('/queryPassword', (req, res) => {
    var people = database.db("people");

    if (req.query == {}) {
        res.redirect("/editprofile?message=Could not load profile1");
        // res.json("hello");
    }
    else {
        var data = [];
        var username = req.query.id;
        if (username == "") {
            // res.json("problem");
            res.redirect("/editprofile?message=Could not load profile2");
            // data.push("problem");
        }
        else if (username == null) {
            // res.json("problem 2");
            res.redirect("/editprofile?message=Could not load profile3");
            // data.push("problem");
        }
        else {
            people.collection("user").find({email : username}).toArray(function(err, result) {
                if (err) {
                  // throw err;
                  res.redirect("/editprofile?message=Could not load profile4");
                  // data.push({'error': 'could not find user'});
                } else if (result != "") {
                    // res.redirect("/editprofile?password=" + result[0].name);
                  res.json({'password': result[0].password});
                  // data.push({'user found': 'hello'})
                } else {
                    res.redirect("/editprofile?message=Could not load profile6");
                  // data.push({'error': 'could not find user'});
                }
            });
        }
        // res.json(data);
    }

    // var query = { email: req.query.email};
    // var username = req.body.username;
    // var password = req.body.password;
    // var name = req.body.name;
    // var phone = req.body.phone;
    // var email = req.body.email;

    // people.collection("user").find(query).toArray(function(err, result) {
    // if (err) {
    //   throw err;
    // } else if (result != "") {
    //   res.json({profile: result[0]})
    // } else {
    //   res.redirect("/queryLoginFailed");
    // }
    // });
});


// This is the '/test' endpoint that you can use to check that this works
// Do not change this, as you will want to use it to check the test code in Part 2
app.use('/test', (req, res) => {
    // create a JSON object
    var data = { 'message' : 'It works!' };
    // send it back
    res.json(data);
});


// This just sends back a message for any URL path not covered above
app.use('/', (req, res) => {
    res.send('Default message.');
});

// This starts the web server on port 4000. 
app.listen(4000, () => {
    console.log('Listening on port 4000');
});
