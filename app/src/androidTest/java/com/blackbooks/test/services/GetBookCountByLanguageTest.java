package com.blackbooks.test.services;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.BookServices;
import com.blackbooks.test.data.Books;
import com.blackbooks.test.data.Languages;

import junit.framework.Assert;

/**
 * Test class of service {@link com.blackbooks.services.BookServices#getBookCountByLanguage(android.database.sqlite.SQLiteDatabase, String)}.
 */
public class GetBookCountByLanguageTest extends AbstractDatabaseTest {

    /**
     * Make sure the method returns the expected value.
     */
    public void testGetBookCountByFirstLetter() {

        BookInfo bookInfo1 = new BookInfo();
        bookInfo1.title = Books.HEART_OF_DARKNESS;
        bookInfo1.languageCode = Languages.ENGLISH;
        BookServices.saveBookInfo(getDb(), bookInfo1);

        BookInfo bookInfo2 = new BookInfo();
        bookInfo2.title = Books.HARRY_POTTER_AND_THE_CHAMBER_OF_SECRETS;
        bookInfo2.languageCode = Languages.ENGLISH;
        BookServices.saveBookInfo(getDb(), bookInfo2);

        int bookCount = BookServices.getBookCountByLanguage(getDb(), Languages.ENGLISH);

        Assert.assertEquals(2, bookCount);
    }
}
