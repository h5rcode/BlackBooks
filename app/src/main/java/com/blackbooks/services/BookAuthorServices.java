package com.blackbooks.services;


import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.model.persistent.BookAuthor;
import com.blackbooks.sql.BrokerManager;

import java.util.List;

/**
 * Services related to the BookAuthor class.
 */
public final class BookAuthorServices {

    /**
     * Delete all the BookAuthor relationships involving a given book.
     *
     * @param db     SQLiteDatabase.
     * @param bookId Id of a book.
     */
    public static void deleteBookAuthorListByBook(SQLiteDatabase db, long bookId) {
        BookAuthor bookAuthor = new BookAuthor();
        bookAuthor.bookId = bookId;
        BrokerManager.getBroker(BookAuthor.class).deleteAllByCriteria(db, bookAuthor);
    }

    /**
     * Get the list of BookAuthor referencing an author.
     *
     * @param db       SQLiteDatabase.
     * @param authorId Id of the referenced author.
     * @return List of BookAuthor.
     */
    public static List<BookAuthor> getBookAuthorListByAuthor(SQLiteDatabase db, long authorId) {
        BookAuthor bookAuthor = new BookAuthor();
        bookAuthor.authorId = authorId;
        return BrokerManager.getBroker(BookAuthor.class).getAllByCriteria(db, bookAuthor);

    }

    /**
     * Get the list of BookAuthor referencing a book.
     *
     * @param db     SQLiteDatabase.
     * @param bookId Id of the referenced book.
     * @return List of BookAuthor.
     */
    public static List<BookAuthor> getBookAuthorListByBook(SQLiteDatabase db, long bookId) {
        BookAuthor bookAuthor = new BookAuthor();
        bookAuthor.bookId = bookId;
        return BrokerManager.getBroker(BookAuthor.class).getAllByCriteria(db, bookAuthor);
    }

    /**
     * Save a BookAuthor.
     *
     * @param db         SQLiteDatabase.
     * @param bookAuthor BookAuthor.
     * @return Id of the saved BookAuthor.
     */
    public static long saveBookAuthor(SQLiteDatabase db, BookAuthor bookAuthor) {
        return BrokerManager.getBroker(BookAuthor.class).save(db, bookAuthor);
    }
}
