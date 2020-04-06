package edu.upenn.cis350.pennbuddies;

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
import org.w3c.dom.Text;

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

    public void onMorningClick(View v){
        view1.setText("Officer 2: 39th & Walnut (5 am - 11 am)");
        view2.setText("Officer 5: Upper Quad Gate (6 am - 12 pm)");
        view3.setText("Officer 8: Hill College House (8 am - 11 am)");
    }

    public void onAfternoonClick(View v){
        view1.setText("Officer 3: 39th & Walnut (12 pm - 6 pm)");
        view2.setText("Officer 6: Upper Quad Gate (12 pm - 4 pm)");
        view3.setText("Officer 9: Hill College House (12 pm - 7 pm)");
    }

    public void onEveningClick(View v){
        view1.setText("Officer 4: 39th & Walnut (6 pm -  12 am)");
        view2.setText("Officer 7: Upper Quad Gate (4 pm - 12 am)");
        view3.setText("Officer 10: Hill College House (8 pm - 4 am)");
    }
}
