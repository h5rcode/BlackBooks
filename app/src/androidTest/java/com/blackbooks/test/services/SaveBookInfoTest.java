package com.blackbooks.test.services;

import android.database.sqlite.SQLiteConstraintException;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.Publisher;
import com.blackbooks.services.BookServices;
import com.blackbooks.services.FullTextSearchServices;
import com.blackbooks.test.data.Authors;
import com.blackbooks.test.data.Books;
import com.blackbooks.test.data.Publishers;

import junit.framework.Assert;

import java.security.InvalidParameterException;
import java.util.List;

/**
 * Test class of service {@link BookServices#saveBookInfo(android.database.sqlite.SQLiteDatabase, com.blackbooks.model.nonpersistent.BookInfo)} ()}.
 */
public class SaveBookInfoTest extends AbstractDatabaseTest {

    /**
     * Test the saving of an empty book. A {@link SQLiteConstraintException} is
     * expected.
     */
    public void testSaveBookInfoEmpty() {
        BookInfo bookInfo = new BookInfo();
        try {
            BookServices.saveBookInfo(getDb(), bookInfo);
            Assert.fail();
        } catch (SQLiteConstraintException e) {
            assertConstraintFailed(e);
        }
    }

    /**
     * Save a book with an invalid ISBN-10.
     */
    public void testSaveBookInfoInvalidIsbn10() {
        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.NOSTROMO;
        bookInfo.isbn10 = "!";

        try {
            BookServices.saveBookInfo(getDb(), bookInfo);
            Assert.fail();
        } catch (InvalidParameterException e) {
            Assert.assertEquals("Invalid ISBN-10.", e.getMessage());
        }
    }

    /**
     * Save a book with an invalid ISBN-13.
     */
    public void testSaveBookInfoInvalidIsbn13() {
        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.HEART_OF_DARKNESS;
        bookInfo.isbn13 = "!";

        try {
            BookServices.saveBookInfo(getDb(), bookInfo);
            Assert.fail();
        } catch (InvalidParameterException e) {
            Assert.assertEquals("Invalid ISBN-13.", e.getMessage());
        }
    }

    /**
     * Verify that a saved book is actually saved in the corresponding
     * Full-Text-Search table.
     */
    public void testSaveBookInfoFullTextSearch() {
        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.LE_MYTHE_DE_SISYPHE;

        BookServices.saveBookInfo(getDb(), bookInfo);

        List<BookInfo> bookInfoList = FullTextSearchServices.searchBooks(getDb(), Books.LE_MYTHE_DE_SISYPHE);

        Assert.assertEquals(1, bookInfoList.size());
        BookInfo bookInfoDb = bookInfoList.get(0);

        Assert.assertEquals(bookInfo.id, bookInfoDb.id);
    }

    /**
     * Save a book with only the title specified.
     */
    public void testSaveBookInfoTitle() {
        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.LE_MYTHE_DE_SISYPHE;

        BookServices.saveBookInfo(getDb(), bookInfo);
        Book book = BookServices.getBook(getDb(), bookInfo.id);
        Assert.assertEquals(Books.LE_MYTHE_DE_SISYPHE, book.title);
    }

    /**
     * Save a book with an author that already exists in the DB.
     */
    public void testSaveBookInfoWithExistingAuthor() {
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

        BookInfo bookInfo1Db = BookServices.getBookInfo(getDb(), bookInfo1.id);
        BookInfo bookInfo2Db = BookServices.getBookInfo(getDb(), bookInfo2.id);

        Assert.assertEquals(1, bookInfo1Db.authors.size());
        Assert.assertEquals(1, bookInfo2Db.authors.size());

        Author authorBook1Db = bookInfo1Db.authors.get(0);
        Author authorBook2Db = bookInfo2Db.authors.get(0);

        Assert.assertEquals(author.id, authorBook1Db.id);
        Assert.assertEquals(author.id, authorBook2Db.id);
        Assert.assertEquals(Authors.ALBERT_CAMUS, authorBook1Db.name);
        Assert.assertEquals(Authors.ALBERT_CAMUS, authorBook2Db.name);
    }

    /**
     * Save a book with a publisher that already exists in the DB.
     */
    public void testSaveBookInfoWithExistingPublisher() {

        BookInfo bookInfo1 = new BookInfo();
        bookInfo1.title = Books.LE_MYTHE_DE_SISYPHE;
        bookInfo1.publisher.name = Publishers.GALLIMARD;
        BookServices.saveBookInfo(getDb(), bookInfo1);

        BookInfo bookInfo2 = new BookInfo();
        bookInfo2.title = Books.LA_PESTE;
        bookInfo2.publisher.name = Publishers.GALLIMARD;
        BookServices.saveBookInfo(getDb(), bookInfo2);

        BookInfo bookInfo1Db = BookServices.getBookInfo(getDb(), bookInfo1.id);
        BookInfo bookInfo2Db = BookServices.getBookInfo(getDb(), bookInfo2.id);

        Publisher publisher1Db = bookInfo1Db.publisher;
        Publisher publisher2Db = bookInfo2Db.publisher;

        Assert.assertEquals(bookInfo1.publisher.id, publisher1Db.id);
        Assert.assertEquals(bookInfo2.publisher.id, publisher2Db.id);

        Assert.assertEquals(Publishers.GALLIMARD, publisher1Db.name);
        Assert.assertEquals(Publishers.GALLIMARD, publisher2Db.name);
    }

    /**
     * Save a book with an author that does not already exist in the DB.
     */
    public void testSaveBookInfoWithNewAuthor() {
        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.LE_MYTHE_DE_SISYPHE;

        Author author = new Author();
        author.name = Authors.ALBERT_CAMUS;
        bookInfo.authors.add(author);

        BookServices.saveBookInfo(getDb(), bookInfo);

        BookInfo bookInfoDb = BookServices.getBookInfo(getDb(), bookInfo.id);

        Assert.assertEquals(1, bookInfoDb.authors.size());
        Author authorDb = bookInfoDb.authors.get(0);

        Assert.assertEquals(author.id, authorDb.id);
        Assert.assertEquals(Authors.ALBERT_CAMUS, authorDb.name);
    }

    /**
     * Save a book with a publisher that does not already exist in the DB.
     */
    public void testSaveBookInfoWithNewPublisher() {
        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.LE_MYTHE_DE_SISYPHE;

        Publisher publisher = bookInfo.publisher;
        publisher.name = Publishers.GALLIMARD;

        BookServices.saveBookInfo(getDb(), bookInfo);

        BookInfo bookInfoDb = BookServices.getBookInfo(getDb(), bookInfo.id);

        Assert.assertNotNull(bookInfoDb.publisherId);
        Publisher publisherDb = bookInfoDb.publisher;

        Assert.assertEquals(publisher.id, publisherDb.id);
        Assert.assertEquals(Publishers.GALLIMARD, publisherDb.name);
    }
}
