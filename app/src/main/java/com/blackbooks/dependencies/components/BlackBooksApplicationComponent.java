package com.blackbooks.dependencies.components;

import android.app.Application;

import com.blackbooks.BlackBooksApplication;
import com.blackbooks.dependencies.modules.BlackBooksApplicationModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;

@Singleton
@Component(modules = {AndroidInjectionModule.class, BlackBooksApplicationModule.class})
public interface BlackBooksApplicationComponent extends AndroidInjector<BlackBooksApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        BlackBooksApplicationComponent build();
    }
}
