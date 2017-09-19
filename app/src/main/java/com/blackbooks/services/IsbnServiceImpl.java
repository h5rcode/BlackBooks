package com.blackbooks.services;

import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Isbn;
import com.blackbooks.repositories.AbstractRepository;
import com.blackbooks.repositories.IsbnRepository;
import com.blackbooks.sql.Broker;
import com.blackbooks.sql.BrokerManager;

import java.util.Date;
import java.util.List;

/**
 * ISBN services.
 */
public final class IsbnServiceImpl extends AbstractRepository implements IsbnService {

    private final BookService bookService;
    private final IsbnRepository isbnRepository;

    public IsbnServiceImpl(BookService bookService, IsbnRepository isbnRepository, SQLiteHelper sqLiteHelper) {
        super(sqLiteHelper);
        this.bookService = bookService;
        this.isbnRepository = isbnRepository;
    }

    /**
     * Delete all the pending ISBNs.
     */
    public void deleteAllPendingIsbns() {
        isbnRepository.deleteAllPendingIsbns();
    }

    /**
     * Get the list of all the ISBNs to look up.
     *
     * @return List of ISBNs to look up.
     */
    public List<Isbn> getIsbnListToLookUp(int limit, int offset) {
        return isbnRepository.getIsbnListToLookUp(limit, offset);
    }

    /**
     * Get the list of all the ISBNs to look up.
     *
     * @return ISBN to look up count.
     */
    public int getIsbnListToLookUpCount() {
        return isbnRepository.getIsbnListToLookUpCount();
    }

    /**
     * Save an ISBN. If it already in the database, just update the date.
     *
     * @param number String.
     */
    public void saveIsbn(String number) {
        SQLiteDatabase db = getWritableDatabase();
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
            isbn.lookedUp = 0L;
            isbn.bookId = null;

            broker.save(db, isbn);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void saveBookInfo(BookInfo bookInfo, long isbnId) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            bookService.saveBookInfo(bookInfo);
            markIsbnLookedUp(isbnId, bookInfo.id);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Mark an ISBN as looked up.
     *
     * @param isbnId Id of the ISBN.
     * @param bookId Id of the book that has been found (may be null if the search was not successful).
     */
    public void markIsbnLookedUp(long isbnId, Long bookId) {
        isbnRepository.markIsbnLookedUp(isbnId, bookId);
    }
}
