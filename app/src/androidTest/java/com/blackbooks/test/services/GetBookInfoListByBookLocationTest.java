package com.blackbooks.test.services;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.BookServices;
import com.blackbooks.test.data.BookLocations;
import com.blackbooks.test.data.Books;

import junit.framework.Assert;

import java.util.List;

/**
 * Test class of {@link com.blackbooks.services.BookServices#getBookInfoListByBookLocation(android.database.sqlite.SQLiteDatabase, long, int, int)}.
 */
public class GetBookInfoListByBookLocationTest extends AbstractDatabaseTest {

    /**
     * Make sure the method returns the expected result.
     */
    public void testGetBookInfoListByBookLocation() {

        BookInfo bookInfo1 = new BookInfo();
        bookInfo1.title = Books.CASINO_ROYALE;
        bookInfo1.bookLocation.name = BookLocations.LIVING_ROOM;
        BookServices.saveBookInfo(getDb(), bookInfo1);

        BookInfo bookInfo2 = new BookInfo();
        bookInfo2.title = Books.THE_CATCHER_IN_THE_RYE;
        bookInfo2.bookLocation.name = BookLocations.LIVING_ROOM;
        BookServices.saveBookInfo(getDb(), bookInfo2);

        List<BookInfo> bookInfoList = BookServices.getBookInfoListByBookLocation(getDb(), bookInfo1.bookLocation.id, Integer.MAX_VALUE, 0);

        Assert.assertEquals(2, bookInfoList.size());

        BookInfo bookInfoResult1 = bookInfoList.get(0);
        BookInfo bookInfoResult2 = bookInfoList.get(1);

        Assert.assertEquals(bookInfo1.id.longValue(), bookInfoResult1.id.longValue());
        Assert.assertEquals(bookInfo2.id.longValue(), bookInfoResult2.id.longValue());
    }
}
