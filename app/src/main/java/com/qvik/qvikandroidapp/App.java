package com.qvik.qvikandroidapp;

import android.app.Application;

import io.realm.Realm;

public class App extends Application {

    private static final String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
