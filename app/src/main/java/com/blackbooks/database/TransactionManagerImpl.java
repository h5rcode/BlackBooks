package com.blackbooks.database;

import android.database.sqlite.SQLiteDatabase;

public class TransactionManagerImpl implements TransactionManager {

    private final SQLiteDatabase sqLiteDatabase;

    public TransactionManagerImpl(SQLiteDatabase sqLiteDatabase) {

        this.sqLiteDatabase = sqLiteDatabase;
    }

    @Override
    public void beginTransaction() {
        sqLiteDatabase.beginTransaction();
    }

    @Override
    public void endTransaction() {
        sqLiteDatabase.endTransaction();
    }

    @Override
    public void setTransactionSuccessful() {
        sqLiteDatabase.setTransactionSuccessful();
    }
}
