package com.dive.camerasub;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void goToSettings(View v){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private MainService mainService = new MainService();

    public void toggleMainService(View v){
        DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date currentDate = new Date();
        TextView serviceDescription = (TextView) findViewById(R.id.service_description);

        // Check if service is running
        if (this.mainService.isRunning()){
            this.mainService.stopService();
            ((Button)v).setText("Start Service");
            serviceDescription.setText("Service stoped at: " + sdf.format(currentDate));
        }else{
            this.mainService.startService();
            ((Button)v).setText("Stop Service");
            serviceDescription.setText("Service started at: " + sdf.format(currentDate));
        }
    }
}
