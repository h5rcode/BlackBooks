package com.blackbooks.services;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.Series;
import com.blackbooks.sql.BrokerManager;

import java.util.List;

/**
 * Series services.
 */
public final class SeriesServices {

    /**
     * Delete a series.
     *
     * @param db       SQLiteDatabase.
     * @param seriesId Id of the series.
     */
    public static void deleteSeries(SQLiteDatabase db, long seriesId) {
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(Book.Cols.SER_ID, (String) null);
            String whereClause = Book.Cols.SER_ID + " = ?";
            String[] whereArgs = new String[]{String.valueOf(seriesId)};
            db.updateWithOnConflict(Book.NAME, values, whereClause, whereArgs, SQLiteDatabase.CONFLICT_ROLLBACK);

            BrokerManager.getBroker(Series.class).delete(db, seriesId);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Delete the publishers that are not referred by any books in the database.
     *
     * @param db SQLiteDatabase.
     */
    public static void deleteSeriesWithoutBooks(SQLiteDatabase db) {
        String sql = "DELETE FROM " + Series.NAME + " WHERE " + Series.Cols.SER_ID + " IN (SELECT ser." + Series.Cols.SER_ID
                + " FROM " + Series.NAME + " ser LEFT JOIN " + Book.NAME + " boo ON boo." + Book.Cols.SER_ID + " = ser."
                + Series.Cols.SER_ID + " WHERE boo." + Book.Cols.BOO_ID + " IS NULL)";

        db.execSQL(sql);
    }

    /**
     * Get the one row matching a criteria. If no rows or more that one rows
     * match the criteria, the method returns null.
     *
     * @param db       SQLiteDatabase.
     * @param criteria The search criteria.
     * @return Series.
     */
    public static Series getSeriesByCriteria(SQLiteDatabase db, Series criteria) {
        return BrokerManager.getBroker(Series.class).getByCriteria(db, criteria);
    }

    /**
     * Get the list of series whose name contains a given text.
     *
     * @param db   SQLiteDatabase.
     * @param text Text.
     * @return List of Series.
     */
    public static List<Series> getSeriesListByText(SQLiteDatabase db, String text) {
        String sql = "SELECT * FROM " + Series.NAME + " WHERE LOWER(" + Series.Cols.SER_NAME
                + ") LIKE '%' || LOWER(?) || '%' ORDER BY " + Series.Cols.SER_NAME;
        String[] selectionArgs = {text};
        return BrokerManager.getBroker(Series.class).rawSelect(db, sql, selectionArgs);
    }

    /**
     * Save a series.
     *
     * @param db     SQLiteDatabase.
     * @param series Series.
     * @return Id of the saved Series.
     */
    public static long saveSeries(SQLiteDatabase db, Series series) {
        return BrokerManager.getBroker(Series.class).save(db, series);
    }

    /**
     * Get a series from the database.
     *
     * @param db    SQLiteDatabase.
     * @param serId Id of the series.
     * @return Series.
     */
    public static Series getSeries(SQLiteDatabase db, long serId) {
        return BrokerManager.getBroker(Series.class).get(db, serId);
    }

    /**
     * Update a series.
     *
     * @param db       SQLiteDatabase.
     * @param seriesId Id of the series.
     * @param newName  New name.
     */
    public static void updateSeries(SQLiteDatabase db, long seriesId, String newName) {
        ContentValues values = new ContentValues();
        values.put(Series.Cols.SER_NAME, newName);
        String whereClause = Series.Cols.SER_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(seriesId)};
        db.updateWithOnConflict(Series.NAME, values, whereClause, whereArgs, SQLiteDatabase.CONFLICT_ROLLBACK);
    }
}
