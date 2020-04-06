package edu.upenn.cis350.pennbuddies;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.TextView;



public class Profile extends AppCompatActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilePicture = findViewById(R.id.profile_picture);
        profilePicture.setImageResource(R.drawable.profile_placeholder);

        username = findViewById(R.id.username);
        username.setText(getString(R.string.username));

        email = findViewById(R.id.email);
        email.setText(getString(R.string.email));

        hairColor = findViewById(R.id.hair_color);
        hairColor.setText(getString(R.string.hairColor));

        eyeColor = findViewById(R.id.eye_color);
        eyeColor.setText(getString(R.string.eyeColor));

        dob = findViewById(R.id.dob);
        dob.setText(getString(R.string.dob));

        height = findViewById(R.id.height);
        height.setText(getString(R.string.height));

        weight = findViewById(R.id.weight);
        weight.setText(getString(R.string.weight));

        height = findViewById(R.id.height);
        height.setText(getString(R.string.height));

        pastTrips = findViewById(R.id.past_trips);
        pastTrips.setText(getString(R.string.pastTrips));

        phoneNumber = findViewById(R.id.phone_number);
        phoneNumber.setText(getString(R.string.phoneNumber));

        fullName = findViewById(R.id.name);
        fullName.setText("Avni Ahuja");

        editButton = findViewById(R.id.edit_button);
        editButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(Profile.this, "This will be linked to the website's " +
                        "edit_profile page", Toast.LENGTH_LONG).show();
            }
        });
    }




}
