package com.blackbooks.repositories;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.persistent.Isbn;
import com.blackbooks.sql.BrokerManager;

import java.util.List;

public class IsbnRepositoryImpl extends AbstractRepository implements IsbnRepository {
    public IsbnRepositoryImpl(SQLiteHelper sqLiteHelper) {
        super(sqLiteHelper);
    }

    @Override
    public void deleteAllLookedUpIsbns() {
        String whereClause = Isbn.Cols.ISB_LOOKED_UP + " = ?";
        String[] whereArgs = new String[]{String.valueOf(1L)};
        getWritableDatabase().delete(Isbn.NAME, whereClause, whereArgs);
    }

    @Override
    public void markIsbnLookedUp(long isbnId, Long bookId) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Isbn.Cols.ISB_LOOKED_UP, 1L);
            contentValues.put(Isbn.Cols.BOO_ID, bookId);

            String whereClause = Isbn.Cols.ISB_ID + " = ?";
            String[] whereArgs = new String[]{String.valueOf(isbnId)};
            db.update(Isbn.NAME, contentValues, whereClause, whereArgs);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public int getIsbnListToLookUpCount() {
        String sql = "SELECT COUNT(*) FROM " + Isbn.NAME + " WHERE " + Isbn.Cols.ISB_LOOKED_UP + " = 0";
        Cursor cursor = getReadableDatabase().rawQuery(sql, null);
        cursor.moveToNext();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    @Override
    public List<Isbn> getIsbnListToLookUp(int limit, int offset) {
        String sql = "SELECT * FROM " + Isbn.NAME + " WHERE " + Isbn.Cols.ISB_LOOKED_UP + " = 0 ORDER BY " + Isbn.Cols.ISB_DATE_ADDED + " DESC LIMIT ? OFFSET ?;";
        String[] selectionArgs = new String[]{
                String.valueOf(limit),
                String.valueOf(offset)
        };
        return BrokerManager.getBroker(Isbn.class).rawSelect(getReadableDatabase(), sql, selectionArgs);
    }

    @Override
    public int getIsbnListLookedUpCount() {
        String sql = "SELECT COUNT(*) FROM " + Isbn.NAME + " WHERE " + Isbn.Cols.ISB_LOOKED_UP + " = 1";
        Cursor cursor = getReadableDatabase().rawQuery(sql, null);
        cursor.moveToNext();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    @Override
    public List<Isbn> getIsbnListLookedUp(int limit, int offset) {
        String sql = "SELECT * FROM " + Isbn.NAME + " WHERE " + Isbn.Cols.ISB_LOOKED_UP + " = 1 ORDER BY " + Isbn.Cols.ISB_DATE_ADDED + " DESC LIMIT ? OFFSET ?;";
        String[] selectionArgs = new String[]{
                String.valueOf(limit),
                String.valueOf(offset)
        };
        return BrokerManager.getBroker(Isbn.class).rawSelect(getReadableDatabase(), sql, selectionArgs);
    }

    @Override
    public void deleteAllPendingIsbns() {
        String whereClause = Isbn.Cols.ISB_LOOKED_UP + " = ?";
        String[] whereArgs = new String[]{String.valueOf(0L)};
        getWritableDatabase().delete(Isbn.NAME, whereClause, whereArgs);
    }
}
