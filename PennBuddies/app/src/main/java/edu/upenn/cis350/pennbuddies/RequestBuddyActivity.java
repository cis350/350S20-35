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
import android.graphics.Color;

public class RequestBuddyActivity extends AppCompatActivity {


    User currUser;
    List<String>  friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_buddy);

        this.currUser = MainActivity.currUser;
        new getFriends().execute(currUser.getUsername());
        new getIncomingRequests().execute(currUser.getUsername());
    }

    public void manageFriends(View view) {
        startActivity(new Intent(RequestBuddyActivity.this, ManageFriendsActivity.class));
    }

    public void upcomingWalks(View view) {
        startActivity(new Intent(RequestBuddyActivity.this, UpcomingWalksActivity.class));
    }

    private class getFriends extends AsyncTask<String, Void, Void> {

        private LinearLayout linearLayout;
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
                        currUser.friends.add(arr.getString(i));
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
                                textView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        textView.setTextColor(Color.parseColor("#F5F5F5"));
                                        currUser.friends.add(textView.getText().toString());
                                        new sendWalkRequest().execute(textView.getText().toString());
                                    }
                                });
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

    private class getIncomingRequests extends AsyncTask<String, Void, Void> {

        private String result;
        private LinearLayout linearLayout2;

        @Override
        protected Void doInBackground(String... params) {
            String friendUsername = params[0];
            try {
                Log.e("Connection", "Connecting to HTTPS");
                URL url = new URL("http://10.0.2.2:4000/getIncomingWalkRequests?id="
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

                    JSONArray arr = (JSONArray) obj.get("received");


                    // getting firstName and lastName
                    friends = new ArrayList<String>();
                    for(int i = 0; i < arr.length(); i++){
                        currUser.friends.add(arr.getString(i));
                        friends.add(arr.getString(i));
                    }

                    Log.e("Incoming Requests: ", friends.toString());

                    in.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            linearLayout2 = findViewById(R.id.linearLayout2);
                            for( int i = 0; i < friends.size(); i++ )
                            {
                                TextView textView = new TextView(RequestBuddyActivity.this);
                                textView.setText(friends.get(i));
                                textView.setTextSize(15);
                                textView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        textView.setTextColor(Color.parseColor("#F5F5F5"));
                                        currUser.friends.add(textView.getText().toString());
                                        new acceptWalkRequest().execute(textView.getText().toString());
                                    }
                                });
                                linearLayout2.addView(textView);
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

    private class acceptWalkRequest extends AsyncTask<String, Void, Void> {

        private String result;
        private TextView resultText;

        @Override
        protected Void doInBackground(String...params) {
            String friendUsername = params[0];
            try {
                Log.e("Connection", "Connecting to HTTPS");
                URL url = new URL("http://10.0.2.2:4000/acceptwalkrequest?id="
                        + currUser.getUsername() + "&id=" + friendUsername);
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

                    // getting firstName and lastName
                    result = (String) userObject.get("status");

                    Log.e("Result", result);
                    conn.disconnect();
                    in.close();

                    currUser.friends.add(params[0]);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resultText = findViewById(R.id.walkRequestResultText);
                            resultText.setText("Walk Request Accepted!");                        }
                    });

                }
            } catch (Exception e) {
                Log.e("Connection", e.toString());
                if (e.toString().equals("org.json.JSONException: No value for status")) {
                    Log.e("no value", "true");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resultText = findViewById(R.id.walkRequestResultText);
                            resultText.setText("Walk Request Accepted!");
                        }
                    });
                }
            }
            return null;
        }
    }

    private class sendWalkRequest extends AsyncTask<String, Void, Void> {

        TextView errorText = findViewById(R.id.errorText);
        private String result;

        @Override
        protected Void doInBackground(String...params) {
            String friendUsername = params[0];
            try {
                Log.e("Connection", "Connecting to HTTPS");
                URL url = new URL("http://10.0.2.2:4000/sendwalkrequest?id="
                        + currUser.getUsername() + "&id=" + friendUsername);
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

                    // getting firstName and lastName
                    result = (String) userObject.get("status");

                    Log.e("Result", result);
                    conn.disconnect();
                    in.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            errorText.setText("Wait! You've already sent a walk request to "
                                    + friendUsername);
                        }
                    });

                }
            } catch (Exception e) {
                Log.e("Connection", e.toString());
                if (e.toString().equals("org.json.JSONException: No value for status")) {
                    Log.e("no value", "true");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            errorText.setText("Success! " + friendUsername + " " +
                                    "has been notified of your request");

                        }
                    });
                }
            }
            return null;
        }
    }
    }
