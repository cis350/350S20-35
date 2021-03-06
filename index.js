var express = require('express');
var bodyParser = require('body-parser');
var session = require('express-session');
var app = express();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }))
app.use(bodyParser.raw({ type: 'application/vnd.custom-type' }));
app.use(bodyParser.text({ type: 'text/html' }));
app.use(session({resave: true, secret:"top-secret", saveUninitialized: true}));

var async = require('async');
var MongoClient = require('mongodb').MongoClient;
var assert = require('assert');
var url = "mongodb+srv://tannera:cis350squad@pennbuddies-aavlp.mongodb.net/test?retryWrites=true&w=majority";

var database;

MongoClient.connect(url, function(err, db) {
  if (err) throw err;
  database = db;


});

//renders homepage
app.get('/', (req, res) => {
  if (req.session.loggedIn) {
    res.render('homepage.ejs', {req: req, message: null});
  } else {
    res.render('main.ejs', {req: req});
  }
});

//renders signup User page
app.get('/signupUser', (req, res) => {
  res.render('signupUser.ejs', {req: req, message: null});
});

//renders student signup page
app.get('/signup', (req, res) => {
  res.render('signup.ejs', {req: req, message: null});
});

//renders police signup page
app.get('/signupPolice', (req, res) => {
  res.render('signupPolice.ejs', {req: req, message: null});
});

//renders escort signup page
app.get('/signupEscort', (req, res) => {
  res.render('signupEscort.ejs', {req: req, message: null});
});

//checks if username is taken, if not signs user up
app.post('/checksignup', (req, res) => {
  var people = database.db("people");
  var hours = database.db("Hours");


  var query = { username: req.body.username };
  var id = req.body._id;
  var username = req.body.username;
  var password = req.body.password;
  var name = req.body.name;
  var phone = req.body.phone;
  var email = req.body.email;
  var location = req.body.location;
  var start = req.body.start;
  var end = req.body.end;

  var friendsentry = {username : username, friends : []};
  var historyentry = {username : username, walks : []};
  var requestentry = {username : username, sent : [], received : []};
  var walkrequestentry = {username : username, sent : [], received : []};
  var policescheduleentry = {Name: name, Start : start, End : end, Location : location};
  var escortscheduleentry = {Name: name, Start : start, End : end};
  

  people.collection("user").find(query).toArray(function(err, result) {
    if (err) {
      throw err;
    } else if (result == "" && id != "" && username != "" && password != "" && name != "" && phone != "" && email != "") {
      people.collection("friends").insertOne(friendsentry, function(err, result2) {
        if (err) throw err;
      });
      people.collection("history").insertOne(historyentry, function(err, result2) {
        if (err) throw err;
      });
      people.collection("friend requests").insertOne(requestentry, function(err, result2) {
        if (err) throw err;
      });
      people.collection("walk requests").insertOne(walkrequestentry, function(err, result2) {
        if (err) throw err;
      });

    if (location == null){
      hours.collection("escorts").insertOne(escortscheduleentry, function(err, result2) {
        if (err) throw err;
        console.log("I am an escort" + escortscheduleentry);
        console.log(username + " schedule inputted");
        req.session.loggedIn = true;
        req.session.currUser = username;
        res.render('homepage.ejs', {req: req, message: null});
  });

}
    else{
      hours.collection("police officers").insertOne(policescheduleentry, function(err, result2) {
        if (err) throw err;
        console.log("I am a police officer" + policescheduleentry);
        console.log(username + " schedule inputted");
        req.session.loggedIn = true;
        req.session.currUser = username;
        res.render('homepage.ejs', {req: req, message: null});
      });
    }
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
  var people = database.db("people");

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

//renders profile page
app.get('/loadprofile', (req, res) => {

  var people = database.db("people");

  var currUser = req.session.currUser;

  people.collection("user").find({ username: currUser }).toArray(function(err, result) {

    if (err) {
      throw err;
    } else if (result != "") {
      res.render('loadprofile.ejs', {req: req, message: null});
    } else {
      res.redirect("/?message=Could not load profile");
    }
  });
});


//gets profile data
app.get('/getprofile', (req, res) => {
  var people = database.db("people");

  var user = req.session.currUser;
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
  var people = database.db("people");
  var hours = database.db("Hours");

  var user = req.session.currUser;
  var query = { username: user };

  people.collection("user").find(query).toArray(function(err, result) {
    if (err) {
      throw err;
    } else if (result != "") {
      var newValues = req.body;
      console.log(newValues);
      var vals = (JSON.stringify(newValues)).split(',');

      var valString = "{ ";
      for (var i = 0; i < vals.length; i++) {
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
        res.redirect("/loadprofile");
      });
    } else {
      res.redirect("/?message=Could not find user");
    }
  });
});

//updates profile pic
app.post('/checkeditprofilePic', (req, res) => {
  var people = database.db("people");
  var hours = database.db("Hours");

  var user = req.session.currUser;
  var query = { username: user };

  people.collection("user").find(query).toArray(function(err, result) {
    if (err) {
      throw err;
    } else if (result != "") {
      var newValues = req.body;
      console.log(newValues);


      people.collection("user").updateOne(query, myobj, function(err, result2) {
        if (err) throw err;
        res.redirect("/loadprofile");
      });
    } else {
      res.redirect("/?message=Could not find user");
    }
  });
});



//searches for a friend and sends a friend request to them
app.post('/sendfriendrequest', (req, res) => {
  var people = database.db("people");

  var user = req.session.currUser;
  var friend = req.body.friend;

  var query1 = {username : user};
  var query2 = {username : friend};

  var alreadySent = false;

  if (friend != undefined) {
    people.collection("friend requests").find(query1).toArray(function(err, result) {
      if (err) {
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
              throw err;
            } else {
              people.collection("friend requests").find(query2).toArray(function(err, result3) {
                if (err) {
                  throw err;
                } else if (result3 != "") {
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
                  res.redirect("/?message=Couldn't find user.");
                }
              });
            }
          });
        } else {
          res.redirect("/?message=Already sent request.");
        }
      } else {
        res.redirect("/?message=Couldn't find user.");
      }
    });
  }
});

