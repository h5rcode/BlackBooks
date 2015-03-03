package com.blackbooks.database.upgrades;

import android.database.sqlite.SQLiteDatabase;

/**
 * Upgrades the Black Books database to version 4.
 */
public final class Version4 {

    /**
     * Upgrade.
     *
     * @param db SQLiteDatabase.
     */
    public static void upgrage(SQLiteDatabase db) {
        String renameSeries = "ALTER TABLE SERIES RENAME TO SERIES_TMP;";
        String renameBookLocation = "ALTER TABLE BOOK_LOCATION RENAME TO BOOK_LOCATION_TMP;";
        String renamePublisher = "ALTER TABLE PUBLISHER RENAME TO PUBLISHER_TMP;";
        String renameAuthor = "ALTER TABLE AUTHOR RENAME TO AUTHOR_TMP;";

        String createSeries = "CREATE TABLE SERIES  (\n" +
                "\tSER_ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\tSER_NAME TEXT NOT NULL UNIQUE\n" +
                ");";
        String createBookLocation = "CREATE TABLE BOOK_LOCATION  (\n" +
                "\tBKL_ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\tBKL_NAME TEXT NOT NULL UNIQUE\n" +
                ");";
        String createPublisher = "CREATE TABLE PUBLISHER  (\n" +
                "\tPUB_ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\tPUB_NAME TEXT NOT NULL UNIQUE\n" +
                ");";
        String createAuthor = "CREATE TABLE AUTHOR  (\n" +
                "\tAUT_ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\tAUT_NAME TEXT NOT NULL UNIQUE\n" +
                ");";

        String insertIntoSeries = "INSERT INTO SERIES(SER_ID, SER_NAME) SELECT SER_ID, SER_NAME FROM SERIES_TMP;";
        String insertIntoBookLocation = "INSERT INTO BOOK_LOCATION(BKL_ID, BKL_NAME) SELECT BKL_ID, BKL_NAME FROM BOOK_LOCATION_TMP;";
        String insertIntoPublisher = "INSERT INTO PUBLISHER(PUB_ID, PUB_NAME) SELECT PUB_ID, PUB_NAME FROM PUBLISHER_TMP;";
        String insertIntoAuthor = "INSERT INTO AUTHOR(AUT_ID, AUT_NAME) SELECT AUT_ID, AUT_NAME FROM AUTHOR_TMP;";

        String dropSeriesTmp = "DROP TABLE SERIES_TMP;";
        String dropBookLocationTmp = "DROP TABLE BOOK_LOCATION_TMP;";
        String dropPublisherTmp = "DROP TABLE PUBLISHER_TMP;";
        String dropAuthorTmp = "DROP TABLE AUTHOR_TMP;";

        db.execSQL(renameSeries);
        db.execSQL(renameBookLocation);
        db.execSQL(renamePublisher);
        db.execSQL(renameAuthor);

        db.execSQL(createSeries);
        db.execSQL(createBookLocation);
        db.execSQL(createPublisher);
        db.execSQL(createAuthor);

        db.execSQL(insertIntoSeries);
        db.execSQL(insertIntoBookLocation);
        db.execSQL(insertIntoPublisher);
        db.execSQL(insertIntoAuthor);

        db.execSQL(dropSeriesTmp);
        db.execSQL(dropBookLocationTmp);
        db.execSQL(dropPublisherTmp);
        db.execSQL(dropAuthorTmp);
    }
}
