package com.blackbooks.test.services;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.BookServices;
import com.blackbooks.test.data.Books;

import junit.framework.Assert;

/**
 * Test class of {@link com.blackbooks.services.BookServices#markBookAsFavourite(android.database.sqlite.SQLiteDatabase, long)}.
 */
public class MarkBookAsFavouriteTest extends AbstractDatabaseTest {

    /**
     * Check that a book that is not a favourite is marked as favourite by the method.
     */
    public void testMarkBookAsFavouriteTrue() {
        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.THE_CATCHER_IN_THE_RYE;

        BookServices.saveBookInfo(getDb(), bookInfo);
        BookServices.markBookAsFavourite(getDb(), bookInfo.id);

        BookInfo bookInfoDb = BookServices.getBookInfo(getDb(), bookInfo.id);

        Assert.assertEquals(1L, bookInfoDb.isFavourite.longValue());
    }

    /**
     * Check that a book that is a favourite is marked as not favourite by the method.
     */
    public void testMarkBookAsFavouriteFalse() {
        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.THE_CATCHER_IN_THE_RYE;
        bookInfo.isFavourite = 1L;

        BookServices.saveBookInfo(getDb(), bookInfo);
        BookServices.markBookAsFavourite(getDb(), bookInfo.id);

        BookInfo bookInfoDb = BookServices.getBookInfo(getDb(), bookInfo.id);

        Assert.assertEquals(0L, bookInfoDb.isFavourite.longValue());
    }
}