//searches for a friend and adds the friend
app.post('/acceptfriendrequest', (req, res) => {
  var people = database.db("people");

  var user = req.session.currUser;
  var friend = req.body.friend;

  var query1 = {username : user};
  var query2 = {username : friend};

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
                    res.redirect("/");
                  }
                });
              } else {
                res.redirect("/?message=Couldn't find user.");
              }
            });
          }
        });
      } else {
        res.redirect("/?message=Couldn't find user.");
      }
    });
  }
});

//gets list of friends
app.use('/getfriends', (req, res) => {
  var people = database.db("people");

  var user = req.session.currUser;
  var query = { username: user};

  people.collection("friends").find(query).toArray(function(err, result) {
    if (err) {
      throw err;
    } else if (result != "") {
      var friends = result[0].friends;
      var nameList = [];

      async.forEach (friends, function(friend, callbck) {
        people.collection("user").find({ username: friend }).toArray(function(err, result2) {
          if (err) {
            throw err;
          } else {
            nameList.push(result2[0]);
            callbck();
          }
        });
      }, function() {
        res.json({friends: nameList});
      });
    } else {
      res.redirect("/?message=Could not get friends");
    }
  });
});

//gets sent requests
app.use('/getsentrequests', (req, res) => {
  var people = database.db("people");

  var user = req.session.currUser;
  var query = { username: user};

  people.collection("friends").find(query).toArray(function(err, result) {
    if (err) {
      throw err;
    } else if (result != "") {
      var friends = result[0].friends;

      people.collection("friend requests").find(query).toArray(function(err, result2) {
        if (err) {
          throw err;
        } else if (result2 != "") {
          var sent = result2[0].sent;
          var sentList = [];

          async.forEach (sent, function(friend, callbck) {
            if (!friends.includes(friend)) {
              people.collection("user").find({ username: friend }).toArray(function(err, result3) {
                if (err) {
                  throw err;
                } else {
                  sentList.push(result3[0]);
                  callbck();
                }
              });
            } else {
              callbck();
            }
          }, function() {
            res.json({sent: sentList});
          });
        } else {
          res.redirect("/?message=Could not get requests");
        }
      });
    } else {
      res.redirect("/?message=Could not get friends");
    }
  });
});

