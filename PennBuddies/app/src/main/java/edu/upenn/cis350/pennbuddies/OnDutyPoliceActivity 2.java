package edu.upenn.cis350.pennbuddies;

import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.core.services.StitchServiceClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteInsertOneResult;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.bson.Document;

import java.util.Date;

public class OnDutyPoliceActivity extends AppCompatActivity {
    StitchAppClient stitchAppClient;
    RemoteMongoClient remoteMongoClient;
    RemoteMongoCollection remoteMongoCollection;
    StitchUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ondutypolice);

        stitchAppClient = Stitch.getAppClient(getString(R.string.stitch_client_app_id));
        remoteMongoClient = stitchAppClient.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
        remoteMongoCollection = remoteMongoClient.getDatabase("Hours").getCollection("police officers");
        currentUser = stitchAppClient.getAuth().getUser();

        Document officer1 = new Document();
        officer1.put("name", "officer1");
        officer1.put("shift start", "12 am");
        officer1.put("shift end", "6 am");
        officer1.put("location", "39th and Walnut");
        remoteMongoCollection.insertOne(officer1);

        Document officer2 = new Document();
        officer2.put("name", "officer2");
        officer2.put("shift start", "6 am");
        officer2.put("shift end", "12 pm");
        officer2.put("location", "39th and Walnut");
        remoteMongoCollection.insertOne(officer2);

        Document officer3 = new Document();
        officer3.put("name", "officer3");
        officer3.put("shift start", "12 pm");
        officer3.put("shift end", "6 pm");
        officer3.put("location", "39th and Walnut");
        remoteMongoCollection.insertOne(officer3);

        Document officer4 = new Document();
        officer4.put("name", "officer4");
        officer4.put("shift start", "6 pm");
        officer4.put("shift end", "12 am");
        officer4.put("location", "39th and Walnut");
        remoteMongoCollection.insertOne(officer4);

        Document officer5 = new Document();
        officer5.put("name", "officer5");
        officer5.put("shift start", "12 am");
        officer5.put("shift end", "8 am");
        officer5.put("location", "Upper Quad Gate");
        remoteMongoCollection.insertOne(officer5);

        Document officer6 = new Document();
        officer6.put("name", "officer6");
        officer6.put("shift start", "8 am");
        officer6.put("shift end", "4 pm");
        officer6.put("location", "Upper Quad Gate");
        remoteMongoCollection.insertOne(officer6);

        Document officer7 = new Document();
        officer7.put("name", "officer7");
        officer7.put("shift start", "4 pm");
        officer7.put("shift end", "12 am");
        officer7.put("location", "Upper Quad Gate");
        remoteMongoCollection.insertOne(officer7);

        Document officer8 = new Document();
        officer8.put("name", "officer8");
        officer8.put("shift start", "4 am");
        officer8.put("shift end", "12 pm");
        officer8.put("location", "Hill College House");
        remoteMongoCollection.insertOne(officer8);

        Document officer9 = new Document();
        officer9.put("name", "officer9");
        officer9.put("shift start", "12 pm");
        officer9.put("shift end", "8 pm");
        officer9.put("location", "Hill College House");
        remoteMongoCollection.insertOne(officer9);

        Document officer10 = new Document();
        officer10.put("name", "officer10");
        officer10.put("shift start", "8 pm");
        officer10.put("shift end", "4 am");
        officer10.put("location", "Hill College House");
        remoteMongoCollection.insertOne(officer10);

    }
}
