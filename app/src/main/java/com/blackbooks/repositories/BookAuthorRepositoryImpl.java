package com.blackbooks.repositories;


import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.model.persistent.BookAuthor;
import com.blackbooks.sql.BrokerManager;

import java.util.List;

/**
 * Services related to the BookAuthor class.
 */
public final class BookAuthorRepositoryImpl implements BookAuthorRepository {

    private final SQLiteDatabase db;

    public BookAuthorRepositoryImpl(SQLiteDatabase db) {
        this.db = db;
    }

    /**
     * Delete all the BookAuthor relationships involving a given book.
     *
     * @param bookId Id of a book.
     */
    public void deleteBookAuthorListByBook(long bookId) {
        BookAuthor bookAuthor = new BookAuthor();
        bookAuthor.bookId = bookId;
        BrokerManager.getBroker(BookAuthor.class).deleteAllByCriteria(db, bookAuthor);
    }

    /**
     * Get the list of BookAuthor referencing an author.
     *
     * @param authorId Id of the referenced author.
     * @return List of BookAuthor.
     */
    public List<BookAuthor> getBookAuthorListByAuthor(long authorId) {
        BookAuthor bookAuthor = new BookAuthor();
        bookAuthor.authorId = authorId;
        return BrokerManager.getBroker(BookAuthor.class).getAllByCriteria(db, bookAuthor);

    }

    /**
     * Get the list of BookAuthor referencing a book.
     *
     * @param bookId Id of the referenced book.
     * @return List of BookAuthor.
     */
    public List<BookAuthor> getBookAuthorListByBook(long bookId) {
        BookAuthor bookAuthor = new BookAuthor();
        bookAuthor.bookId = bookId;
        return BrokerManager.getBroker(BookAuthor.class).getAllByCriteria(db, bookAuthor);
    }

    /**
     * Save a BookAuthor.
     *
     * @param bookAuthor BookAuthor.
     * @return Id of the saved BookAuthor.
     */
    public long saveBookAuthor(BookAuthor bookAuthor) {
        return BrokerManager.getBroker(BookAuthor.class).save(db, bookAuthor);
    }

    @Override
    public List<BookAuthor> getBookAuthorListByBooks(List<Long> bookIdList) {
        return BrokerManager.getBroker(BookAuthor.class).getAllWhereIn(db, BookAuthor.Cols.BOO_ID, bookIdList);
    }
}
