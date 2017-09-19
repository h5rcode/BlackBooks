package com.blackbooks.repositories;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.BookLocation;
import com.blackbooks.sql.BrokerManager;

import java.util.List;

public class BookLocationRepositoryImpl extends AbstractRepository implements BookLocationRepository {
    public BookLocationRepositoryImpl(SQLiteHelper sqLiteHelper) {
        super(sqLiteHelper);
    }

    @Override
    public void deleteBookLocation(long bookLocationId) {
        SQLiteDatabase db = getWritableDatabase();
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
        getWritableDatabase().execSQL(sql);
    }

    @Override
    public BookLocation getBookLocation(Long bookLocationId) {
        return BrokerManager.getBroker(BookLocation.class).get(getReadableDatabase(), bookLocationId);
    }

    @Override
    public BookLocation getBookLocationByCriteria(BookLocation criteria) {
        return BrokerManager.getBroker(BookLocation.class).getByCriteria(getReadableDatabase(), criteria);
    }

    @Override
    public List<BookLocation> getBookLocationListByText(String text) {
        String sql = "SELECT * FROM " + BookLocation.NAME + " WHERE LOWER(" + BookLocation.Cols.BKL_NAME
                + ") LIKE '%' || LOWER(?) || '%' ORDER BY " + BookLocation.Cols.BKL_NAME;
        String[] selectionArgs = {text};
        return BrokerManager.getBroker(BookLocation.class).rawSelect(getReadableDatabase(), sql, selectionArgs);
    }

    @Override
    public long saveBookLocation(BookLocation bookLocation) {
        return BrokerManager.getBroker(BookLocation.class).save(getWritableDatabase(), bookLocation);
    }

    @Override
    public void updateBookLocation(long bookLocationId, String newName) {
        ContentValues values = new ContentValues();
        values.put(BookLocation.Cols.BKL_NAME, newName);
        String whereClause = BookLocation.Cols.BKL_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(bookLocationId)};
        getWritableDatabase().update(BookLocation.NAME, values, whereClause, whereArgs);
    }

    @Override
    public int getBookLocationCount() {
        String sql = "SELECT COUNT(*) FROM " + BookLocation.NAME;
        return queryInt(sql);
    }

    private int queryInt(String sql) {
        Cursor cursor = getReadableDatabase().rawQuery(sql, null);
        cursor.moveToNext();
        int result = cursor.getInt(0);
        cursor.close();
        return result;
    }
}
