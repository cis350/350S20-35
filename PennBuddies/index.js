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

var currentEmail;

app.use('/queryPassword', (req, res) => {
    var people = database.db("people");

    if (req.query == {}) {
        res.redirect("/editprofile?message=Could not load profile1");
        // res.json("hello");
    }
    else {
        var data = [];
        var email = req.query.id;
        if (email == "") {
            // res.json("problem");
            res.redirect("/editprofile?message=Could not load profile2");
            // data.push("problem");
        }
        else if (email == null) {
            // res.json("problem 2");
            res.redirect("/editprofile?message=Could not load profile3");
            // data.push("problem");
        }
        else {
            people.collection("user").find({email : email}).toArray(function(err, result) {
                if (err) {
                  // throw err;
                  res.redirect("/editprofile?message=Could not load profile4");
                } else if (result != "") {
                    // res.redirect("/editprofile?password=" + result[0].name);
                  res.json({'password': result[0].password});
                  currentEmail = email;
                } else {
                    res.redirect("/editprofile?message=Could not load profile6");
                }
            });
        }
        // res.json(data);
    }
});

app.use('/currentUser', (req, res) => {
    var email = req.query.id;

    var data = [];
    var people = database.db("people");

    people.collection("user").find({email : email}).toArray(function(err, result) {
        // res.json({'password': result[0].password});
        res.json(
            {'name' : result[0].name,
            'username': result[0].username,
            'password' : result[0].password,
            'email' : result[0].email,
            'hair' : result[0].hair,
            'eyes' : result[0].eyes,
            'dob' : result[0].birthdate,
            'phone' : result[0].phone,
            'height' : result[0].height,
            'weight' : result[0].weight,
            'gender' : result[0].gender,
        });
    });
});

//searches for a friend and sends a friend request to them
app.use('/sendfriendrequestMobile', (req, res) => {
  var abort = false;
  var people = database.db("people");

  var id = req.query.id;

  var data = {}

  var user = id[0];
  var friend = id[1];

  console.log(user + " is sending a friend request to: " + friend);

  var query1 = {username : user};
  var query2 = {username : friend};

  var alreadySent = false;

  if (friend != undefined) {
    people.collection("user").find(query2).toArray(function(err, result) {
        if (err) {
            abort = true;
            data = {"status": "error finding user"};
            res.json(data);
        }
        else {
            abort = false;
        }
    })
} else {
    abort = true;
}

console.log("abort: " + abort);

if (abort == false) {
    people.collection("friend requests").find(query1).toArray(function(err, result) {
      if (err) {
        data = {"status": "error finding user"};
        throw err;
      } else if (result != "") {
        var sent = result[0].sent;

        for (var i = 0; i < sent.length; i++) {
          if (sent[i] == friend) {
            alreadySent = true;
          }
        }

        if (!alreadySent) {
          sent.push(friend);

          var sentList = { $set: {sent : sent} };

          people.collection("friend requests").updateOne(query1, sentList, function(err, result2) {
            if (err) {
              data = {"status": "error finding user"};
              throw err;
            } else {
              people.collection("friend requests").find(query2).toArray(function(err, result3) {
                if (err) {
                  data = {"status": "error finding user"};
                  throw err;
                } else if (result3 != "") {
                    data = {"status": "friend request sent"};
                  var received = result3[0].received;
                  received.push(user);

                  var receivedList = { $set: {received : received} };

                  people.collection("friend requests").updateOne(query2, receivedList, function(err, result4) {
                    if (err) {
                      throw err;
                    } else {
                      res.redirect("/");
                    }
                  });
                } else {
                    data = {"status": "couldn't find user"};
                }
              });
            }
          });
        } else {
            data = {"status": "friend request already sent"};
        }
      } else {
        data = {"status": "couldn't find user"};
      }
      res.json(data);
    });
    console.log(data);
  }
});

//gets list of friends
app.use('/getfriends', (req, res) => {
  var people = database.db("people");

  var username = req.query.id;
  var query = { username: username};

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

//gets list of friends
app.use('/getIncomingRequests', (req, res) => {
  var people = database.db("people");

  var username = req.query.id;
  var query = { username: username};

  people.collection("friend requests").find(query).toArray(function(err, result) {
    if (err) {
      throw err;
    } else if (result != "") {
      res.json({ profile: result[0] });
    } else {
      res.redirect("/?message=Could not get friends");
    }
  });
});

//gets call boxes locations
app.use('/getCallBoxLocations', (req, res) => {
  var location = database.db("Locations");
  var locationCoordinates = [];

  location.collection("Call Boxes").find({}).toArray(function(err, result) {
    if (err){
      throw err;
    } else if (result != null){
      result.forEach( (doc) => {
        locationCoordinates.push( {'location' : doc.Location, 'latitude' : doc.latitude,
          'longitude' : doc.longitude});
    });
    res.json(locationCoordinates);
  } else{
    res.redirect("/?message=Could not get call box locations");
  }
  });
});

//gets police officer Locations
app.use('/getOfficerLocations', (req, res) => {
  var hours = database.db("Hours");
  var officers = [];

  hours.collection("police officers").find({}).toArray(function(err, result) {
    if (err){
      throw err;
    } else if (result != null){
      result.forEach( (doc) => {
        officers.push({'name' : doc.Name, 'start': doc.Start, 'end': doc.End,
        'latitude': doc.Latitude, 'longitude': doc.Longitude});
      });
      res.json(officers);
    } else{
      res.redirect('/?message=Could not get police officer locations');
    }
  });
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
