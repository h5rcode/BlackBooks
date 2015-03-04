package com.blackbooks.test.services;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.BookServices;
import com.blackbooks.test.data.BookLocations;
import com.blackbooks.test.data.Books;

import junit.framework.Assert;

/**
 * Test class of service {@link com.blackbooks.services.BookServices#getBookCountByBookLocation(android.database.sqlite.SQLiteDatabase, Long)}.
 */
public class GetBookCountByBookLocationTest extends AbstractDatabaseTest {

    /**
     * Make sure the method returns the expected value.
     */
    public void testGetBookCountByBookLocation() {

        BookInfo bookInfo1 = new BookInfo();
        bookInfo1.title = Books.LE_MYTHE_DE_SISYPHE;
        bookInfo1.bookLocation.name = BookLocations.LIVING_ROOM;
        BookServices.saveBookInfo(getDb(), bookInfo1);

        BookInfo bookInfo2 = new BookInfo();
        bookInfo2.title = Books.LA_PESTE;
        bookInfo2.bookLocation.name = BookLocations.LIVING_ROOM;
        BookServices.saveBookInfo(getDb(), bookInfo2);

        BookInfo bookInfo3 = new BookInfo();
        bookInfo3.title = Books.HEART_OF_DARKNESS;
        bookInfo3.bookLocation.name = BookLocations.LIVING_ROOM;
        BookServices.saveBookInfo(getDb(), bookInfo3);

        int bookCount = BookServices.getBookCountByBookLocation(getDb(), bookInfo1.bookLocationId);

        Assert.assertEquals(3, bookCount);
    }
}
