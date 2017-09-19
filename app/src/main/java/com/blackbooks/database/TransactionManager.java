package com.blackbooks.database;

public interface TransactionManager {

    void beginTransaction();

    void endTransaction();

    void setTransactionSuccessful();
}
