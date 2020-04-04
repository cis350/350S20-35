package edu.upenn.cis350.pennbuddies;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;
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
import android.view.View;

//notifications

import androidx.core.app.NotificationCompat;
import android.app.PendingIntent;

import edu.upenn.cis350.pennbuddies.ui.home.HomeFragment;

public class Buddies extends AppCompatActivity {
    LinearLayout profile;
    DrawerLayout drawer;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    Context context = this.getBaseContext();

    String username;
    String email;

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

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_hours, R.id.nav_onDutyPolice, R.id.nav_tools,
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
        TextView email = headerview.findViewById(R.id.nav_title);
        email.setText("GET EMAIL FROM DATABASE");
        LinearLayout header = (LinearLayout) headerview.findViewById(R.id.header);
        header.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picture_intent = new Intent(Buddies.this, Profile.class);
                startActivity(picture_intent);
            }
        });

    }

    //notifications
    public void sendNotification(View v) {
        startActivity(new Intent(Buddies.this, BuildingHoursActivity.class));

//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(this);
//
//        //Create the intent that’ll fire when the user taps the notification//
//
//        Intent intent = new Intent(this, Profile.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
//
//        mBuilder.setContentIntent(pendingIntent);
//
//        mBuilder.setSmallIcon(R.drawable.ic_profile_trips);
//        mBuilder.setContentTitle("My notification");
//        mBuilder.setContentText("Hello World!");
//
//        NotificationManager mNotificationManager =
//
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        mNotificationManager.notify(001, mBuilder.build());
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
//                        intent = new Intent(getApplicationContext(), HomeFragment.class);
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
//
//    }
}
