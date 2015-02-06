package com.blackbooks.database.upgrades;

import android.database.sqlite.SQLiteDatabase;

/**
 * Upgrades the Black Books database to version 3.
 */
public final class Version3 {

    /**
     * Upgrade.
     *
     * @param db SQLiteDatabase.
     */
    public static void upgrade(SQLiteDatabase db) {
        String createTableScannedIsbn = "CREATE TABLE SCANNED_ISBN  (\n" +
                "\tSCI_ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\tSCN_ISBN TEXT NOT NULL UNIQUE,\n" +
                "\tSCI_SCAN_DATE INTEGER NOT NULL\n" +
                ");";
        db.execSQL(createTableScannedIsbn);
    }
}
