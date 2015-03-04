package com.blackbooks.test.services;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.BookServices;
import com.blackbooks.test.data.Books;
import com.blackbooks.test.data.People;

import junit.framework.Assert;

import java.util.Date;
import java.util.List;

/**
 * Test class of {@link com.blackbooks.services.BookServices#getBookInfoListByLoanedTo(android.database.sqlite.SQLiteDatabase, String, int, int)}.
 */
public class GetBookInfoListByLoanedToTest extends AbstractDatabaseTest {

    /**
     * Make sure the method returns the expected result.
     */
    public void testGetBookInfoListByLoanedTo() {

        BookInfo bookInfo1 = new BookInfo();
        bookInfo1.title = Books.THE_GOLDEN_BOUGH;
        bookInfo1.loanedTo = People.JOHN_DOE;
        bookInfo1.loanDate = new Date();
        BookServices.saveBookInfo(getDb(), bookInfo1);

        BookInfo bookInfo2 = new BookInfo();
        bookInfo2.title = Books.HEART_OF_DARKNESS;
        bookInfo2.loanedTo = People.JOHN_DOE;
        bookInfo2.loanDate = new Date();
        BookServices.saveBookInfo(getDb(), bookInfo2);

        List<BookInfo> bookInfoList = BookServices.getBookInfoListByLoanedTo(getDb(), People.JOHN_DOE, Integer.MAX_VALUE, 0);

        Assert.assertEquals(2, bookInfoList.size());

        BookInfo bookInfoResult1 = bookInfoList.get(0);
        BookInfo bookInfoResult2 = bookInfoList.get(1);

        Assert.assertEquals(bookInfo2.id.longValue(), bookInfoResult1.id.longValue());
        Assert.assertEquals(bookInfo1.id.longValue(), bookInfoResult2.id.longValue());
    }
}
