package edu.upenn.cis350.pennbuddies;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;



public class Profile extends AppCompatActivity {

    Button editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        editButton = findViewById(R.id.edit_button);
        editButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(Profile.this, "This will be linked to the website's " +
                        "edit_profile page", Toast.LENGTH_LONG).show();
            }
        });
    }




}
