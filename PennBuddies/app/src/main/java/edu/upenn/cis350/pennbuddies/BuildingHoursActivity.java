package edu.upenn.cis350.pennbuddies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.local.LocalMongoDbService;

import org.bson.BsonString;
import org.bson.Document;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class BuildingHoursActivity extends AppCompatActivity {

    TextView view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buildinghours);
        view = (TextView)findViewById(R.id.hoursText);
    }

    public void on1920CommonsClick(View v) {
        view.setText("Breakfast/Lunch: 11:30 am - 1:30 pm, Dinner : 5 pm - 7 pm");}

    public void onArchClick(View v){
        view.setText("M - TH : 8:15 am - 12 am, F : 8:15 am - 10 pm, SS : 10 am - 10 pm");
    }

    public void onCohenClick(View v){
        view.setText("M - F : 8 am - 10 pm, Closed on Weekends");
    }

    public void onFaginClick(View v){
        view.setText("7 am - 11 pm, Weekend Hours May Vary");
    }

    public void onBookstoreClick(View v){
        view.setText("8:30 am - 9:30 pm");
    }

    public void onCollegeClick(View v){
        view.setText("M - F : 7 am - 6:30 pm, Closed on Weekends");
    }

    public void onDRLClick(View v){
        view.setText("M - TH : 9 am - 11 pm, F : 9 am - 7 pm, SS : 12 pm - 10 pm");
    }

    public void onFisherClick(View v){
        view.setText("M - F : 9 am - 5 pm, Closed on Weekends");
    }

    public void onHighRiseClick(View v){
        view.setText("Open 24 hours");
    }

    public void onHillClick(View v){
        view.setText("Open 24 hours");
    }

    public void onHoustonClick(View v){
        view.setText("M - TH : 8 am - 6 pm, F : 8 am - 3:30 pm, Closed on Weekends");
    }

    public void onHospitalClick(View v){
        view.setText("Open 24 hours");
    }

    public void onHunstmanClick(View v){
        view.setText("7 AM - 2 AM");
    }

    public void onKCHClick(View v){
        view.setText("Open 24 hours");
    }

    public void onLauderClick(View v){
        view.setText("Open 24 hours");
    }

    public void onLeidyClick(View v){
        view.setText("M - F : 7:30 am - 8:30 pm, Closed on Weekends");
    }

    public void onMcNeilClick(View v){
        view.setText("7 AM - 8:30 PM");
    }

    public void onMeyersonClick(View v){
        view.setText("???");
    }

    public void onPerelmanClick(View v){
        view.setText("8 AM - 9 PM");
    }

    public void onPottruckClick(View v){
        view.setText("6 am - 11:30 pm");
    }

    public void onQuadClick(View v){
        view.setText("Open 24 hours");
    }

    public void onRadianClick(View v){
        view.setText("Open 24 hours");
    }

    public void onSinghClick(View v){
        view.setText("M - F : 8 am - 7 pm");
    }

    public void onSkirkanichClick(View v) {
        view.setText("7 AM - 11 PM");
    }

    public void onTowneClick(View v){
        view.setText("7 AM - 11 PM");
    }

    public void onVPClick(View v){
        view.setText("Open 24 hours");
    }
}
