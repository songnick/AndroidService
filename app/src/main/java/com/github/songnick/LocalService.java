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
import android.widget.Toast;

import com.github.songnick.floatingwindow.FloatWindow;

/**
 * Created by SongNick on 15/12/22.
 */
public class LocalService extends Service {

    public static final String ACTION_SHOW_FLOATING_WINDOW = "show_floating_window";
    public static final String ACTION_HIDE_FLOATING_WINDOW = "hide_floating_window";

    private static final String TAG = " LocalService";

    private FloatWindow mFloatWindow = null;

    private Notification mNotification = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mFloatWindow = new FloatWindow(this);
        Log.d(TAG, " id " + Thread.currentThread().getId());
        mNotification = new Notification();
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);
        mNotification.contentView = remoteViews;
//        startForeground(1, mNotification);
        Log.d(TAG, " onCreate coming");
        showToast("Service onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int ret = super.onStartCommand(intent, flags, startId);
        showToast("onStartCommand");
        Log.d(TAG, " onStartCommand intent " + intent + "ret == " + ret);
        if (intent == null){
            Toast.makeText(this, "null intent", Toast.LENGTH_LONG).show();
        }else {
            String action = intent.getAction();
            if (ACTION_SHOW_FLOATING_WINDOW.equals(action)){
                mFloatWindow.show();
            }else if (ACTION_HIDE_FLOATING_WINDOW.equals(action)){
                mFloatWindow.hide();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Servcie onDestroy", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Toast.makeText(this, " on task removed ", Toast.LENGTH_LONG).show();
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
            Log.d(TAG, " current task id TipActivity == " + getTaskId());
        }
    }
}
