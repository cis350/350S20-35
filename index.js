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

  dbo.collection("user").insertMany(myobjs, function(err, res) {
    if (err) throw err;
    console.log("Number of documents inserted: " + res.insertedCount);
    db.close();
  });
});


// This is the definition of the Person class
class Person {
    constructor(username, password, id, name, phone, email, birthdate, height, weight, hair, eyes) {
        this.username = username;
        this.password = password;
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.birthdate = birthdate;
        this.height = height;
        this.weight = weight;
        this.hair = hair;
        this.eyes = eyes;
    }
}

//renders homepage
app.use('/home', (req, res) => {
  res.render('main.ejs', {req: req, message: null});
});

//renders signup page
app.use('/signup', (req, res) => {
  res.render('signup.ejs', {req: req, message: null});
});

// This is the '/test' endpoint that you can use to check that this works
app.use('/test', (req, res) => {
    // create a JSON object
    var data = { 'message' : 'It works!' };
    // send it back
    res.json(data);
});

app.use('/get', (req, res) => {
    var id = req.query.id;
    var matches = [];

    if (Array.isArray(id)) {
      id.forEach((i) => {
        var found = false;
        people.forEach((person) => {
          if (person.id === i) {
            matches.push(person);
            found = true;
          }
        });
        if (!found) {
          matches.push(new Person(i, 'unknown', new Date().getTime()));
        }
      });
    } else {
      people.forEach((person) => {
        if (person.id === id) {
          matches.push(person);
        }
      });
    }

    if (id) {
      res.send(matches);
    } else {
      res.send([]);
    }
});

// This endpoint allows a caller to add data to the Map of Person objects
app.use('/set', (req, res) => {
    // read id and status from query parameters
    var id = req.query.id;
    var status = req.query.status;
    // create new Person object
    var person = new Person(id, status, new Date().getTime());
    // add it to Map
    people.set(id, person);
    // send it back to caller
    res.json(person);
});

// This starts the web server on port 3000.
app.listen(3000, () => {
    console.log('Listening on port 3000');
});
