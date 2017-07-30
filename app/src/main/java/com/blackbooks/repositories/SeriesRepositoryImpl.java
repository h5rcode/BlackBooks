package com.blackbooks.repositories;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.Series;
import com.blackbooks.sql.BrokerManager;

import java.util.List;

public class SeriesRepositoryImpl implements SeriesRepository {
    private final SQLiteDatabase db;

    public SeriesRepositoryImpl(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public void deleteSeries(long seriesId) {
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

    @Override
    public void deleteSeriesWithoutBooks() {
        String sql = "DELETE FROM " + Series.NAME + " WHERE " + Series.Cols.SER_ID + " IN (SELECT ser." + Series.Cols.SER_ID
                + " FROM " + Series.NAME + " ser LEFT JOIN " + Book.NAME + " boo ON boo." + Book.Cols.SER_ID + " = ser."
                + Series.Cols.SER_ID + " WHERE boo." + Book.Cols.BOO_ID + " IS NULL)";

        db.execSQL(sql);
    }

    @Override
    public Series getSeriesByCriteria(Series criteria) {
        return BrokerManager.getBroker(Series.class).getByCriteria(db, criteria);
    }

    @Override
    public List<Series> getSeriesListByText(String text) {
        String sql = "SELECT * FROM " + Series.NAME + " WHERE LOWER(" + Series.Cols.SER_NAME
                + ") LIKE '%' || LOWER(?) || '%' ORDER BY " + Series.Cols.SER_NAME;
        String[] selectionArgs = {text};
        return BrokerManager.getBroker(Series.class).rawSelect(db, sql, selectionArgs);
    }

    @Override
    public long saveSeries(Series series) {
        return BrokerManager.getBroker(Series.class).save(db, series);
    }

    @Override
    public Series getSeries(long serId) {
        return BrokerManager.getBroker(Series.class).get(db, serId);
    }

    @Override
    public void updateSeries(long seriesId, String newName) {
        ContentValues values = new ContentValues();
        values.put(Series.Cols.SER_NAME, newName);
        String whereClause = Series.Cols.SER_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(seriesId)};
        db.updateWithOnConflict(Series.NAME, values, whereClause, whereArgs, SQLiteDatabase.CONFLICT_ROLLBACK);
    }

    @Override
    public int getSeriesCount() {
        String sql = "SELECT COUNT(*) FROM " + Series.NAME;
        return queryInt(db, sql);
    }

    private int queryInt(SQLiteDatabase db, String sql) {
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToNext();
        int result = cursor.getInt(0);
        cursor.close();
        return result;
    }
}
