package com.blackbooks.test.services;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.BookServices;
import com.blackbooks.test.data.Books;

import junit.framework.Assert;

/**
 * Test class of {@link com.blackbooks.services.BookServices#getBookSmallThumbnail(android.database.sqlite.SQLiteDatabase, long)}.
 */
public class GetBookSmallThumbnailTest extends AbstractDatabaseTest {

    /**
     * Try to load the small thumbnail of a book without thumbnail. The result is expected to
     * be null.
     */
    public void testGetBookSmallThumbnail() {
        byte[] smallThumbnail = new byte[]{0, 1, 2, 3};

        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.THE_CATCHER_IN_THE_RYE;
        bookInfo.smallThumbnail = smallThumbnail;

        BookServices.saveBookInfo(getDb(), bookInfo);

        byte[] smallThumbnailDb = BookServices.getBookSmallThumbnail(getDb(), bookInfo.id);

        Assert.assertNotNull(smallThumbnailDb);
        Assert.assertTrue(smallThumbnailDb.length > 0);
        Assert.assertEquals(smallThumbnail.length, smallThumbnailDb.length);
    }

    /**
     * Try to load the small thumbnail of a book without thumbnail. The result is expected to
     * be null.
     */
    public void testGetBookSmallThumbnailNoThumbnail() {
        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.THE_VOYNICH_MANUSCRIPT;

        BookServices.saveBookInfo(getDb(), bookInfo);

        byte[] smallThumbnail = BookServices.getBookSmallThumbnail(getDb(), bookInfo.id);

        Assert.assertNull(smallThumbnail);
    }

    /**
     * Try to load the small thumbnail of a book with an empty thumbnail. The result is expected to
     * be null.
     */
    public void testGetBookSmallThumbnailEmptyThumbnail() {
        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.THE_VOYNICH_MANUSCRIPT;
        bookInfo.smallThumbnail = new byte[]{};

        BookServices.saveBookInfo(getDb(), bookInfo);

        byte[] smallThumbnail = BookServices.getBookSmallThumbnail(getDb(), bookInfo.id);

        Assert.assertNull(smallThumbnail);
    }
}
