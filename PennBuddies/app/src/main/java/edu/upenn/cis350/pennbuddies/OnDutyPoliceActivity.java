package edu.upenn.cis350.pennbuddies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
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
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class OnDutyPoliceActivity extends AppCompatActivity {

    TextView view1;
    TextView view2;
    TextView view3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ondutypolice);
        view1 = (TextView) findViewById(R.id.onDuty1);
        view2 = (TextView) findViewById(R.id.onDuty2);
        view3 = (TextView) findViewById(R.id.onDuty3);
    }

    public void on34thAndWalnutClick(View v){
        view1.setText("Officer John Smith: 9 AM - 3 PM");
        view2.setText("Officer Bob Williams: 3 PM - 9 PM");
    }

    public void on40thAndWalnutClick(View v){
        view1.setText("Officer Robert James: 3 PM - 9 PM");
        view2.setText("");
    }

    public void on39thAndSansomClick(View v){
        view1.setText("Officer Thomas Brown: 3 PM - 9 PM");
        view2.setText("");
    }
}
