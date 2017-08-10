package com.blackbooks;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.dependencies.components.DaggerBlackBooksApplicationComponent;
import com.blackbooks.utils.LogUtils;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Black Books application class.
 */
public final class BlackBooksApplication extends Application implements HasActivityInjector, HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidFragmentInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerBlackBooksApplicationComponent
                .builder()
                .build()
                .inject(this);

        Log.i(LogUtils.TAG, "Application starting.");

        SQLiteHelper.initialize(getApplicationContext());
    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidFragmentInjector;
    }
}
