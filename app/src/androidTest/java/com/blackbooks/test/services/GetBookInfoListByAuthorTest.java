package com.blackbooks.test.services;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.services.BookServices;
import com.blackbooks.test.data.Authors;
import com.blackbooks.test.data.Books;

import junit.framework.Assert;

import java.util.List;

/**
 * Test class of {@link com.blackbooks.services.BookServices#getBookInfoListByAuthor(android.database.sqlite.SQLiteDatabase, long, int, int)}.
 */
public class GetBookInfoListByAuthorTest extends AbstractDatabaseTest {

    /**
     * Make sure the method returns the expected result.
     */
    public void testGetBookInfoListByAuthor() {
        Author author = new Author();
        author.name = Authors.J_K_ROWLING;

        BookInfo bookInfo1 = new BookInfo();
        bookInfo1.title = Books.HARRY_POTTER_AND_THE_CHAMBER_OF_SECRETS;
        bookInfo1.authors.add(author);

        BookServices.saveBookInfo(getDb(), bookInfo1);

        BookInfo bookInfo2 = new BookInfo();
        bookInfo2.title = Books.HARRY_POTTER_AND_THE_PHILOSOPHER_S_STONE;
        bookInfo2.authors.add(author);

        BookServices.saveBookInfo(getDb(), bookInfo2);

        List<BookInfo> bookInfoList = BookServices.getBookInfoListByAuthor(getDb(), author.id, Integer.MAX_VALUE, 0);

        Assert.assertEquals(2, bookInfoList.size());

        BookInfo bookInfoResult1 = bookInfoList.get(0);
        BookInfo bookInfoResult2 = bookInfoList.get(1);

        Assert.assertEquals(bookInfo1.id.longValue(), bookInfoResult1.id.longValue());
        Assert.assertEquals(bookInfo2.id.longValue(), bookInfoResult2.id.longValue());
    }
}
