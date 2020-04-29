package edu.upenn.cis350.pennbuddies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_Code = 99;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            checkUserLocationPermission();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);

        // Add a marker in Penn
        LatLng penn = new LatLng(39.9509461, -75.1935127);

        mMap.addMarker(new MarkerOptions().position(penn).title("University of Pennsylvania"));
       // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(penn, 15f));

        // current location marker
        MarkerOptions markerOptions = new MarkerOptions();
        LatLng curr = new LatLng(39.9532, -75.2013);
        markerOptions.position(curr);
        markerOptions.title("User Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        currentUserLocationMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(curr));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curr, 15f));



        //Add markers for College Houses
        LatLng harnwell = new LatLng(39.9524, -75.2002);
        LatLng hill = new LatLng(39.9530, -75.1907);
        LatLng radian = new LatLng(39.9524, -75.2012);
        LatLng quad = new LatLng(39.950806, -75.197306);

        //Add markers for Academic buildings
        LatLng drl = new LatLng(39.9519, -75.1900);
        LatLng towne = new LatLng(39.9517, -75.1912);
        LatLng vanpelt = new LatLng(39.9527, -75.1940);
        LatLng huntsman = new LatLng(39.9522, -75.1983);

        //Add markers for other important buildings
        LatLng houston = new LatLng(39.9509, -75.1939);
        LatLng hospital = new LatLng(39.9501, -75.1939);

        mMap.addMarker(new MarkerOptions().position(harnwell).title("Harnwell College House"));
        mMap.addMarker(new MarkerOptions().position(hill).title("Hill College House"));
        mMap.addMarker(new MarkerOptions().position(radian).title("The Radian"));
        mMap.addMarker(new MarkerOptions().position(quad).title("The Quadrangle"));
        mMap.addMarker(new MarkerOptions().position(drl).title("David Rittenhouse Labaratory"));
        mMap.addMarker(new MarkerOptions().position(towne).title("Towne Building"));
        mMap.addMarker(new MarkerOptions().position(vanpelt).title("Van Pelt Library"));
        mMap.addMarker(new MarkerOptions().position(huntsman).title("Huntsman"));
        mMap.addMarker(new MarkerOptions().position(houston).title("Houston Hall"));
        mMap.addMarker(new MarkerOptions().position(hospital).title("Hospital of UPenn"));

        //add call box lcoations
        try {
            String[] locations;
            double[] latitudes;
            double[] longitudes;

            Log.e("ConnectionCallBox", "Connecting to HTTPS");
            URL url = new URL("http://10.0.2.2:4000/getCallBoxLocations?id=");
            //URL url = new URL("localhost:4000/getCallBoxLocations");
            Log.e("Connection", "Connected");

            InputStream inputStream;
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responsecode = conn.getResponseCode();
            if (responsecode != 200) {
                throw new IllegalStateException();
            } else {
                int count = 0;
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

                locations = new String[jsonEntries.length];
                latitudes = new double[jsonEntries.length];
                longitudes = new double[jsonEntries.length];

                for (String entry : jsonEntries){

                    JSONObject object = new JSONObject(entry);
                    JSONObject callBox = (JSONObject)object;

                    String location = (String)callBox.get("location");
                    double latitude = (double) callBox.get("latitude");
                    double longitude = (double) callBox.get("longitude");

                    locations[count] = location;
                    latitudes[count] = latitude;
                    longitudes[count] = longitude;
                    count++;
                }

                for (int i = 0; i < locations.length; i++){
                    MarkerOptions marker = new MarkerOptions();
                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    mMap.addMarker(marker.position(new LatLng(latitudes[i], longitudes[i])).title(locations[i]));
                }

                in.close();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        //add police officer locations based on current time of day
        try {
            String[] names;
            String[] startTimes;
            String[] endTimes;
            double[] latitudes;
            double[] longitudes;

            Log.e("ConnectionOfficer", "Connecting to HTTPS");
            URL url = new URL("http://10.0.2.2:4000/getOfficerLocations?id=");
            //URL url = new URL("localhost:4000/getCallBoxLocations");
            Log.e("Connection", "Connected");

            InputStream inputStream;
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responsecode = conn.getResponseCode();
            if (responsecode != 200) {
                throw new IllegalStateException();
            } else {
                int count = 0;
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

                names = new String[jsonEntries.length];
                startTimes = new String[jsonEntries.length];
                endTimes = new String[jsonEntries.length];
                latitudes = new double[jsonEntries.length];
                longitudes = new double[jsonEntries.length];


                for (String entry : jsonEntries){

                    JSONObject object = new JSONObject(entry);
                    JSONObject officer = (JSONObject)object;

                    String name = (String)officer.get("name");
                    String start = (String)officer.get("start");
                    String end = (String)officer.get("end");
                    double latitude = (double) officer.get("latitude");
                    double longitude = (double) officer.get("longitude");

                    names[count] = name;
                    startTimes[count] = start;
                    endTimes[count] = end;
                    latitudes[count] = latitude;
                    longitudes[count] = longitude;
                    count++;
                }

                for (int i = 0; i < names.length; i++){
                    //get current time
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
                    String currTime = sdf.format(date);
                    currTime = currTime.substring(0, 2) + " " + currTime.substring(currTime.length()-2);
                    currTime = "06 PM";

                    if (startTimes[i].length() == 4){
                        startTimes[i] = "0" + startTimes[i];
                    }
                    if (endTimes[i].length() == 4){
                        endTimes[i] = "0" + endTimes[i];
                    }

                    if (startTimes[i].substring(startTimes[i].length()-2).equals("PM") && endTimes[i].substring(endTimes[i].length()-2).equals("PM")
                        && currTime.substring(currTime.length()-2).equals("AM")){
                        continue;
                    }

                    if (startTimes[i].substring(startTimes[i].length()-2).equals("AM") && endTimes[i].substring(endTimes[i].length()-2).equals("AM")
                            && currTime.substring(currTime.length()-2).equals("PM")){
                        continue;
                    }

                    if (startTimes[i].substring(startTimes[i].length()-2).equals("AM") && endTimes[i].substring(endTimes[i].length()-2).equals("PM")
                            && currTime.substring(currTime.length()-2).equals("AM") && currTime.compareTo(startTimes[i]) > 0){
                        MarkerOptions marker = new MarkerOptions();
                        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                        mMap.addMarker(marker.position(new LatLng(latitudes[i], longitudes[i])).title("Officer " + names[i]));
                    } else if (startTimes[i].substring(startTimes[i].length()-2).equals("AM") && endTimes[i].substring(endTimes[i].length()-2).equals("PM")
                            && currTime.substring(currTime.length()-2).equals("PM") && currTime.compareTo(endTimes[i]) < 0){
                        MarkerOptions marker = new MarkerOptions();
                        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                        mMap.addMarker(marker.position(new LatLng(latitudes[i], longitudes[i])).title("Officer " + names[i]));
                    } else if (startTimes[i].substring(startTimes[i].length()-2).equals("PM") && endTimes[i].substring(endTimes[i].length()-2).equals("AM")
                            && currTime.substring(currTime.length()-2).equals("AM") && currTime.compareTo(endTimes[i]) < 0){
                        MarkerOptions marker = new MarkerOptions();
                        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                        mMap.addMarker(marker.position(new LatLng(latitudes[i], longitudes[i])).title("Officer " + names[i]));
                    } else if(startTimes[i].substring(startTimes[i].length()-2).equals("PM") && endTimes[i].substring(endTimes[i].length()-2).equals("AM")
                            && currTime.substring(currTime.length()-2).equals("PM") && currTime.compareTo(startTimes[i]) > 0){
                        MarkerOptions marker = new MarkerOptions();
                        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                        mMap.addMarker(marker.position(new LatLng(latitudes[i], longitudes[i])).title("Officer " + names[i]));
                    } else if(startTimes[i].substring(startTimes[i].length()-2).equals("PM") && endTimes[i].substring(endTimes[i].length()-2).equals("PM")
                            && currTime.substring(currTime.length()-2).equals("PM") && currTime.compareTo(startTimes[i]) > 0 &&
                            currTime.compareTo(endTimes[i]) < 0){
                        MarkerOptions marker = new MarkerOptions();
                        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                        mMap.addMarker(marker.position(new LatLng(latitudes[i], longitudes[i])).title("Officer " + names[i]));
                    } else if (startTimes[i].substring(startTimes[i].length()-2).equals("AM") && endTimes[i].substring(endTimes[i].length()-2).equals("AM")
                            && currTime.substring(currTime.length()-2).equals("AM") && currTime.compareTo(startTimes[i]) > 0 &&
                            currTime.compareTo(endTimes[i]) < 0){
                        MarkerOptions marker = new MarkerOptions();
                        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                        mMap.addMarker(marker.position(new LatLng(latitudes[i], longitudes[i])).title("Officer " + names[i]));
                    }
                }

                in.close();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkUserLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            } else{
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            }
            return false;
        } else{
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Request_User_Location_Code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        if (googleApiClient == null){
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else{
                    Toast.makeText(this, "Permission Denied...", Toast.LENGTH_SHORT).show();
                }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        googleApiClient.connect();

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

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        if (currentUserLocationMarker != null){
            currentUserLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("User Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        currentUserLocationMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));

        if (googleApiClient != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this::onLocationChanged);
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        //locationRequest.setInterval(1100);
        //locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this::onLocationChanged);

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
