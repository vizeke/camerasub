package com.dive.camerasub;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;

/**
 * Created by vinicius.barbosa on 18/10/2017.
 */

public class AlarmService extends Fragment {

    // This value is defined and consumed by app code, so any value will work.
    // There's no significance to this sample using 0.
    public static final int REQUEST_CODE = 0;

    private AlarmManager mAlarmMgr;
    private PendingIntent mPendingIntent;

    public AlarmService(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetAlarm(getActivity());
    }

    public void SetAlarm(Context context){
        Intent intent = new Intent(context, CameraActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
        final int INTERVAL = 15000;

        mPendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, 0);
        mAlarmMgr = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        mAlarmMgr.setRepeating(alarmType, SystemClock.elapsedRealtime() + INTERVAL, INTERVAL, mPendingIntent);
    }

    public void cancelAlarm(){
        // If the alarm has been set, cancel it.
        if (mAlarmMgr != null) {
            mAlarmMgr.cancel(mPendingIntent);
        }
    }
}