//gets received requests
app.use('/getreceivedrequests', (req, res) => {
  var people = database.db("people");

  var user = req.session.currUser;
  var query = { username: user};

  people.collection("friends").find(query).toArray(function(err, result) {
    if (err) {
      throw err;
    } else if (result != "") {
      var friends = result[0].friends;

      people.collection("friend requests").find(query).toArray(function(err, result2) {
        if (err) {
          throw err;
        } else if (result2 != "") {
          var received = result2[0].received;
          var receivedList = [];

          async.forEach (received, function(friend, callbck) {
            if (!friends.includes(friend)) {
              people.collection("user").find({ username: friend }).toArray(function(err, result3) {
                if (err) {
                  throw err;
                } else {
                  receivedList.push(result3[0]);
                  callbck();
                }
              });
            } else {
              callbck();
            }
          }, function() {
            res.json({received: receivedList});
          });
        } else {
          res.redirect("/?message=Could not get requests");
        }
      });
    } else {
      res.redirect("/?message=Could not get friends");
    }
  });
});

//gets list of friend suggestions
app.use('/getsuggestions', (req, res) => {
  var people = database.db("people");
  var user = req.session.currUser;

  people.collection("friends").find({ username: user }).toArray(function(err, result) {
    if (err) {
      throw err;
    } else if (result != "") {
      var friends = result[0].friends;

      people.collection("friend requests").find({ username: user }).toArray(function(err, result2) {
        if (err) {
          throw err;
        } else if (result2 != "") {
          var sent = result2[0].sent;
          var received = result2[0].received;
          var suggestions = [];

          async.forEach (friends, function(friend, callbck) {
            people.collection("friends").find({ username: friend }).toArray(function(err, result3) {
              if (err) {
                throw err;
              } else {
                var fr = result3[0].friends;

                async.forEach (fr, function(f, callbck2) {
                  if ((user != f) && !friends.includes(f) && !sent.includes(f) && !received.includes(f) && !suggestions.includes(f)) {
                    people.collection("user").find({ username: f }).toArray(function(err, result3) {
                      if (err) {
                        throw err;
                      } else {
                        suggestions.push(result3[0]);
                        callbck2();
                      }
                    });
                  } else {
                    callbck2();
                  }
                }, function() {
                  callbck();
                });
              }
            });
          }, function() {
            res.json({suggestions: suggestions});
          });
        } else {
          res.redirect("/?message=Could not get requests");
        }
      });
    } else {
      res.redirect("/?message=Could not get friends");
    }
  });
});

//renders building hours page
app.get('/buildinghours', (req, res, next) => {
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
      res.render('buildinghours.ejs', {req: req, buildings: buildingArray});
    });
  });

});

//renders campus resource hours page
app.get('/campusresourceshours', (req, res, next) => {
  var campusResourceArray = [];
    MongoClient.connect(url, function(err, db){
      theResources = database.db("Hours");
      assert.equal(null, err);
      var pointer = theResources.collection('campus resources').find();
      pointer.forEach(function(doc, err){
        assert.equal(null, err);
        campusResourceArray.push(doc);
      }, function(){
        db.close();
        console.log(campusResourceArray);
        res.render('campusresourceshours.ejs', {req: req, campusResources: campusResourceArray});
      });
    });


});

//renders call box locations page
app.get('/callBoxLocations', (req, res, next) => {

var callBoxArray = [];
  MongoClient.connect(url, function(err, db){
    theCallBoxes = database.db("Locations");
    assert.equal(null, err);
    var pointer = theCallBoxes.collection('Call Boxes').find();
    pointer.forEach(function(doc, err){
      assert.equal(null, err);
      callBoxArray.push(doc);
    }, function(){
      db.close();
      console.log(callBoxArray);
      res.render('callBoxLocations.ejs', {req: req, callBoxes: callBoxArray});
    });
  });

});

//renders police officers schedules page
app.get('/policeSchedules', (req, res, next) => {

var policeArray = [];
  MongoClient.connect(url, function(err, db){
    policeSchedules = database.db("Hours");
    assert.equal(null, err);
    var pointer = policeSchedules.collection('police officers').find();
    pointer.forEach(function(doc, err){
      assert.equal(null, err);
      policeArray.push(doc);
    }, function(){
      db.close();
      console.log(policeArray);
      res.render('policeOfficerSchedules.ejs', {req: req, schedules: policeArray});
    });
  });

});

//renders escorts schedules page
app.get('/escortSchedules', (req, res, next) => {

var escortArray = [];
  MongoClient.connect(url, function(err, db){
    escortSchedules = database.db("Hours");
    assert.equal(null, err);
    var pointer = escortSchedules.collection('escorts').find();
    pointer.forEach(function(doc, err){
      assert.equal(null, err);
      escortArray.push(doc);
    }, function(){
      db.close();
      console.log(escortArray);
      res.render('escortSchedules.ejs', {req: req, schedules: escortArray});
    });
  });

});

