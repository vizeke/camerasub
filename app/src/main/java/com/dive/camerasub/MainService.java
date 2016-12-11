package com.dive.camerasub;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Date;

public class MainService extends Service {

    private boolean isRunning;
    private Date lastUpdate;

    public boolean isRunning() {
        return isRunning;
    }

    public MainService() {
    }

    @Override
    public void onCreate(){
        super.onCreate();

        this.isRunning = false;
        this.lastUpdate = null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        this.lastUpdate = new Date();
        this.isRunning = true;

        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void stopService() {
        this.lastUpdate = new Date();
        this.isRunning = false;
    }

    private void doSomething(){

    }
}
