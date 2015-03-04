package com.blackbooks.test.services;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.BookServices;
import com.blackbooks.test.data.Books;

import junit.framework.Assert;

import java.util.List;

/**
 * Test class of {@link com.blackbooks.services.BookServices#getBookInfoListToRead(android.database.sqlite.SQLiteDatabase, int, int)}.
 */
public class GetBookInfoListToReadTest extends AbstractDatabaseTest {

    /**
     * Make sure the method returns the expected result.
     */
    public void testGetBookInfoListToRead() {

        BookInfo bookInfo1 = new BookInfo();
        bookInfo1.title = Books.HARRY_POTTER_AND_THE_CHAMBER_OF_SECRETS;
        BookServices.saveBookInfo(getDb(), bookInfo1);

        BookInfo bookInfo2 = new BookInfo();
        bookInfo2.title = Books.HARRY_POTTER_AND_THE_CHAMBER_OF_SECRETS;
        BookServices.saveBookInfo(getDb(), bookInfo2);

        List<BookInfo> bookInfoList = BookServices.getBookInfoListToRead(getDb(), Integer.MAX_VALUE, 0);

        Assert.assertEquals(2, bookInfoList.size());

        BookInfo bookInfoResult1 = bookInfoList.get(0);
        BookInfo bookInfoResult2 = bookInfoList.get(1);

        Assert.assertEquals(bookInfo1.id.longValue(), bookInfoResult1.id.longValue());
        Assert.assertEquals(bookInfo2.id.longValue(), bookInfoResult2.id.longValue());
    }
}
