package com.github.songnick;

import android.app.Application;
import android.content.Context;

/**
 * Created by qfsong on 16/1/13.
 */
public class AndroidServiceApp extends Application {

    private static Application sContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
    }

    public static Context getContext(){

        return sContext;
    }
}
