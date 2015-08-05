package com.blackbooks.services;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.fts.BookFTS;
import com.blackbooks.sql.BrokerManager;

import java.util.List;

/**
 * Full-Text-Search services.
 */
public final class FullTextSearchServices {

    /**
     * Private constructor.
     */
    private FullTextSearchServices() {
    }

    /**
     * Return the number of books matching a query.
     *
     * @param db    SQLiteDatabase.
     * @param query The searched text.
     * @return The number of books matching the query.
     */
    public static int getSearchResultCount(SQLiteDatabase db, String query) {
        String select = "SELECT COUNT(*) FROM " + BookFTS.NAME + " book_fts WHERE book_fts MATCH ?;";

        Cursor cursor = db.rawQuery(select, new String[]{query});
        cursor.moveToNext();
        int resultCount = cursor.getInt(0);
        cursor.close();
        return resultCount;
    }

    /**
     * Search the books whose title matched the query.
     *
     * @param db     SQLiteDatabase.
     * @param query  The searched text.
     * @param limit  Limit.
     * @param offset Offset.
     * @return The books whose title contains the given query.
     */
    public static List<BookInfo> searchBooks(SQLiteDatabase db, String query, int limit, int offset) {

        String sql = "SELECT book." + Book.Cols.BOO_ID + ", book." + Book.Cols.BOO_TITLE + ", book." + Book.Cols.BOO_SUBTITLE
                + ", book." + Book.Cols.BOO_DESCRIPTION + " FROM " + BookFTS.NAME + " book_fts JOIN " + Book.NAME
                + " book ON book." + Book.Cols.BOO_ID + " = book_fts." + BookFTS.Cols.DOCID + " WHERE book_fts MATCH ? ORDER BY "
                + Book.Cols.BOO_TITLE + " COLLATE NOCASE LIMIT ? OFFSET ?;";

        String selection[] = new String[]{
                query,
                String.valueOf(limit),
                String.valueOf(offset)
        };
        List<Book> bookList = BrokerManager.getBroker(Book.class).rawSelect(db, sql, selection);
        return BookServices.getBookInfoListFromBookList(db, bookList);
    }

}
