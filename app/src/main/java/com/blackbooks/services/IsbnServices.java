package com.blackbooks.services;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Isbn;
import com.blackbooks.sql.Broker;
import com.blackbooks.sql.BrokerManager;

import java.util.Date;
import java.util.List;

/**
 * ISBN services.
 */
public class IsbnServices {

    /**
     * Delete all the looked up ISBNs.
     *
     * @param db SQLiteDatabase.
     */
    public static void deleteAllLookedUpIsbns(SQLiteDatabase db) {
        String whereClause = Isbn.Cols.ISB_LOOKED_UP + " = ?";
        String[] whereArgs = new String[]{String.valueOf(1L)};
        db.delete(Isbn.NAME, whereClause, whereArgs);
    }

    /**
     * Delete all the pending ISBNs.
     *
     * @param db SQLiteDatabase.
     */
    public static void deleteAllPendingIsbns(SQLiteDatabase db) {
        String whereClause = Isbn.Cols.ISB_LOOKED_UP + " = ?";
        String[] whereArgs = new String[]{String.valueOf(0L)};
        db.delete(Isbn.NAME, whereClause, whereArgs);
    }

    /**
     * Get the list of all the ISBNs that have been looked up.
     *
     * @param db     SQLiteDatabase.
     * @param limit  Limit.
     * @param offset Offset.
     */
    public static List<Isbn> getIsbnListLookedUp(SQLiteDatabase db, int limit, int offset) {
        String sql = "SELECT * FROM " + Isbn.NAME + " WHERE " + Isbn.Cols.ISB_LOOKED_UP + " = 1 ORDER BY " + Isbn.Cols.ISB_DATE_ADDED + " LIMIT ? OFFSET ?;";
        String[] selectionArgs = new String[]{
                String.valueOf(limit),
                String.valueOf(offset)
        };
        return BrokerManager.getBroker(Isbn.class).rawSelect(db, sql, selectionArgs);
    }

    /**
     * Get the list of all the ISBNs to look up.
     *
     * @param db SQLiteDatabase.
     */
    public static List<Isbn> getIsbnListToLookUp(SQLiteDatabase db, int limit, int offset) {
        String sql = "SELECT * FROM " + Isbn.NAME + " WHERE " + Isbn.Cols.ISB_LOOKED_UP + " = 0 ORDER BY " + Isbn.Cols.ISB_DATE_ADDED + " LIMIT ? OFFSET ?;";
        String[] selectionArgs = new String[]{
                String.valueOf(limit),
                String.valueOf(offset)
        };
        return BrokerManager.getBroker(Isbn.class).rawSelect(db, sql, selectionArgs);
    }

    /**
     * Save an ISBN. If it already in the database, just update the date.
     *
     * @param db     SQLiteDatabase.
     * @param number String.
     */
    public static void saveIsbn(SQLiteDatabase db, String number) {
        db.beginTransaction();
        try {
            Broker<Isbn> broker = BrokerManager.getBroker(Isbn.class);

            Isbn criteria = new Isbn();
            criteria.number = number;
            criteria.lookedUp = null;
            Isbn isbn = broker.getByCriteria(db, criteria);

            if (isbn == null) {
                isbn = new Isbn();
                isbn.number = number;
            }
            isbn.dateAdded = new Date();

            broker.save(db, isbn);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Save a BookInfo and mark the corresponding ISBN as looked up.
     *
     * @param db       SQLiteDatabase.
     * @param bookInfo BookInfo.
     * @param isbnId   Id of the ISBN.
     */
    public static void saveBookInfo(SQLiteDatabase db, BookInfo bookInfo, long isbnId) {
        db.beginTransaction();
        try {
            BookServices.saveBookInfo(db, bookInfo);
            markIsbnLookedUp(db, isbnId, bookInfo.id);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Mark an ISBN as looked up.
     *
     * @param db     SQLiteDatabase.
     * @param isbnId Id of the ISBN.
     * @param bookId Id of the book that has been found (may be null if the search was not successful).
     */
    public static void markIsbnLookedUp(SQLiteDatabase db, long isbnId, Long bookId) {
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
}
