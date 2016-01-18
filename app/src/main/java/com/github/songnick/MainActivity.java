package com.github.songnick;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import com.github.songnick.utils.CameraUtils;
import com.github.songnick.view.ChuShouSurfaceView;

public class MainActivity extends AppCompatActivity{

    private boolean mNeedFinished = false;
    private CameraUtils mCameraUtils = null;
    private ChuShouSurfaceView mSurfaceView = null;
    private CheckBox mCameraCheckBox = null;
    private boolean mIsOpened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int id = getTaskId();
        Log.d("", " current task id MainActivity == " + id);
        findViewById(R.id.start_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(MainActivity.this, LocalService.class));
            }
        });
        Log.d("ddd", " id " + Thread.currentThread().getId());
        findViewById(R.id.bind_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindService(new Intent(MainActivity.this, LocalService.class), mServiceCon, BIND_AUTO_CREATE);
            }
        });
        findViewById(R.id.finish_app).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNeedFinished = true;
                onBackPressed();
            }
        });
        findViewById(R.id.show_window).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LocalService.class);
                intent.setAction(LocalService.ACTION_SHOW_FLOATING_WINDOW);
                startService(intent);
            }
        });
        findViewById(R.id.hide_window).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LocalService.class);
                intent.setAction(LocalService.ACTION_HIDE_FLOATING_WINDOW);
                startService(intent);

            }
        });
        findViewById(R.id.stop_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(MainActivity.this, LocalService.class));
            }
        });
    }

    private LocalService.LocalBinder mBinder = null;

    private ServiceConnection mServiceCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (LocalService.LocalBinder)service;
            Log.d("", " binder ");
            mBinder.showTip();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBinder = null;
        }
    };

}
