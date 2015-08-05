package com.blackbooks.services;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.BookLocation;
import com.blackbooks.sql.BrokerManager;

import java.util.List;

/**
 * Book location services.
 */
public final class BookLocationServices {

    /**
     * Private constructor.
     */
    private BookLocationServices() {
    }

    /**
     * Delete a book location.
     *
     * @param db             SQLiteDatabase.
     * @param bookLocationId Id of the book location.
     */
    public static void deleteBookLocation(SQLiteDatabase db, long bookLocationId) {
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

    /**
     * Delete the book locations that are not referred by any books in the
     * database.
     *
     * @param db SQLiteDatabase.
     */
    public static void deleteBookLocationsWithoutBooks(SQLiteDatabase db) {
        String sql = "DELETE FROM " + BookLocation.NAME + " WHERE " + BookLocation.Cols.BKL_ID + " IN (SELECT bkl."
                + BookLocation.Cols.BKL_ID + " FROM " + BookLocation.NAME + " bkl LEFT JOIN " + Book.NAME + " boo ON boo."
                + Book.Cols.BKL_ID + " = bkl." + BookLocation.Cols.BKL_ID + " WHERE boo." + Book.Cols.BOO_ID + " IS NULL)";
        db.execSQL(sql);
    }

    /**
     * Get a book location.
     *
     * @param db             SQLiteDatabase.
     * @param bookLocationId Id of a book location.
     * @return BookLocation.
     */
    public static BookLocation getBookLocation(SQLiteDatabase db, Long bookLocationId) {
        return BrokerManager.getBroker(BookLocation.class).get(db, bookLocationId);
    }

    /**
     * Get the one row matching a criteria. If no rows or more that one rows
     * match the criteria, the method returns null.
     *
     * @param db       SQLiteDatabase.
     * @param criteria The search criteria.
     * @return Author.
     */
    public static BookLocation getBookLocationByCriteria(SQLiteDatabase db, BookLocation criteria) {
        return BrokerManager.getBroker(BookLocation.class).getByCriteria(db, criteria);
    }

    /**
     * Get the list of book locations whose name contains a given text.
     *
     * @param db   SQLiteDatabase.
     * @param text Text.
     * @return List of BookLocation.
     */
    public static List<BookLocation> getBookLocationListByText(SQLiteDatabase db, String text) {
        String sql = "SELECT * FROM " + BookLocation.NAME + " WHERE LOWER(" + BookLocation.Cols.BKL_NAME
                + ") LIKE '%' || LOWER(?) || '%' ORDER BY " + BookLocation.Cols.BKL_NAME;
        String[] selectionArgs = {text};
        return BrokerManager.getBroker(BookLocation.class).rawSelect(db, sql, selectionArgs);
    }

    /**
     * Save a book location.
     *
     * @param db           SQLiteDatabase.
     * @param bookLocation BookLocation.
     * @return Id of the saved BookLocation.
     */
    public static long saveBookLocation(SQLiteDatabase db, BookLocation bookLocation) {
        return BrokerManager.getBroker(BookLocation.class).save(db, bookLocation);
    }

    /**
     * Update a book location.
     *
     * @param db             SQLiteDatabase.
     * @param bookLocationId Id of the book location.
     * @param newName        New name.
     */
    public static void updateBookLocation(SQLiteDatabase db, long bookLocationId, String newName) {
        ContentValues values = new ContentValues();
        values.put(BookLocation.Cols.BKL_NAME, newName);
        String whereClause = BookLocation.Cols.BKL_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(bookLocationId)};
        db.update(BookLocation.NAME, values, whereClause, whereArgs);
    }
}
