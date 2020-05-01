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
      if (result == "") {
        res.json({'password': ""})
      } else {
        var username = result[0].username;
        var name = result[0].name;
        var password = result[0].password;
        var email = result[0].email;
        var hair = result[0].hair;
        var phone = result[0].phone;
        var eyes = result[0].eyes;
        var dob = result[0].birthdate;
        var height = result[0].height;
        var weight = result[0].weight;
        var gender = result[0].gender;

        people.collection("history").find({username : username}).toArray(function(err, result2) {
          if (result == "") {
            var history = [];
          } else {
            var history = result2[0].walks;
          }
          res.json(
            {'name' : name,
            'username': username,
            'password' : password,
            'email' : email,
            'hair' : hair,
            'eyes' : eyes,
            'dob' : dob,
            'phone' : phone,
            'height' : height,
            'weight' : weight,
            'gender' : gender,
            'history' : history,
        });
        });


      }
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


//searches for a friend and sends a friend request to them
app.use('/acceptfriendrequestMobile', (req, res) => {
  var people = database.db("people");

  var id = req.query.id;

  var data = {}

  var user = id[0];
  var friend = id[1];

  var query1 = {username : user};
  var query2 = {username : friend};

  console.log(user + " is accepting " + friend + "'s request");

  if (friend != undefined) {
    people.collection("friends").find(query1).toArray(function(err, result) {
      if (err) {
        throw err;
      } else if (result != "") {
        var friends = result[0].friends;
        friends.push(friend);
        var friendsList = { $set: {friends : friends} };

        people.collection("friends").updateOne(query1, friendsList, function(err, result2) {
          if (err) {
            throw err;
          } else {
            people.collection("friends").find(query2).toArray(function(err, result3) {
              if (err) {
                throw err;
              } else if (result3 != "") {
                var friends = result3[0].friends;
                friends.push(user);
                var friendsList = { $set: {friends : friends} };

                people.collection("friends").updateOne(query2, friendsList, function(err, result4) {
                  if (err) {
                    throw err;
                  } else {
                    data = {"status": "successfully accepted"};
                  }
                });
              } else {
                data = {"status": "couldn't find user"};
              }
            });
          }
        });
      } else {
        data = {"status": "couldn't find user"};
      }
      res.json(data);
    });
  }
});


//searches for a friend and sends a walk request to them
app.use('/sendwalkrequest', (req, res) => {
  var abort = false;
  var people = database.db("people");

  var id = req.query.id;

  var data = {}

  var user = id[0];
  var friend = id[1];

  console.log(user + " is sending a walk request to: " + friend);

  var query1 = {username : user};
  var query2 = {username : friend};

  var alreadySent = false;

  if (friend != undefined) {
    console.log("here");
    people.collection("user").find(query2).toArray(function(err, result20) {
      console.log("result:" + result20 + ".");
      if (err) {
        console.log("err");
        throw err;
      }
      if (result20 == "") {
          console.log("blank");
          abort = true;
          console.log("abort: "+ abort);
          data = {"status": "error finding user"};
      }
    })
} else {
    abort = true;
}

console.log("abort: " + abort);

if (abort == false) {
    people.collection("walk requests").find(query1).toArray(function(err, result) {
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

          people.collection("walk requests").updateOne(query1, sentList, function(err, result2) {
            if (err) {
              data = {"status": "error finding user"};
              throw err;
            } else {
              people.collection("walk requests").find(query2).toArray(function(err, result3) {
                if (err) {
                  data = {"status": "error finding user"};
                  throw err;
                } else if (result3 != "") {
                    data = {"status": "walk request sent"};
                  var received = result3[0].received;
                  received.push(user);

                  var receivedList = { $set: {received : received} };

                  people.collection("walk requests").updateOne(query2, receivedList, function(err, result4) {
                    if (err) {
                      throw err;
                    } else {
                      data = ({"status": "sent"});
                    }
                  });
                } else {
                    data = {"status": "couldn't find user"};
                }
              });
            }
          });
        } else {
            data = {"status": "walk request already sent"};
        }
      } else {
        data = {"status": "couldn't find user"};
      }
      res.json(data);
    });
    console.log(data);
  }
});



//searches for a friend and sends a friend request to them
app.use('/acceptwalkrequest', (req, res) => {
  var people = database.db("people");

  var id = req.query.id;

  var data = {}

  var user = id[0];
  var friend = id[1];

  var query1 = {username : user};
  var query2 = {username : friend};

  console.log(user + " is accepting " + friend + "'s walk request");

  if (friend != undefined) {
    people.collection("history").find(query1).toArray(function(err, result) {
      if (err) {
        throw err;
      } else if (result != "") {
        var walks = result[0].walks;
        var today = new Date();
        var date = today.getFullYear()+'-'+(today.getMonth()+1)+'-'+today.getDate()
        var walk = {friend, date};
        console.log(walk);
        walks.push(walk);
        var walks = { $set: {walks : walks} };

        people.collection("history").updateOne(query1, walks, function(err, result2) {
          if (err) {
            throw err;
          } else {
            people.collection("history").find(query2).toArray(function(err, result3) {
              if (err) {
                throw err;
              } else if (result3 != "") {
                var walks = result3[0].walks;
                var walk = {user, date};
                console.log(walk);
                walks.push(walk);
                var walks = { $set: {walks : walks} };

                people.collection("history").updateOne(query2, walks, function(err, result4) {
                  if (err) {
                    throw err;
                  } else {
                    data = {"status": "successfully accepted"};
                  }
                });
              } else {
                data = {"status": "couldn't find user"};
              }
            });
          }
        });
      } else {
        data = {"status": "couldn't find user"};
      }
      res.json(data);
    });
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
app.use('/getIncomingWalkRequests', (req, res) => {
  var people = database.db("people");

  var username = req.query.id;
  var query = { username: username};


  console.log(username + " is looking for her walk requests");

  people.collection("walk requests").find(query).toArray(function(err, result) {
    if (err) {
      throw err;
    } else if (result != "") {
      res.json({ received: result[0].received });
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

//get most recent buddy request that was accept
app.use('/getRecentAcceptedBuddyRequest', (req, res) => {
  var people = database.db("people");
  var requestInfo = [];
  var username = req.query.id;
  var query = { username: username};

  people.collection("history").find(query).toArray(function(err, result) {
    if (err){
      throw err;
    } else if (result != null){
      result.forEach( (doc) => {
        requestInfo.push({'history': doc.walks[doc.walks.length-1]});
      });
      res.json(requestInfo);
    } else{
      res.redirect('/?message=Could not get recently approved buddy request');
    }
  });
});

//get building hours
app.use('/getBuildingHours', (req, res) => {
  var hours = database.db("Hours");
  var buildingHours = [];

  hours.collection("building").find({}).toArray(function(err, result) {
    if (err){
      throw err;
    } else if (result != null){
      result.forEach( (doc) => {
        buildingHours.push({'name': doc.name, 'hours': doc.hours});
      });
      res.json(buildingHours);
    } else{
      res.redirect('/?message=Could not get building hours');
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
