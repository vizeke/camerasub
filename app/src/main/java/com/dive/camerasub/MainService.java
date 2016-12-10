package com.dive.camerasub;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MainService extends Service {

    private boolean isRunning;

    public boolean isRunning() {
        return isRunning;
    }

    public MainService() {
        this.isRunning = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void toggleService(){
        this.isRunning = !this.isRunning;
    }

    public void startService(){
        this.isRunning = true;
    }

    public void stopService() {
        this.isRunning = false;
    }
}
