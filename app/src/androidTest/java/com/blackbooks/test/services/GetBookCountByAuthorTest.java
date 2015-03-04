package com.blackbooks.test.services;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.services.BookServices;
import com.blackbooks.test.data.Authors;
import com.blackbooks.test.data.Books;

import junit.framework.Assert;

/**
 * Test class of service {@link BookServices#getBookCountByAuthor(android.database.sqlite.SQLiteDatabase, long)}.
 */
public class GetBookCountByAuthorTest extends AbstractDatabaseTest {

    /**
     * Make sure the method returns the expected value.
     */
    public void testGetBookCountByAuthor() {
        Author author = new Author();
        author.name = Authors.ALBERT_CAMUS;

        BookInfo bookInfo1 = new BookInfo();
        bookInfo1.title = Books.LE_MYTHE_DE_SISYPHE;
        bookInfo1.authors.add(author);
        BookServices.saveBookInfo(getDb(), bookInfo1);

        BookInfo bookInfo2 = new BookInfo();
        bookInfo2.title = Books.LA_PESTE;
        bookInfo2.authors.add(author);
        BookServices.saveBookInfo(getDb(), bookInfo2);

        int bookCount = BookServices.getBookCountByAuthor(getDb(), author.id);

        Assert.assertEquals(2, bookCount);
    }
}
