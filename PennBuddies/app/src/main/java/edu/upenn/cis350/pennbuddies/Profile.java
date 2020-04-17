package edu.upenn.cis350.pennbuddies;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Profile extends AppCompatActivity {

    User currUser;

    Button editButton;

    ImageView profilePicture;
    TextView username;
    TextView email;
    TextView hairColor;
    TextView eyeColor;
    TextView height;
    TextView weight;
    TextView dob;
    TextView pastTrips;
    TextView fullName;
    TextView phoneNumber;

    String emailString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        this.currUser = MainActivity.currUser;

        profilePicture = findViewById(R.id.profile_picture);
        profilePicture.setImageResource(R.drawable.profile_placeholder);

        username = findViewById(R.id.username);
        username.setText(currUser.getUsername());

        email = findViewById(R.id.email);
        email.setText(currUser.getEmail());

        hairColor = findViewById(R.id.hair_color);
        hairColor.setText(currUser.getHair());

        eyeColor = findViewById(R.id.eye_color);
        eyeColor.setText(currUser.getEyes());

        dob = findViewById(R.id.dob);
        dob.setText(currUser.getDob());

        height = findViewById(R.id.height);
        height.setText(currUser.getHeight());

        weight = findViewById(R.id.weight);
        weight.setText(currUser.getWeight());

        pastTrips = findViewById(R.id.past_trips);
        pastTrips.setText(getString(R.string.pastTrips));

        phoneNumber = findViewById(R.id.phone_number);
        phoneNumber.setText(currUser.getPhone());

        fullName = findViewById(R.id.name);
        fullName.setText(currUser.getName());

        editButton = findViewById(R.id.edit_button);
        editButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(Profile.this, "This will be linked to the website's " +
                        "edit_profile page", Toast.LENGTH_LONG).show();
            }
        });
    }




}
