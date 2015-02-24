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
        String createTableIsbn = "CREATE TABLE ISBN  (\n" +
                "\tISB_DATE_ADDED INTEGER NOT NULL,\n" +
                "\tISB_ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\tISB_LOOKED_UP INTEGER NOT NULL,\n" +
                "\tISB_NUMBER TEXT NOT NULL UNIQUE,\n" +
                "\tISB_SEARCH_SUCCESSFUL INTEGER NOT NULL\n" +
                ");";
        db.execSQL(createTableIsbn);
    }
}
