package com.github.songnick;

import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Created by SongNick on 15/12/22.
 */
public class LocalService extends Service {

    private static final String TAG = " LocalService";

    private FloatWindow mFloatWindow = null;

    private Notification mNotification = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mFloatWindow = new FloatWindow(this);
        Log.d(TAG, " id " + Thread.currentThread().getId());
//        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mNotification = new Notification();
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);
        mNotification.contentView = remoteViews;
        startForeground(1, mNotification);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, " onStartCommand intent " + intent);
        if (intent == null)
            throw new IllegalStateException("null intent");
        mFloatWindow.show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, " onbinder");
        return new LocalBinder();
    }


    public static class LocalBinder extends Binder{
        public void showTip(){
            Log.d(TAG, " local binder test");
        }
    }

    public static class TipActivity extends Activity{
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.tip_activity_layout);
        }
    }
}
