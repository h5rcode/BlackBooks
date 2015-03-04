package com.blackbooks.test.services;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.BookServices;
import com.blackbooks.test.data.Books;
import com.blackbooks.test.data.People;

import junit.framework.Assert;

import java.util.Date;

/**
 * Test class of service {@link com.blackbooks.services.BookServices#getBookCountByLoanedTo(android.database.sqlite.SQLiteDatabase, String)}.
 */
public class GetBookCountByLoanedToTest extends AbstractDatabaseTest {

    /**
     * Make sure the method returns the expected value.
     */
    public void testGetBookCountByLoanedTo() {

        BookInfo bookInfo1 = new BookInfo();
        bookInfo1.title = Books.HEART_OF_DARKNESS;
        bookInfo1.loanedTo = People.JOHN_DOE;
        bookInfo1.loanDate = new Date();
        BookServices.saveBookInfo(getDb(), bookInfo1);

        BookInfo bookInfo2 = new BookInfo();
        bookInfo2.title = Books.HARRY_POTTER_AND_THE_CHAMBER_OF_SECRETS;
        bookInfo2.loanedTo = People.JOHN_DOE;
        bookInfo2.loanDate = new Date();
        BookServices.saveBookInfo(getDb(), bookInfo2);

        int bookCount = BookServices.getBookCountByLoanedTo(getDb(), People.JOHN_DOE);

        Assert.assertEquals(2, bookCount);
    }
}
