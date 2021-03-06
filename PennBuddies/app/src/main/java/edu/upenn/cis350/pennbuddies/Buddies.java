package edu.upenn.cis350.pennbuddies;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.auth.StitchAuth;
import com.mongodb.stitch.android.core.auth.StitchUser;

import com.google.android.material.navigation.NavigationView;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;

//notifications

import androidx.core.app.NotificationCompat;
import android.app.PendingIntent;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Buddies extends AppCompatActivity {
    LinearLayout profile;
    DrawerLayout drawer;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    Context context = this.getBaseContext();

    public User currUser;

    String username;
    String emailString;

    private StitchAppClient stitchClient;
    private RemoteMongoClient mongoClient;
    private RemoteMongoCollection itemsCollection;

    private AppBarConfiguration mAppBarConfiguration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddies);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        this.currUser = MainActivity.currUser;
        emailString = currUser.getEmail();

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_onDutyPolice, R.id.nav_tools,
                R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

//
//        stitchClient = Stitch.getDefaultAppClient();
//        mongoClient = stitchClient.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
////        purchasesCollection = mongoClient.getDatabase("store").getCollection("purchases");
//
//        initInstances();

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerview = navigationView.getHeaderView(0);
        TextView username = headerview.findViewById(R.id.nav_title);
        TextView email = headerview.findViewById(R.id.nav_subtitle);

        email.setText(emailString);
        username.setText(currUser.getName());

        LinearLayout header = (LinearLayout) headerview.findViewById(R.id.header);
        header.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Buddies.this, Profile.class);
                intent.putExtra("EMAIL", emailString);
                startActivity(intent);
            }
        });

        new getRecentRequest().execute(currUser.getUsername());
    }

    private class getRecentRequest extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String friendUsername = params[0];
            try {
                Log.e("Connection", "Connecting to HTTPS");
                URL url = new URL("http://10.0.2.2:4000/getRecentAcceptedBuddyRequest?id="
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

                    while ((currentLine = in.readLine()) != null){
                        response.append(currentLine);
                    }
                    String info = response.toString();
                    info = info.substring(1, info.length()-2);
                    info = info.substring(11);
                    Log.e("info", info);
                    JSONObject object = new JSONObject(info);
                    JSONObject requestInfo = (JSONObject)object;

                    String name;
                    if (requestInfo.has("friend")){
                        name = (String) requestInfo.getString("friend");
                    } else{
                        name = (String)requestInfo.getString("user");
                    }
                    String date = (String) requestInfo.getString("date");

                    in.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            NotificationManager mNotificationManager =
                                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                NotificationChannel channel = new NotificationChannel("YOUR_CHANNEL_ID",
                                        "YOUR_CHANNEL_NAME",
                                        NotificationManager.IMPORTANCE_DEFAULT);
                                channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DESCRIPTION");
                                mNotificationManager.createNotificationChannel(channel);
                            }
                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "YOUR_CHANNEL_ID")
                                    .setSmallIcon(R.drawable.logo) // notification icon
                                    .setContentTitle("Your walking buddy request was accepted!") // title for notification
                                    .setContentText(name + " accepted on " + date + "!")// message for notification
                                    .setAutoCancel(true); // clear notification after click
                            Intent intent = new Intent(getApplicationContext(), RequestBuddyActivity.class);
                            PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            mBuilder.setContentIntent(pi);
                            mNotificationManager.notify(0, mBuilder.build());
                        }
                    });

                }
            } catch (Exception e) {
                Log.e("Connection", e.toString());
            }
            return null;
        }
    }

    public void goRequestBuddy(View v) {
        startActivity(new Intent(Buddies.this, RequestBuddyActivity.class));
    }

    public void buildingHours(View v) {
        startActivity(new Intent(Buddies.this, BuildingHoursActivity.class));
    }

    public void sendPoliceNotification(View v){
        startActivity(new Intent(Buddies.this, OnDutyPoliceActivity.class));
    }

    public void sendMapsNotification(View v){
        startActivity(new Intent(Buddies.this, MapsActivity.class));
    }

    public void testNotifications(View v) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("YOUR_CHANNEL_ID",
                    "YOUR_CHANNEL_NAME",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DESCRIPTION");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "YOUR_CHANNEL_ID")
                .setSmallIcon(R.drawable.logo) // notification icon
                .setContentTitle("Your buddy is almost there!") // title for notification
                .setContentText("Your buddy is 2 minutes away.")// message for notification
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(getApplicationContext(), Buddies.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(0, mBuilder.build());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.buddies, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e("Selection:", "Menu");
        int id = item.getItemId();
        if (id == R.id.logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        StitchUser user;
        final StitchAppClient client = Stitch.getAppClient(getString(R.string.stitch_client_app_id));
        StitchAuth auth = client.getAuth();
        startActivity(new Intent(Buddies.this, MainActivity.class));
        auth.logout();
        Log.e("logout?", "Logout succeeded");
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

//    private void initInstances() {
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(drawerToggle);
//
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(MenuItem menuItem) {
//                int id = menuItem.getItemId();
//                Intent intent;
//                switch (id) {
//                    case R.id.nav_home:
//                        intent = new Intent(getApplicationContext(), Buddies.class);
//                        startActivity(intent);
//                        break;
//                    case R.id.nav_hours:
//                        intent = new Intent(getApplicationContext(), BuildingHoursActivity.class);
//                        startActivity(intent);
//                        break;
//                    case R.id.nav_onDutyPolice:
//                        intent = new Intent(getApplicationContext(), OnDutyPoliceActivity.class);
//                        startActivity(intent);
//                        break;
//                }
//                return false;
//            }
//        });
//    }
}
