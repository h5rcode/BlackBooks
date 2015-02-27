package com.blackbooks.services;

import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.BookAuthor;
import com.blackbooks.sql.BrokerManager;

import java.util.List;

/**
 * Author services.
 */
public class AuthorServices {

    /**
     * Delete the authors that are not referred by any books in the database.
     *
     * @param db SQLiteDatabase.
     */
    public static void deleteAuthorsWithoutBooks(SQLiteDatabase db) {
        String sql = "DELETE FROM " + Author.NAME + " WHERE " + Author.Cols.AUT_ID + " IN (SELECT aut." + Author.Cols.AUT_ID
                + " FROM " + Author.NAME + " aut LEFT JOIN " + BookAuthor.NAME + " bka ON bka." + BookAuthor.Cols.AUT_ID
                + " = aut." + Author.Cols.AUT_ID + " WHERE bka." + BookAuthor.Cols.BKA_ID + " IS NULL)";

        db.execSQL(sql);
    }

    /**
     * Get an author from the database.
     *
     * @param db SQLiteDatabase.
     * @param id Id of the author.
     * @return Author.
     */
    public static Author getAuthor(SQLiteDatabase db, long id) {
        return BrokerManager.getBroker(Author.class).get(db, id);
    }

    /**
     * Get the one row matching a criteria. If no rows or more that one rows
     * match the criteria, the method returns null.
     *
     * @param db       SQLiteDatabase.
     * @param criteria The search criteria.
     * @return Author.
     */
    public static Author getAuthorByCriteria(SQLiteDatabase db, Author criteria) {
        return BrokerManager.getBroker(Author.class).getByCriteria(db, criteria);
    }

    /**
     * Get the list of authors whose name contains a given text.
     *
     * @param db   SQLiteDatabase.
     * @param text Text.
     * @return List of Author.
     */
    public static List<Author> getAuthorListByText(SQLiteDatabase db, String text) {
        String sql = "SELECT * FROM " + Author.NAME + " WHERE LOWER(" + Author.Cols.AUT_NAME
                + ") LIKE '%' || LOWER(?) || '%' ORDER BY " + Author.Cols.AUT_NAME;
        String[] selectionArgs = {text};
        return BrokerManager.getBroker(Author.class).rawSelect(db, sql, selectionArgs);
    }

    /**
     * Save an author in the database.
     *
     * @param db     SQLiteDatabase.
     * @param author Author.
     * @return Id of the saved author.
     */
    public static long saveAuthor(SQLiteDatabase db, Author author) {
        return BrokerManager.getBroker(Author.class).save(db, author);
    }
}
