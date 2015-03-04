package com.blackbooks.test.services;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.BookServices;
import com.blackbooks.test.data.Books;
import com.blackbooks.test.data.Seriez;

import junit.framework.Assert;

/**
 * Test class of service {@link com.blackbooks.services.BookServices#getBookCountBySeries(android.database.sqlite.SQLiteDatabase, Long)}.
 */
public class GetBookCountBySeriesTest extends AbstractDatabaseTest {

    /**
     * Make sure the method returns the expected value.
     */
    public void testGetBookCountBySeries() {

        BookInfo bookInfo1 = new BookInfo();
        bookInfo1.title = Books.HARRY_POTTER_AND_THE_CHAMBER_OF_SECRETS;
        bookInfo1.series.name = Seriez.HARRY_POTTER;
        BookServices.saveBookInfo(getDb(), bookInfo1);

        BookInfo bookInfo2 = new BookInfo();
        bookInfo2.title = Books.HARRY_POTTER_AND_THE_CHAMBER_OF_SECRETS;
        bookInfo2.series.name = Seriez.HARRY_POTTER;
        BookServices.saveBookInfo(getDb(), bookInfo2);

        int bookCount = BookServices.getBookCountBySeries(getDb(), bookInfo1.seriesId);

        Assert.assertEquals(2, bookCount);
    }
}
