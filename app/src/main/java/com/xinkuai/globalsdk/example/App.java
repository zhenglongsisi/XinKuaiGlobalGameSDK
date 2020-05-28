package com.xinkuai.globalsdk.example;

import android.app.Application;
import android.content.Context;

import com.xinkuai.globalsdk.XKGlobalSDK;

/**
 * Created by Long on 2019/7/17
 */
public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        XKGlobalSDK.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        XKGlobalSDK.initialize(this);
        XKGlobalSDK.setLoggerEnable(true);
    }

}
