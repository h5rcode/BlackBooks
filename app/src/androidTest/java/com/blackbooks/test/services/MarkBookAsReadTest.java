package com.blackbooks.test.services;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.BookServices;
import com.blackbooks.test.data.Books;

import junit.framework.Assert;

/**
 * Test class for {@link com.blackbooks.services.BookServices#markBookAsRead(android.database.sqlite.SQLiteDatabase, long)}.
 */
public class MarkBookAsReadTest extends AbstractDatabaseTest {

    /**
     * Check that a book that is not read is marked as read by the method.
     */
    public void testMarkBookAsReadTrue() {
        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.THE_GOLDEN_BOUGH;

        BookServices.saveBookInfo(getDb(), bookInfo);
        BookServices.markBookAsRead(getDb(), bookInfo.id);

        BookInfo bookInfoDb = BookServices.getBookInfo(getDb(), bookInfo.id);

        Assert.assertEquals(1L, bookInfoDb.isRead.longValue());
    }

    /**
     * Check that a book that is a read is marked as not read by the method.
     */
    public void testMarkBookAsReadFalse() {
        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.THE_CATCHER_IN_THE_RYE;
        bookInfo.isRead = 1L;

        BookServices.saveBookInfo(getDb(), bookInfo);
        BookServices.markBookAsRead(getDb(), bookInfo.id);

        BookInfo bookInfoDb = BookServices.getBookInfo(getDb(), bookInfo.id);

        Assert.assertEquals(0L, bookInfoDb.isRead.longValue());
    }
}
