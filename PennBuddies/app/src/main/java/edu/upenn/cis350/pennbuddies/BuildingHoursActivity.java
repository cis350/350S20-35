package edu.upenn.cis350.pennbuddies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BuildingHoursActivity extends AppCompatActivity {

    TextView view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buildinghours);
        view = (TextView)findViewById(R.id.hoursText);
    }

    public void on1920CommonsClick(View v) {
        view.setText("10 AM - 1 PM, 5 PM - 9 PM");
    }

    public void onArchClick(View v){
        view.setText("8 AM - 12 AM");
    }

    public void onCohenClick(View v){
        view.setText("8 AM - 10 PM");
    }

    public void onFaginClick(View v){
        view.setText("7 AM - 11 PM");
    }

    public void onBookstoreClick(View v){
        view.setText("8:30 AM - 9:30 PM");
    }

    public void onCollegeClick(View v){
        view.setText("7 AM - 6:30 PM");
    }

    public void onDRLClick(View v){
        view.setText("8 AM - 9 PM");
    }

    public void onFisherClick(View v){
        view.setText("8:30 AM - 12 AM");
    }

    public void onHighRiseClick(View v){
        view.setText("Open 24 hours");
    }

    public void onHillClick(View v){
        view.setText("Open 24 hours");
    }

    public void onHoustonClick(View v){
        view.setText("8 AM - 7 PM");
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
        view.setText("8 AM - 9:30 PM");
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
        view.setText("8 AM - 10 PM");
    }

    public void onQuadClick(View v){
        view.setText("Open 24 hours");
    }

    public void onRadianClick(View v){
        view.setText("Open 24 hours");
    }

    public void onSinghClick(View v){
        view.setText("8 AM - 7 PM");
    }

    public void onSkirkanichClick(View v) {
        view.setText("7 AM - 11 PM");
    }

    public void onTowneClick(View v){
        view.setText("7 AM - 11 PM");
    }

    public void onVPClick(View v){
        view.setText("8:30 AM - 12 AM");
    }
}
