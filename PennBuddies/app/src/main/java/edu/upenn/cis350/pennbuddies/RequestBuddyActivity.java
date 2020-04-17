package edu.upenn.cis350.pennbuddies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RequestBuddyActivity extends AppCompatActivity {

    LinearLayout linearLayout;

    User currUser;
    List<String>  friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_buddy);

        this.currUser = MainActivity.currUser;
        new getFriends().execute(currUser.getUsername());
    }

    public void manageFriends(View view) {
        startActivity(new Intent(RequestBuddyActivity.this, ManageFriendsActivity.class));
    }

    private class getFriends extends AsyncTask<String, Void, Void> {

        private String result;

        @Override
        protected Void doInBackground(String... params) {
            String friendUsername = params[0];
            try {
                Log.e("Connection", "Connecting to HTTPS");
                URL url = new URL("http://10.0.2.2:4000/getfriends?id="
                        + currUser.getUsername());
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

                    // parsing file "JSONExample.json"
                    JSONObject obj = new JSONObject(resultPassword);

                    // typecasting obj to JSONObject
                    JSONObject userObject = (JSONObject) obj;

                    JSONObject profile = (JSONObject) userObject.get("profile");
                    JSONArray arr = (JSONArray) profile.get("friends");

                    // getting firstName and lastName
                    friends = new ArrayList<String>();
                    for(int i = 0; i < arr.length(); i++){
                        friends.add(arr.getString(i));
                    }

                    Log.e("Friends: ", friends.toString());

                    in.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            linearLayout = findViewById(R.id.linearLayout);
                            for( int i = 0; i < friends.size(); i++ )
                            {
                                TextView textView = new TextView(RequestBuddyActivity.this);
                                textView.setText(friends.get(i));
                                textView.setTextSize(15);
                                linearLayout.addView(textView);
                            }
                        }
                    });

                }
            } catch (Exception e) {
                Log.e("Connection", e.toString());
            }
            return null;
        }
      }
    }
