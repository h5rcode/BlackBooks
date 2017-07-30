package com.blackbooks.repositories;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.BookLocation;
import com.blackbooks.sql.BrokerManager;

import java.util.List;

public class BookLocationRepositoryImpl implements BookLocationRepository {
    private final SQLiteDatabase db;

    public BookLocationRepositoryImpl(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public void deleteBookLocation(long bookLocationId) {
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(Book.Cols.BKL_ID, (String) null);
            String whereClause = Book.Cols.BKL_ID + " = ?";
            String[] whereArgs = new String[]{String.valueOf(bookLocationId)};
            db.updateWithOnConflict(Book.NAME, values, whereClause, whereArgs, SQLiteDatabase.CONFLICT_ROLLBACK);

            BrokerManager.getBroker(BookLocation.class).delete(db, bookLocationId);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void deleteBookLocationsWithoutBooks() {
        String sql = "DELETE FROM " + BookLocation.NAME + " WHERE " + BookLocation.Cols.BKL_ID + " IN (SELECT bkl."
                + BookLocation.Cols.BKL_ID + " FROM " + BookLocation.NAME + " bkl LEFT JOIN " + Book.NAME + " boo ON boo."
                + Book.Cols.BKL_ID + " = bkl." + BookLocation.Cols.BKL_ID + " WHERE boo." + Book.Cols.BOO_ID + " IS NULL)";
        db.execSQL(sql);
    }

    @Override
    public BookLocation getBookLocation(Long bookLocationId) {
        return BrokerManager.getBroker(BookLocation.class).get(db, bookLocationId);
    }

    @Override
    public BookLocation getBookLocationByCriteria(BookLocation criteria) {
        return BrokerManager.getBroker(BookLocation.class).getByCriteria(db, criteria);
    }

    @Override
    public List<BookLocation> getBookLocationListByText(String text) {
        String sql = "SELECT * FROM " + BookLocation.NAME + " WHERE LOWER(" + BookLocation.Cols.BKL_NAME
                + ") LIKE '%' || LOWER(?) || '%' ORDER BY " + BookLocation.Cols.BKL_NAME;
        String[] selectionArgs = {text};
        return BrokerManager.getBroker(BookLocation.class).rawSelect(db, sql, selectionArgs);
    }

    @Override
    public long saveBookLocation(BookLocation bookLocation) {
        return BrokerManager.getBroker(BookLocation.class).save(db, bookLocation);
    }

    @Override
    public void updateBookLocation(long bookLocationId, String newName) {
        ContentValues values = new ContentValues();
        values.put(BookLocation.Cols.BKL_NAME, newName);
        String whereClause = BookLocation.Cols.BKL_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(bookLocationId)};
        db.update(BookLocation.NAME, values, whereClause, whereArgs);
    }

    @Override
    public int getBookLocationCount() {
        String sql = "SELECT COUNT(*) FROM " + BookLocation.NAME;
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
