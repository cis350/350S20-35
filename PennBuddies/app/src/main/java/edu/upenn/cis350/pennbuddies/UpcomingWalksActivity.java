package edu.upenn.cis350.pennbuddies;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UpcomingWalksActivity extends AppCompatActivity {
    LinearLayout linearLayout;

    User currUser;
    List<JSONObject> history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_walks);

        this.currUser = MainActivity.currUser;

        new getHistory().execute(currUser.getUsername());

    }

    private class getHistory extends AsyncTask<String, Void, Void> {

        private String result;

        @Override
        protected Void doInBackground(String... params) {
            String friendUsername = params[0];
            try {
                Log.e("Connection", "Connecting to HTTPS");
                URL url = new URL("http://10.0.2.2:4000/currentUser?id="
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
                    Log.e("Here", "HERE");
                    JSONArray arr = (JSONArray) profile.get("history");


                    history = new ArrayList<JSONObject>();

                    for(int i = 0; i < arr.length(); i++){
                        history.add(arr.getJSONObject(i));
                    }

                    in.close();

                    runOnUiThread(new Runnable() {
                        String buddyName;
                        String buddyDate;
                        @Override
                        public void run() {
                            linearLayout = findViewById(R.id.linearLayout);
                            for( int i = 0; i < history.size(); i++ )
                            {
                                TextView textView = new TextView(UpcomingWalksActivity.this);
                                JSONObject obj = history.get(i);
                                try{
                                    buddyName = (String) obj.get("name");
                                    buddyDate = (String) obj.get("date");
                                } catch (Exception e) {
                                    Log.e("Connection", e.toString());
                                }
                                currUser.history.add(buddyName);
                                textView.setText(buddyName + " on " + buddyDate);
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
