package edu.upenn.cis350.pennbuddies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;

import com.mongodb.stitch.android.core.auth.StitchUser;

import android.util.Log;
import android.text.TextUtils;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    public static User currUser;

    private String host;
    private int port;

    private EditText email;
    private EditText password;
    private Button login;
    private StitchUser currentUser;

    @Override
    protected void onStart() {
        super.onStart();

        host = "localhost";
        port = 4000;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        login.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                attemptLogin();
            }
        });

//        final Button noAccount = findViewById(R.id.noAccount);
//
//        noAccount.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                Intent intent = new Intent(v.getContext(), RegisterActivity.class);
//                startActivity(intent);
//            }
//        });

    }

    private void attemptLogin() {
        final String emailText = email.getText().toString();
        final String passwordText = password.getText().toString();
        boolean abort = false;
        View focus = null;

        if (TextUtils.isEmpty(emailText)) {
            email.setError("Required Field");
            focus = email;
            abort = true;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            email.setError("Invalid Email");
            focus = email;
            abort = true;
        }

        if (TextUtils.isEmpty(passwordText)) {
            password.setError("Empty Password");
            focus = password;
            abort = true;
        }

        if (abort) {
            focus.requestFocus();
        } else {
            new letsLogin().execute(emailText, passwordText);
        }
    }

    private class letsLogin extends AsyncTask<String, Void, Void> {
        private ProgressDialog progress;
        private byte statusCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = ProgressDialog.show(MainActivity.this, "",
                    "PennBuddies is finding you...");
        }

        @Override
        protected Void doInBackground(String...params) {
            try {
                Log.e("Connection", "Connecting to HTTPS");
                URL url = new URL("http://10.0.2.2:4000/currentUser?id=" + params[0]);
                Log.e("Connection", "Connected");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                InputStream inputStream;
                int responsecode = conn.getResponseCode();
                if (responsecode != 200) {
                    throw new IllegalStateException();
                } else {
                    inputStream = conn.getInputStream();

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(
                                    inputStream));

                    StringBuilder response = new StringBuilder();
                    String currentLine;

                    while ((currentLine = in.readLine()) != null)
                        response.append(currentLine);
                        String resultPassword = response.toString();


                    if (resultPassword.equals("Default Message")) {
                        statusCode = 0;
                        onPostExecute();
                    }
                    else {
                        // parsing file "JSONExample.json"
                        JSONObject obj = new JSONObject(resultPassword);

                        // typecasting obj to JSONObject
                        JSONObject userObject = (JSONObject) obj;

                        // getting firstName and lastName
                        String passwordString = (String) userObject.get("password");

                        Log.e("Connection", passwordString);

                        in.close();

                        if (passwordString.equals(params[1])) {
                            currUser = new User((String) userObject.get("name"),
                                    (String) userObject.get("username"),
                                    (String) userObject.get("email"),
                                    (String) userObject.get("password"),
                                    (String) userObject.get("hair"),
                                    (String) userObject.get("eyes"),
                                    (String) userObject.get("height"),
                                    (String) userObject.get("weight"),
                                    (String) userObject.get("dob"),
                                    (String) userObject.get("phone"));
                            statusCode = 1;
                            onPostExecute();
                        }
                        else {
                            statusCode = 0;
                            onPostExecute();
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("Connection", e.toString());
            }

            return null;
        }

        protected void onPostExecute() {
            View focus = null;
            if (statusCode == 1) {
                Log.d("Login", "Successful!");
                Intent intent = new Intent(MainActivity.this, Buddies.class);
                intent.putExtra("email", currUser.getEmail());
                startActivity(intent);

//                startActivity(new Intent(MainActivity.this, Buddies.class));
                progress.dismiss();
            } else {
                password.setText("");
                password.setHint("Wrong Email/Password");
                Log.e("Login", "Failed");
                focus = email;
                progress.dismiss();
            }

        }

    }

}
