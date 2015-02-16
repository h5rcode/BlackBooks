package com.blackbooks.services;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.model.nonpersistent.Summary;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.BookLocation;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.model.persistent.Series;

/**
 * Summary services.
 */
public class SummaryServices {

    public static Summary getSummary(SQLiteDatabase db) {
        Summary summary = new Summary();

        summary.books = getBooks(db);
        summary.authors = getAuthorCount(db);
        summary.categories = getCategoryCount(db);
        summary.languages = getLanguageCount(db);
        summary.series = getSeriesCount(db);
        summary.bookLocations = getBookLocationCount(db);
        summary.toRead = getBookToReadCount(db);
        summary.loaned = getBookLoanedCount(db);
        summary.favourites = getFavouriteBooks(db);

        return summary;
    }

    private static int getBooks(SQLiteDatabase db) {
        String sql = "SELECT COUNT(*) FROM " + Book.NAME;
        return queryInt(db, sql);
    }

    /**
     * Return the number of authors in the library.
     *
     * @param db SQLiteDatabase.
     * @return Author count.
     */
    public static int getAuthorCount(SQLiteDatabase db) {
        String sql = "SELECT COUNT(*) FROM " + Author.NAME;
        return queryInt(db, sql);
    }

    /**
     * Return the number of categories in the library.
     *
     * @param db SQLiteDatabase.
     * @return Category count.
     */
    public static int getCategoryCount(SQLiteDatabase db) {
        String sql = "SELECT COUNT(*) FROM " + Category.NAME;
        return queryInt(db, sql);
    }

    /**
     * Return the number of languages in the library.
     *
     * @param db SQLiteDatabase.
     * @return Language count.
     */
    public static int getLanguageCount(SQLiteDatabase db) {
        String sql = "SELECT COUNT(DISTINCT " + Book.Cols.BOO_LANGUAGE_CODE + ") FROM " + Book.NAME;
        return queryInt(db, sql);
    }

    /**
     * Return the number of series in the library.
     *
     * @param db SQLiteDatabase.
     * @return Series count.
     */
    public static int getSeriesCount(SQLiteDatabase db) {
        String sql = "SELECT COUNT(*) FROM " + Series.NAME;
        return queryInt(db, sql);
    }

    /**
     * Return the number of book locations in the library.
     *
     * @param db SQLiteDatabase.
     * @return Book location count.
     */
    public static int getBookLocationCount(SQLiteDatabase db) {
        String sql = "SELECT COUNT(*) FROM " + BookLocation.NAME;
        return queryInt(db, sql);
    }

    /**
     * Return the number of books to read.
     *
     * @param db SQLiteDatabase.
     * @return Book to read count.
     */
    public static int getBookToReadCount(SQLiteDatabase db) {
        String sql = "SELECT COUNT(*) FROM " + Book.NAME + " WHERE " + Book.Cols.BOO_IS_READ + " = 0;";
        return queryInt(db, sql);
    }

    /**
     * Return the number of loaned books.
     *
     * @param db SQLiteDatabase.
     * @return Loaned book count.
     */
    public static int getBookLoanedCount(SQLiteDatabase db) {
        String sql = "SELECT COUNT(*) FROM " + Book.NAME + " WHERE " + Book.Cols.BOO_LOANED_TO + " IS NOT NULL;";
        return queryInt(db, sql);
    }

    /**
     * Return the number of favourite books.
     *
     * @param db SQLiteDatabase.
     * @return Favourite book count.
     */
    public static int getFavouriteBooks(SQLiteDatabase db) {
        String sql = "SELECT COUNT(*) FROM " + Book.NAME + " WHERE " + Book.Cols.BOO_IS_FAVOURITE + " = 1;";
        return queryInt(db, sql);
    }

    private static int queryInt(SQLiteDatabase db, String sql) {
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToNext();
        return cursor.getInt(0);
    }
}
