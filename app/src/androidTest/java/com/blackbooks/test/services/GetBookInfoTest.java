package com.blackbooks.test.services;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.services.BookServices;
import com.blackbooks.test.data.Authors;
import com.blackbooks.test.data.BookLocations;
import com.blackbooks.test.data.Books;
import com.blackbooks.test.data.Categories;
import com.blackbooks.test.data.Languages;
import com.blackbooks.test.data.Publishers;
import com.blackbooks.test.data.Seriez;

import junit.framework.Assert;

import java.util.Date;

/**
 * Test class of {@link BookServices#getBookInfo(android.database.sqlite.SQLiteDatabase, long)} ()}.
 */
public class GetBookInfoTest extends AbstractDatabaseTest {

    /**
     * Test of getBookInfo.
     */
    public void testGetBookInfo() {
        Author author = new Author();
        author.name = Authors.IAN_FLEMING;

        Category category = new Category();
        category.name = Categories.SPY_FICTION;

        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.CASINO_ROYALE;
        bookInfo.subtitle = Books.CASINO_ROYALE;
        bookInfo.authors.add(author);
        bookInfo.categories.add(category);
        bookInfo.series.name = Seriez.JAMES_BOND;
        bookInfo.number = 1L;
        bookInfo.publisher.name = Publishers.JONATHAN_CAPE;
        bookInfo.publishedDate = new Date();
        bookInfo.pageCount = 213L;
        bookInfo.languageCode = Languages.ENGLISH;
        bookInfo.description = "James Bond, the finest gambler in the service, has a deadly new mission: to outplay Le Chiffre and shatter his Soviet cell";
        bookInfo.isbn10 = "0099575973";
        bookInfo.isbn13 = "9780099575979";
        bookInfo.isRead = 1L;
        bookInfo.isFavourite = 0L;
        bookInfo.bookLocation.name = BookLocations.LIVING_ROOM;

        BookServices.saveBookInfo(getDb(), bookInfo);

        BookInfo bookInfoDb = BookServices.getBookInfo(getDb(), bookInfo.id);

        Assert.assertNotNull(bookInfoDb);

        Assert.assertNotNull(bookInfoDb.id);
        Assert.assertNotNull(bookInfoDb.title);
        Assert.assertNotNull(bookInfoDb.subtitle);
        Assert.assertNotNull(bookInfoDb.seriesId);
        Assert.assertNotNull(bookInfoDb.number);
        Assert.assertNotNull(bookInfoDb.publisherId);
        Assert.assertNotNull(bookInfoDb.pageCount);
        Assert.assertNotNull(bookInfoDb.languageCode);
        Assert.assertNotNull(bookInfoDb.description);
        Assert.assertNotNull(bookInfoDb.isbn10);
        Assert.assertNotNull(bookInfoDb.isbn13);
        Assert.assertNotNull(bookInfoDb.isRead);
        Assert.assertNotNull(bookInfoDb.isFavourite);
        Assert.assertNotNull(bookInfoDb.bookLocationId);

        Assert.assertEquals(bookInfo.id, bookInfoDb.id);
        Assert.assertEquals(bookInfo.title, bookInfoDb.title);
        Assert.assertEquals(bookInfo.subtitle, bookInfoDb.subtitle);
        Assert.assertEquals(bookInfo.seriesId, bookInfoDb.seriesId);
        Assert.assertEquals(bookInfo.number, bookInfoDb.number);
        Assert.assertEquals(bookInfo.publisherId, bookInfoDb.publisherId);
        Assert.assertEquals(bookInfo.pageCount, bookInfoDb.pageCount);
        Assert.assertEquals(bookInfo.languageCode, bookInfoDb.languageCode);
        Assert.assertEquals(bookInfo.description, bookInfoDb.description);
        Assert.assertEquals(bookInfo.isbn10, bookInfoDb.isbn10);
        Assert.assertEquals(bookInfo.isbn13, bookInfoDb.isbn13);
        Assert.assertEquals(bookInfo.isRead, bookInfoDb.isRead);
        Assert.assertEquals(bookInfo.isFavourite, bookInfoDb.isFavourite);
        Assert.assertEquals(bookInfo.bookLocationId, bookInfoDb.bookLocationId);

        Assert.assertEquals(bookInfo.authors.size(), bookInfoDb.authors.size());
        Assert.assertEquals(1, bookInfoDb.authors.size());
        Author authorDb = bookInfo.authors.get(0);
        Assert.assertNotNull(authorDb.id);
        Assert.assertEquals(author.id, authorDb.id);

        Assert.assertEquals(bookInfo.categories.size(), bookInfoDb.categories.size());
        Assert.assertEquals(1, bookInfoDb.categories.size());
        Category categoryDb = bookInfoDb.categories.get(0);
        Assert.assertNotNull(categoryDb.id);
        Assert.assertEquals(category.id, categoryDb.id);
    }
}