//renders resources page
app.use('/loadresources', (req, res) => {
  res.render('resources.ejs', {req: req, message: null});
});

//renders profile pic page
app.use('/loadProfilePic', (req, res) => {
  res.render('profilePic.ejs', {req: req, message: null});
});


//searches for a user
app.use('/searchuser', (req, res) => {
  var people = database.db("people");

  var currUser = req.session.currUser;
  var searchUser = "";

  if (req.body != undefined && req.body.username != undefined) {
    searchUser = req.body.username;
  } else {
    searchUser = req.query.username;
  }

  var alreadyFriends = false;
  var requestSent = false;
  var requestReceived = false;

  people.collection("friends").find({ username: searchUser }).toArray(function(err, result) {
    if (err) {
      throw err;
    } else if (result != "") {
      var friends = result[0].friends;

      for (var i = 0; i < friends.length; i++) {
        if (friends[i] == currUser) {
          alreadyFriends = true;
        }
      }

      if (!alreadyFriends) {
        people.collection("friend requests").find({ username: searchUser }).toArray(function(err, result2) {
          if (err) {
            throw err;
          } else if (result2 != "") {
            var sent = result2[0].sent;

            for (var i = 0; i < sent.length; i++) {
              if (sent[i] == currUser) {
                requestReceived = true;
              }
            }

            var received = result2[0].received;

            for (var i = 0; i < received.length; i++) {
              if (received[i] == currUser) {
                requestSent = true;
              }
            }

            res.render('loadsearchprofile.ejs', {req: req, message: null, user: searchUser, friends: alreadyFriends, sent: requestSent, received: requestReceived});
          } else {
            res.redirect("/?message=Couldn't find user.");
          }
        });
      } else {
        res.render('loadsearchprofile.ejs', {req: req, message: null, user: searchUser, friends: alreadyFriends, sent: requestSent, received: requestReceived});
      }
    } else {
      res.redirect("/?message=Couldn't find user.");
    }
  });
});

//gets other user's profile data
app.get('/getsearchprofile', (req, res) => {
  var people = database.db("people");

  var searchUser = req.query.username;
  var alreadyFriends = req.query.friends;

  people.collection("user").find({ username: searchUser }).toArray(function(err, result) {
    if (err) {
      throw err;
    } else if (result != "") {
      res.json({ profile: result[0], friends: alreadyFriends });
    } else {
      res.redirect("/?message=Could not load profile");
    }
  });
});

//gets other user's friends list
app.get('/getsearchfriends', (req, res) => {
  var people = database.db("people");

  var currUser = req.session.currUser;
  var searchUser = req.query.username;
  var alreadyFriends = req.query.friends;

  people.collection("friends").find({ username: searchUser }).toArray(function(err, result) {
    if (err) {
      throw err;
    } else if (result != "") {
      var friends = result[0].friends;
      var nameList = [];

      if (alreadyFriends == "false") {
        people.collection("friends").find({ username: currUser }).toArray(function(err, result2) {
          if (err) {
            throw err;
          } else {
            var currFriends = result2[0].friends;

            async.forEach (friends, function(friend, callbck) {
              if (currFriends.includes(friend)) {
                people.collection("user").find({ username: friend }).toArray(function(err, result3) {
                  if (err) {
                    throw err;
                  } else {
                    nameList.push(result3[0]);
                    callbck();
                  }
                });
              } else {
                callbck();
              }
            }, function() {
              res.json({friends: nameList, alreadyFriends: alreadyFriends});
            });
          }
        });
      } else {
        async.forEach (friends, function(friend, callbck) {
          people.collection("user").find({ username: friend }).toArray(function(err, result3) {
            if (err) {
              throw err;
            } else {
              nameList.push(result3[0]);
              callbck();
            }
          });
        }, function() {
          res.json({friends: nameList, alreadyFriends: alreadyFriends});
        });
      }
    } else {
      res.redirect("/?message=Could not get friends");
    }
  });
});

//renders walking history page
app.use('/loadhistory', (req, res) => {
  res.render('history.ejs', {req: req, message: null});
});

//gets history data
app.get('/gethistory', (req, res) => {
  var people = database.db("people");

  var user = req.session.currUser;
  var query = { username: user};

  people.collection("history").find(query).toArray(function(err, result) {
    if (err) {
      throw err;
    } else if (result != "") {
      res.json({ history: result[0] });
    } else {
      res.redirect("/editprofile?message=Could not load history");
    }
  });
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
