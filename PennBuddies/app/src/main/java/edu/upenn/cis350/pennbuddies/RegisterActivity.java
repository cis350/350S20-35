package edu.upenn.cis350.pennbuddies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;

import android.os.Bundle;
import android.widget.Button;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final Button haveAccount = findViewById(R.id.haveAccount);
        haveAccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                intent.putExtra("email", R.id.email);
                intent.putExtra("email", R.id.password);
                startActivity(intent);
            }
        });

        final Button register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Buddies.class);
                intent.putExtra("email", R.id.email);
                intent.putExtra("email", R.id.password);
                startActivity(intent);
            }
        });
    }
}
