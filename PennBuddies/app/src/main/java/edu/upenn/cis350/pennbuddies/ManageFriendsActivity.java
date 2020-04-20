package edu.upenn.cis350.pennbuddies;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.view.View.OnClickListener;

public class ManageFriendsActivity extends AppCompatActivity {

    private String host;
    private int port;

    TextView errorText;

    public User currUser;

    private EditText friendUsernameBox;

    @Override
    protected void onStart() {
        super.onStart();

        host = "localhost";
        port = 4000;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_friends);

        this.currUser = MainActivity.currUser;
        new loadIncomingRequests().execute(currUser.getUsername());

        errorText = findViewById(R.id.errorLabel);
    }

    public void checkParams(View view) {
        friendUsernameBox = findViewById(R.id.friendUsername);
        final String friendUsernameText = friendUsernameBox.getText().toString();
        boolean abort;
        View focus = null;

        if (TextUtils.isEmpty(friendUsernameText)) {
            friendUsernameBox.setError("Required Field");
            friendUsernameBox.requestFocus();
        } else {
            new addFriend().execute(friendUsernameText);
        }
    }

    private class acceptRequest extends AsyncTask<String, Void, Void> {

        LinearLayout linlay;
        private String result;

        @Override
        protected Void doInBackground(String...params) {
            String friendUsername = params[0];
            try {
                Log.e("Connection", "Connecting to HTTPS");
                URL url = new URL("http://10.0.2.2:4000/acceptfriendrequestMobile?id="
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
                            errorText.setText(result);
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
                            errorText.setText("Friend request accepted");
                        }
                    });
                }
            }
            return null;
        }
    }

    private class loadIncomingRequests extends AsyncTask<String, Void, Void> {

        private ArrayList<String> incomingRequests;
        LinearLayout linearLayout;

        @Override
        protected Void doInBackground(String... params) {
            String friendUsername = params[0];
            try {
                Log.e("Connection", "Connecting to HTTPS");
                URL url = new URL("http://10.0.2.2:4000/getIncomingRequests?id="
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
                    String result = response.toString();

                    // parsing file "JSONExample.json"
                    JSONObject obj = new JSONObject(result);

                    // typecasting obj to JSONObject
                    JSONObject userObject = (JSONObject) obj;

                    JSONObject profile = (JSONObject) userObject.get("profile");
                    JSONArray arrIncoming = (JSONArray) profile.get("received");

                    // getting firstName and lastName
                    incomingRequests = new ArrayList<String>();
                    for(int i = 0; i < arrIncoming.length(); i++){
                        if (!currUser.friends.contains(arrIncoming.getString(i))) {
                            incomingRequests.add(arrIncoming.getString(i));
                        }
                    }

                    Log.e("Recieved requests: ", incomingRequests.toString());

                    conn.disconnect();
                    in.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            linearLayout = findViewById(R.id.LinearLayout);
                            for( int i = 0; i < incomingRequests.size(); i++ )
                            {
                                TextView textView = new TextView(ManageFriendsActivity.this);
                                textView.setText(incomingRequests.get(i));
                                textView.setTextSize(15);
                                textView.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        v.setVisibility(View.GONE);
                                        currUser.friends.add(textView.getText().toString());
                                        new acceptRequest().execute(textView.getText().toString());
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

    private class addFriend extends AsyncTask<String, Void, Void> {

        private String result;

        @Override
        protected Void doInBackground(String...params) {
            String friendUsername = params[0];
            if (currUser.getUsername().equals(friendUsername)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        errorText.setText("Unfortunately, you can't friend yourself");
                    }
                });
                return null;
            }

            if (currUser.getFriends().contains(friendUsername)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        errorText.setText("You're already friends with "+ friendUsername);
                    }
                });
                return null;
            }
            try {
                Log.e("Connection", "Connecting to HTTPS");
                URL url = new URL("http://10.0.2.2:4000/sendFriendRequestMobile?id="
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
                            errorText.setText(result);
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
                            errorText.setText("Friend request sent");
                        }
                    });
                }
            }
            return null;
        }


    }
}
