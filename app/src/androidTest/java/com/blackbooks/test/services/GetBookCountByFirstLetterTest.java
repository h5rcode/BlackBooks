package com.blackbooks.test.services;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.BookServices;

import junit.framework.Assert;

/**
 * Test class of service {@link com.blackbooks.services.BookServices#getBookCountByFirstLetter(android.database.sqlite.SQLiteDatabase, String)}.
 */
public class GetBookCountByFirstLetterTest extends AbstractDatabaseTest {

    /**
     * Make sure the method returns the expected value.
     */
    public void testGetBookCountByFirstLetter() {

        BookInfo bookInfo1 = new BookInfo();
        bookInfo1.title = "A";
        BookServices.saveBookInfo(getDb(), bookInfo1);

        BookInfo bookInfo2 = new BookInfo();
        bookInfo2.title = "a";
        BookServices.saveBookInfo(getDb(), bookInfo2);

        int bookCount = BookServices.getBookCountByFirstLetter(getDb(), "A");

        Assert.assertEquals(2, bookCount);
    }
}
