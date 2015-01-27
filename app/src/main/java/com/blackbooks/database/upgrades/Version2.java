package com.blackbooks.database.upgrades;

import android.database.sqlite.SQLiteDatabase;

/**
 * Upgrades the Black Books database to version 2.
 */
public final class Version2 {

    /**
     * Upgrade.
     *
     * @param db SQLiteDatabase.
     */
    public static void upgrade(SQLiteDatabase db) {
        String updateEmptyBookComments = "UPDATE BOOK SET BOO_COMMENT = NULL WHERE BOO_COMMENT = '';";
        db.execSQL(updateEmptyBookComments);
    }
}
