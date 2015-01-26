package com.blackbooks.test.services;

import com.blackbooks.model.nonpersistent.AuthorInfo;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.nonpersistent.SeriesInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.Series;
import com.blackbooks.services.AuthorServices;
import com.blackbooks.services.BookServices;
import com.blackbooks.test.data.Authors;
import com.blackbooks.test.data.Books;
import com.blackbooks.test.data.Seriez;

import junit.framework.Assert;

import java.util.List;

/**
 * Test of getAuthorInfoList.
 */
public class GetAuthorInfoListTest extends AbstractDatabaseTest {

    /**
     * Test case where there are two books of the same series and of the same
     * author in the database.
     */
    public void testGetAuthorInfoListOneAuthorOneSeriesTwoBooks() {

        Author author = new Author();
        author.name = Authors.J_K_ROWLING;

        Series series = new Series();
        series.name = Seriez.HARRY_POTTER;

        BookInfo bookInfo1 = new BookInfo();
        bookInfo1.title = Books.HARRY_POTTER_AND_THE_PHILOSOPHER_S_STONE;
        bookInfo1.authors.add(author);
        bookInfo1.series = series;
        bookInfo1.number = 1L;
        BookServices.saveBookInfo(getDb(), bookInfo1);

        BookInfo bookInfo2 = new BookInfo();
        bookInfo2.title = Books.HARRY_POTTER_AND_THE_CHAMBER_OF_SECRETS;
        bookInfo2.authors.add(author);
        bookInfo2.series = series;
        bookInfo2.number = 2L;
        BookServices.saveBookInfo(getDb(), bookInfo2);

        List<AuthorInfo> authorInfoList = AuthorServices.getAuthorInfoList(getDb());

        Assert.assertEquals(1, authorInfoList.size());
        AuthorInfo authorInfo = authorInfoList.get(0);

        Assert.assertEquals(author.id.longValue(), authorInfo.id.longValue());
        Assert.assertEquals(author.name, authorInfo.name);

        Assert.assertEquals(1, authorInfo.series.size());
        SeriesInfo seriesInfo = authorInfo.series.get(0);

        Assert.assertEquals(bookInfo1.seriesId.longValue(), seriesInfo.id.longValue());
        Assert.assertEquals(bookInfo1.series.name, seriesInfo.name);

        Assert.assertEquals(2, seriesInfo.books.size());
        Book bookDb1 = seriesInfo.books.get(0);
        Book bookDb2 = seriesInfo.books.get(1);

        Assert.assertEquals(bookInfo1.id.longValue(), bookDb1.id.longValue());
        Assert.assertEquals(bookInfo1.number, bookDb1.number);
        Assert.assertEquals(bookInfo1.title, bookDb1.title);
        Assert.assertEquals(bookInfo1.description, bookDb1.description);

        Assert.assertEquals(bookInfo2.id.longValue(), bookDb2.id.longValue());
        Assert.assertEquals(bookInfo2.number, bookDb2.number);
        Assert.assertEquals(bookInfo2.title, bookDb2.title);
        Assert.assertEquals(bookInfo2.description, bookDb2.description);
    }

    /**
     * Test case where there are two books with an unspecified author in the
     * database.
     */
    public void testGetAuthorInfoListTwoBooksWithoutAuthor() {
        BookInfo bookInfo1 = new BookInfo();
        bookInfo1.title = Books.THE_VOYNICH_MANUSCRIPT;
        BookServices.saveBookInfo(getDb(), bookInfo1);

        BookInfo bookInfo2 = new BookInfo();
        bookInfo2.title = Books.BEOWULF;
        BookServices.saveBookInfo(getDb(), bookInfo2);

        List<AuthorInfo> authorInfoList = AuthorServices.getAuthorInfoList(getDb());

        Assert.assertEquals(1, authorInfoList.size());
        AuthorInfo authorInfo = authorInfoList.get(0);
        Assert.assertNull(authorInfo.id);
        Assert.assertNull(authorInfo.name);
        Assert.assertEquals(1, authorInfo.series.size());

        SeriesInfo seriesInfo = authorInfo.series.get(0);
        Assert.assertNull(seriesInfo.id);
        Assert.assertNull(seriesInfo.name);
        Assert.assertEquals(2, seriesInfo.books.size());

        Book bookDb1 = seriesInfo.books.get(0);
        Book bookDb2 = seriesInfo.books.get(1);

        Assert.assertEquals(bookInfo2.id.longValue(), bookDb1.id.longValue());
        Assert.assertEquals(bookInfo2.title, bookDb1.title);

        Assert.assertEquals(bookInfo1.id.longValue(), bookDb2.id.longValue());
        Assert.assertEquals(bookInfo1.title, bookDb2.title);
    }
}
