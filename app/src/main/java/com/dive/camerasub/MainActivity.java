package com.dive.camerasub;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

        setupServiceReceiver();
    }

    public void goToSettings(View v){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private MainIntentService mainIntentService = new MainIntentService();
    private MainResultReceiver receiver;
    private Intent _serviceIntent;

    public void startMainIntentService(View v){
        MainIntentService.startActionTakePictures(this, 10, this.receiver);
    }

    // Setup the callback for when data is received from the service
    public void setupServiceReceiver() {
        this.receiver = new MainResultReceiver(new Handler());
        // This is where we specify what happens when data is received from the service
        this.receiver.setReceiver(new MainResultReceiver.Receiver() {
            @Override
            public void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == RESULT_OK) {
                    String resultType = resultData.getString("type");
                    if (resultType == "status"){
                        boolean isRunning = resultData.getBoolean("isRunning");
                        setInfoService(isRunning);
                    } else if (resultType == "count"){
                        int count = resultData.getInt("count");
                        Toast.makeText(MainActivity.this, "Pictures taken:" + String.valueOf(count), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    private void setInfoService( boolean isRunning ){
        DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date currentDate = new Date();
        TextView serviceDescription = (TextView) findViewById(R.id.service_description);
        Button button = (Button) findViewById(R.id.toggle_service);

        if (isRunning){
            serviceDescription.setText("Service started at: " + sdf.format(currentDate));
            button.setText("Stop Service");
        }else {
            serviceDescription.setText("Service stoped at: " + sdf.format(currentDate));
            button.setText("Start Service");
        }
    }
}
