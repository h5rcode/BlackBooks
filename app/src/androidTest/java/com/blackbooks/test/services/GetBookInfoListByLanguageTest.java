package com.blackbooks.test.services;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.BookServices;
import com.blackbooks.test.data.Books;
import com.blackbooks.test.data.Languages;

import junit.framework.Assert;

import java.util.List;

/**
 * Test class of {@link com.blackbooks.services.BookServices#getBookInfoListByLanguage(android.database.sqlite.SQLiteDatabase, String, int, int)}.
 */
public class GetBookInfoListByLanguageTest extends AbstractDatabaseTest {

    /**
     * Make sure the method returns the expected result.
     */
    public void testGetBookInfoListByLanguage() {

        BookInfo bookInfo1 = new BookInfo();
        bookInfo1.title = Books.THE_GOLDEN_BOUGH;
        bookInfo1.languageCode = Languages.ENGLISH;
        BookServices.saveBookInfo(getDb(), bookInfo1);

        BookInfo bookInfo2 = new BookInfo();
        bookInfo2.title = Books.HEART_OF_DARKNESS;
        bookInfo2.languageCode = Languages.ENGLISH;
        BookServices.saveBookInfo(getDb(), bookInfo2);

        List<BookInfo> bookInfoList = BookServices.getBookInfoListByLanguage(getDb(), Languages.ENGLISH, Integer.MAX_VALUE, 0);

        Assert.assertEquals(2, bookInfoList.size());

        BookInfo bookInfoResult1 = bookInfoList.get(0);
        BookInfo bookInfoResult2 = bookInfoList.get(1);

        Assert.assertEquals(bookInfo2.id.longValue(), bookInfoResult1.id.longValue());
        Assert.assertEquals(bookInfo1.id.longValue(), bookInfoResult2.id.longValue());
    }
}
