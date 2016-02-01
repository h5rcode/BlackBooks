package com.blackbooks.database.upgrades;


import android.database.sqlite.SQLiteDatabase;

/**
 * Upgrades the Black Books database to version 5.
 */
public final class Version5 {

    /**
     * Upgrade.
     *
     * @param db SQLiteDatabase.
     */
    public static void upgrade(SQLiteDatabase db) {
        db.execSQL("CREATE INDEX BOOK_BKL_ID ON BOOK (BKL_ID);");
        db.execSQL("CREATE INDEX BOOK_PUB_ID ON BOOK (PUB_ID);");
        db.execSQL("CREATE INDEX BOOK_SER_ID ON BOOK (SER_ID);");
        db.execSQL("CREATE INDEX BOOK_AUTHOR_AUT_ID ON BOOK_AUTHOR (AUT_ID);");
        db.execSQL("CREATE INDEX BOOK_AUTHOR_BOO_ID ON BOOK_AUTHOR (BOO_ID);");
        db.execSQL("CREATE INDEX BOOK_CATEGORY_BOO_ID ON BOOK_CATEGORY (BOO_ID);");
        db.execSQL("CREATE INDEX BOOK_CATEGORY_CAT_ID ON BOOK_CATEGORY (CAT_ID);");
        db.execSQL("CREATE INDEX ISBN_BOO_ID ON ISBN (BOO_ID);");
    }
}
