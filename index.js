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
  //var obj = db.db("Hours");


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

  console.log("req.body: " + req.body);
  console.log("JSON.stringify(req.body): " + JSON.stringify(req.body));
  console.log("{username : username, friends : []}: " + ({username : username, friends : []}));
  var friendsentry = {username : username, friends : []};

  people.collection("user").find(query).toArray(function(err, result) {
    if (err) {
      throw err;
    } else if (result == "" && id != "" && username != "" && password != "" && name != "" && phone != "" && email != "") {
      people.collection("friends").insertOne(friendsentry, function(err, result2) {
        if (err) throw err;
      });
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
app.get('/editprofile', (req, res) => {
  res.render('editprofile.ejs', {req: req, message: null});
});

//gets profile data
app.get('/getprofile', (req, res) => {
  var user = req.session.currUser;
  people = database.db("people");

  var query = { username: user};

  people.collection("user").find(query).toArray(function(err, result) {
    if (err) {
      throw err;
    } else if (result != "") {
      res.json({ profile: result[0] });
    } else {
      res.redirect("/editprofile?message=Could not load profile");
    }
  });
});

//updates profile
app.post('/checkeditprofile', (req, res) => {
  var user = req.session.currUser;
  people = database.db("people");

  var query = { username: user };

  people.collection("user").find(query).toArray(function(err, result) {
    if (err) {
      throw err;
    } else if (result != "") {
      var newValues = req.body;
      var vals = (JSON.stringify(newValues)).split(',');

      var valString = "{ ";
      for (var i = 0; i < vals.length; i++) {
        console.log("vals[i]: " + vals[i]);
        var vs = vals[i].split(':');

        if (vs[1].length != 2 && vals[i].charAt(0) == '{') {
          valString = vs[0].substring(1, vs[0].length) + ":" + vs[1] + ",";
        } else if (vs[1].length != 3 && vals[i].endsWith('}')) {
          valString += vs[0].substring(0, vs[0].length) + ":" + vs[1].substring(0, vs[1].length - 1);
        } else if (vals[i].endsWith('}')) {
          valString = valString.substring(0, valString.length - 1);
        } else if (vs[1].length != 2) {
          valString += vs[0].substring(0, vs[0].length) + ":" + vs[1] + ",";
        }
      }
      valString += " }";

      var myobj = { $set: JSON.parse(valString) };

      people.collection("user").updateOne(query, myobj, function(err, result2) {
        if (err) throw err;
        console.log(user + " updated");
        res.redirect("/editprofile");
      });
    } else {
      res.redirect("/?message=Could not find user");
    }
  });
});

//renders edit profile page
app.post('/addfriend', (req, res) => {
  people = database.db("people");

  var user = req.session.currUser;
  var friend = req.body.friend;

  var query1 = {username : user};
  var query2 = {username : friend};

  people.collection("friends").find(query1).toArray(function(err, result) {
    if (err) {
      throw err;
    } else if (result != "") {
      var friends = result[0].friends;
      friends.push(friend);

      var friendsList = { $set: {friends : friends} };

      people.collection("friends").updateOne(query1, friendsList, function(err, result2) {
        if (err) throw err;
      });
    } else {
      console.log(username + " username taken");
      res.redirect("/signup?message=User already exists or not all fields inputted.");
    }
  });

  people.collection("friends").find(query2).toArray(function(err, result) {
    if (err) {
      throw err;
    } else if (result != "") {
      var friends = result[0].friends;
      friends.push(user);

      var friendsList = { $set: {friends : friends} };

      people.collection("friends").updateOne(query2, friendsList, function(err, result2) {
        if (err) throw err;
      });
    } else {
      console.log(username + " username taken");
      res.redirect("/signup?message=User already exists or not all fields inputted.");
    }
  });

  res.redirect("/");
});

//gets list of friends
app.use('/getfriends', (req, res) => {
  people = database.db("people");

  var user = req.session.currUser;
  var query = { username: user};

  people.collection("friends").find(query).toArray(function(err, result) {
    if (err) {
      throw err;
    } else if (result != "") {
      res.json({ profile: result[0] });
    } else {
      res.redirect("/?message=Could not get friends");
    }
  });
});


//renders building hours page
app.get('/buildinghours', (req, res, next) => {
//  buildings = database.db("hours");
//  var query = { name: building };

  //people = database.db("Hours");

//  var buildingName = req.name;
//  var hours = req.name.hours;
//  var location = req.name.location;

var buildingArray = [];
  MongoClient.connect(url, function(err, db){
    theBuildings = database.db("Hours");
    assert.equal(null, err);
    var pointer = theBuildings.collection('building').find();
    pointer.forEach(function(doc, err){
      assert.equal(null, err);
      buildingArray.push(doc);
    }, function(){
      db.close();
      console.log(buildingArray);
      res.render('buildinghours.ejs', {buildings: buildingArray});
    });
  });

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
