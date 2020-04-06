package edu.upenn.cis350.pennbuddies;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        // Add a marker in Penn and move the camera
        LatLng penn = new LatLng(39.9509461, -75.1935127);
        mMap.addMarker(new MarkerOptions().position(penn).title("University of Pennsylvania"));


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

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(penn, 15f));





        //mMap.moveCamera(CameraUpdateFactory.newLatLng(penn));
    }
}
