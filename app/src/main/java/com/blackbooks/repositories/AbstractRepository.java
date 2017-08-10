package com.blackbooks.repositories;

import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.database.SQLiteHelper;

public abstract class AbstractRepository {

    private final SQLiteHelper sqLiteHelper;

    protected AbstractRepository(SQLiteHelper sqLiteHelper) {

        this.sqLiteHelper = sqLiteHelper;
    }

    protected SQLiteDatabase getReadableDatabase() {
        return sqLiteHelper.getReadableDatabase();
    }

    protected SQLiteDatabase getWritableDatabase() {
        return sqLiteHelper.getWritableDatabase();
    }
}
