package com.blackbooks.dependencies.components;

import com.blackbooks.BlackBooksApplication;
import com.blackbooks.dependencies.modules.BlackBooksApplicationModule;

import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;

@Component(modules = {AndroidInjectionModule.class, BlackBooksApplicationModule.class})
public interface BlackBooksApplicationComponent extends AndroidInjector<BlackBooksApplication> {
}
