package edu.upenn.cis350.pennbuddies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.local.LocalMongoDbService;

import org.bson.BsonString;
import org.bson.Document;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class BuildingHoursActivity extends AppCompatActivity {

    TextView hoursView;
    HashMap<String, String> buildingHours = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buildinghours);
        hoursView = (TextView)findViewById(R.id.hoursText);
        new getHours().execute();
    }

    private class getHours extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                Log.e("Connection", "Connecting to HTTPS");
                URL url = new URL("http://10.0.2.2:4000/getBuildingHours?id=");
                Log.e("Connection", "Connected");

                InputStream inputStream;
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
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
                    while ((currentLine = in.readLine()) != null){
                        response.append(currentLine);
                    }

                    String result = makeParsableString(response.toString());
                    result = result.substring(1, result.length()-1);

                    String[] jsonEntries = result.split("&");

                    for (String entry : jsonEntries){
                        JSONObject object = new JSONObject(entry);
                        JSONObject building = (JSONObject)object;

                        String name = building.getString("name");
                        String hours = building.getString("hours");

                        buildingHours.put(name, hours);
                    }
                    in.close();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public String makeParsableString(String input) {
        StringBuilder line = new StringBuilder(input);
        boolean inCurlyBracket = false;
        for (int i = 0; i < line.length(); i++) {
            char currChar = line.charAt(i);
            if (currChar == '{') {
                inCurlyBracket = true;
            }
            if (!inCurlyBracket && currChar == ',') {
                line.setCharAt(i, '&');
            }
            if (currChar == '}') {
                inCurlyBracket = false;
            }
        }
        return line.toString();
    }

    public void on1920CommonsClick(View v) {
        hoursView.setText(buildingHours.get("Class of 1920 Commons"));
    }

    public void onArchClick(View v){
        hoursView.setText(buildingHours.get("the ARCH"));
    }

    public void onCohenClick(View v){
        hoursView.setText(buildingHours.get("Claudia Cohen Hall"));
    }

    public void onFaginClick(View v){
        hoursView.setText(buildingHours.get("Claire M. Fagin Hall"));
    }

    public void onBookstoreClick(View v){
        hoursView.setText(buildingHours.get("Penn Bookstore"));
    }

    public void onCollegeClick(View v){
        hoursView.setText(buildingHours.get("College Hall"));
    }

    public void onDRLClick(View v){
        hoursView.setText(buildingHours.get("David Rittenhouse Laboratory"));
    }

    public void onFisherClick(View v){
        hoursView.setText(buildingHours.get("Fisher Hassenfeld College House"));
    }

    public void onHighRiseClick(View v){
        hoursView.setText(buildingHours.get("High Rises"));
    }

    public void onHillClick(View v){
        hoursView.setText(buildingHours.get("Hill College House"));
    }

    public void onHoustonClick(View v){
        hoursView.setText(buildingHours.get("Houston Hall"));
    }

    public void onHospitalClick(View v){
        hoursView.setText(buildingHours.get("Hospital of the University of Pennsylvania"));
    }

    public void onHunstmanClick(View v){
        hoursView.setText(buildingHours.get("Jon M. Huntsman Hall"));
    }

    public void onKCHClick(View v){
        hoursView.setText(buildingHours.get("Kings Court English College House"));
    }

    public void onLauderClick(View v){
        hoursView.setText(buildingHours.get("Lauder College House (NCH)"));
    }

    public void onLeidyClick(View v){
        hoursView.setText(buildingHours.get("Leidy Laboratories of Biology"));
    }

    public void onMcNeilClick(View v){
        hoursView.setText(buildingHours.get("McNeil Building"));
    }

    public void onMeyersonClick(View v){
        hoursView.setText("10 am - 10 pm");
    }

    public void onPerelmanClick(View v){
        hoursView.setText(buildingHours.get("Perelman School of Medicine"));
    }

    public void onPottruckClick(View v){
        hoursView.setText(buildingHours.get("Penn Campus Recreation (Pottruck)"));
    }

    public void onQuadClick(View v){
        hoursView.setText(buildingHours.get("Quadrangle Dormitories"));
    }

    public void onRadianClick(View v){
        hoursView.setText("Open 24 Hours");
    }

    public void onSinghClick(View v){
        hoursView.setText(buildingHours.get("Singh Center for Nanotechnology"));
    }

    public void onSkirkanichClick(View v) {
        hoursView.setText(buildingHours.get("Skirkanich Hall"));
    }

    public void onTowneClick(View v){
        hoursView.setText(buildingHours.get("Towne Building"));
    }

    public void onVPClick(View v){
        hoursView.setText(buildingHours.get("Van Pelt Library"));
    }
}
