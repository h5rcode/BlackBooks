package com.blackbooks.database;

public class TransactionManagerImpl implements TransactionManager {

    private final SQLiteHelper sqLiteHelper;

    public TransactionManagerImpl(SQLiteHelper sqLiteHelper) {
        this.sqLiteHelper = sqLiteHelper;
    }

    @Override
    public void beginTransaction() {
        sqLiteHelper.getWritableDatabase().beginTransaction();
    }

    @Override
    public void endTransaction() {
        sqLiteHelper.getWritableDatabase().endTransaction();
    }

    @Override
    public void setTransactionSuccessful() {
        sqLiteHelper.getWritableDatabase().setTransactionSuccessful();
    }
}
