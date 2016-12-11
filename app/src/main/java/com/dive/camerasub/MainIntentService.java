package com.dive.camerasub;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.database.CursorJoiner;
import android.os.Bundle;
import android.os.ResultReceiver;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MainIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_TAKE_PICTURES = "com.dive.camerasub.action.TAKE_PICTURES";
    private static final String ACTION_FETCH_CONFIG = "com.dive.camerasub.action.FETCH_CONFIG";

    // TODO: Rename parameters
    private static final String EXTRA_PICTURE_NUMBER = "com.dive.camerasub.extra.PICTURE_NUMBER";

    public MainIntentService() {
        super("MainIntentService");
    }

    private ResultReceiver _receiver;
    private int _count = 0;
    private boolean _isRunning = false;

    public boolean isRunning(){
        return this._isRunning;
    }

    @Override
    public void onCreate() {
        super.onCreate(); // if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionTakePictures(Context context, int param1, ResultReceiver receiver) {
        Intent intent = new Intent(context, MainIntentService.class);
        intent.setAction(ACTION_TAKE_PICTURES);
        intent.putExtra(EXTRA_PICTURE_NUMBER, param1);
        intent.putExtra("receiver", receiver);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionFetchConfig(Context context) {
        Intent intent = new Intent(context, MainIntentService.class);
        intent.setAction(ACTION_FETCH_CONFIG);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_TAKE_PICTURES.equals(action)) {
                final int param1 = intent.getIntExtra(EXTRA_PICTURE_NUMBER, 1);
                this._receiver = intent.getParcelableExtra("receiver");
                handleActionTakePictures(param1, this._receiver);
            } else if (ACTION_FETCH_CONFIG.equals(action)) {
                handleActionFetchConfig();
            }

            this._isRunning = false;
            this.sendStatus();
            super.stopSelf();
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionTakePictures(int param1, ResultReceiver receiver) {
        this._isRunning = true;
        this.sendStatus();

        int count = 0;

        try {
            while (count < param1) {
                count++;
                this.sendCount(count);
                Thread.sleep(5000);
            }
        }catch(InterruptedException ex){

        }

    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFetchConfig() {
        this._isRunning = true;
        this.sendStatus();

        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void sendStatus(){
        Bundle bundle = new Bundle();
        bundle.putString("type", "status");
        bundle.putBoolean("isRunning", this._isRunning);
        this._receiver.send(Activity.RESULT_OK, bundle);
    }

    private void sendCount(int count){
        Bundle bundle = new Bundle();
        bundle.putInt("count", count);
        bundle.putString("type", "count");
        // Here we call send passing a resultCode and the bundle of extras
        this._receiver.send(Activity.RESULT_OK, bundle);
    }
}
