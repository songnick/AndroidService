package com.github.songnick;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.github.songnick.aidl.IMyAidlInterface;

/**
 * Created by qfsong on 16/1/7.
 */
public class RemoteService extends Service {

    private class BnBinder extends IMyAidlInterface.Stub{
        @Override
        public void startFloatWindow(String tipStr) throws RemoteException {

        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new BnBinder();
    }
}
