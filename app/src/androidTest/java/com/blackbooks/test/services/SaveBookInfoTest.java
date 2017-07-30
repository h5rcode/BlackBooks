package com.blackbooks.test.services;

import android.database.sqlite.SQLiteConstraintException;

import com.blackbooks.database.TransactionManager;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.Publisher;
import com.blackbooks.repositories.AuthorRepository;
import com.blackbooks.repositories.BookAuthorRepository;
import com.blackbooks.repositories.BookCategoryRepository;
import com.blackbooks.repositories.BookFTSRepository;
import com.blackbooks.repositories.BookLocationRepository;
import com.blackbooks.repositories.BookRepository;
import com.blackbooks.repositories.CategoryRepository;
import com.blackbooks.repositories.PublisherRepository;
import com.blackbooks.repositories.SeriesRepository;
import com.blackbooks.services.BookService;
import com.blackbooks.services.BookServiceImpl;
import com.blackbooks.test.data.Authors;
import com.blackbooks.test.data.Books;
import com.blackbooks.test.data.Publishers;

import junit.framework.Assert;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.security.InvalidParameterException;

@RunWith(MockitoJUnitRunner.class)
public class SaveBookInfoTest extends AbstractDatabaseTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookAuthorRepository bookAuthorRepository;

    @Mock
    private BookCategoryRepository bookCategoryRepository;

    @Mock
    private BookFTSRepository bookFTSRepository;

    @Mock
    private BookLocationRepository bookLocationRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private PublisherRepository publisherRepository;

    @Mock
    private SeriesRepository seriesRepository;

    @Mock
    private TransactionManager transactionManager;

    /**
     * Test the saving of an empty book. A {@link SQLiteConstraintException} is
     * expected.
     */
    public void testSaveBookInfoEmpty() {
        BookService bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);

        BookInfo bookInfo = new BookInfo();
        try {
            bookService.saveBookInfo(bookInfo);
            Assert.fail();
        } catch (SQLiteConstraintException e) {
            assertConstraintFailed(e);
        }
    }

    /**
     * Save a book with an invalid ISBN-10.
     */
    public void testSaveBookInfoInvalidIsbn10() {
        BookService bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);

        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.NOSTROMO;
        bookInfo.isbn10 = "!";

        try {
            bookService.saveBookInfo(bookInfo);
            Assert.fail();
        } catch (InvalidParameterException e) {
            Assert.assertEquals("Invalid ISBN-10.", e.getMessage());
        }
    }

    /**
     * Save a book with an invalid ISBN-13.
     */
    public void testSaveBookInfoInvalidIsbn13() {
        BookService bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);

        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.HEART_OF_DARKNESS;
        bookInfo.isbn13 = "!";

        try {
            bookService.saveBookInfo(bookInfo);
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
        BookService bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);

        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.LE_MYTHE_DE_SISYPHE;

        bookService.saveBookInfo(bookInfo);

        // TODO
    }

    /**
     * Save a book with only the title specified.
     */
    public void testSaveBookInfoTitle() {
        BookService bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);

        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.LE_MYTHE_DE_SISYPHE;

        bookService.saveBookInfo(bookInfo);
        Book book = bookService.getBook(bookInfo.id);
        Assert.assertEquals(Books.LE_MYTHE_DE_SISYPHE, book.title);
    }

    /**
     * Save a book with an author that already exists in the DB.
     */
    public void testSaveBookInfoWithExistingAuthor() {
        BookService bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);

        Author author = new Author();
        author.name = Authors.ALBERT_CAMUS;

        BookInfo bookInfo1 = new BookInfo();
        bookInfo1.title = Books.LE_MYTHE_DE_SISYPHE;
        bookInfo1.authors.add(author);
        bookService.saveBookInfo(bookInfo1);

        BookInfo bookInfo2 = new BookInfo();
        bookInfo2.title = Books.LA_PESTE;
        bookInfo2.authors.add(author);
        bookService.saveBookInfo(bookInfo2);

        BookInfo bookInfo1Db = bookService.getBookInfo(bookInfo1.id);
        BookInfo bookInfo2Db = bookService.getBookInfo(bookInfo2.id);

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
        BookService bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);

        BookInfo bookInfo1 = new BookInfo();
        bookInfo1.title = Books.LE_MYTHE_DE_SISYPHE;
        bookInfo1.publisher.name = Publishers.GALLIMARD;
        bookService.saveBookInfo(bookInfo1);

        BookInfo bookInfo2 = new BookInfo();
        bookInfo2.title = Books.LA_PESTE;
        bookInfo2.publisher.name = Publishers.GALLIMARD;
        bookService.saveBookInfo(bookInfo2);

        BookInfo bookInfo1Db = bookService.getBookInfo(bookInfo1.id);
        BookInfo bookInfo2Db = bookService.getBookInfo(bookInfo2.id);

        Publisher publisher1Db = bookInfo1Db.publisher;
        Publisher publisher2Db = bookInfo2Db.publisher;

        Assert.assertEquals(bookInfo1.publisherId, publisher1Db.id);
        Assert.assertEquals(bookInfo2.publisherId, publisher2Db.id);

        Assert.assertEquals(Publishers.GALLIMARD, publisher1Db.name);
        Assert.assertEquals(Publishers.GALLIMARD, publisher2Db.name);
    }

    /**
     * Save a book with an author that does not already exist in the DB.
     */
    public void testSaveBookInfoWithNewAuthor() {
        BookService bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);

        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.LE_MYTHE_DE_SISYPHE;

        Author author = new Author();
        author.name = Authors.ALBERT_CAMUS;
        bookInfo.authors.add(author);

        bookService.saveBookInfo(bookInfo);

        BookInfo bookInfoDb = bookService.getBookInfo(bookInfo.id);

        Assert.assertEquals(1, bookInfoDb.authors.size());
        Author authorDb = bookInfoDb.authors.get(0);

        Assert.assertEquals(author.id, authorDb.id);
        Assert.assertEquals(Authors.ALBERT_CAMUS, authorDb.name);
    }

    /**
     * Save a book with a publisher that does not already exist in the DB.
     */
    public void testSaveBookInfoWithNewPublisher() {
        BookService bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);

        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.LE_MYTHE_DE_SISYPHE;

        Publisher publisher = bookInfo.publisher;
        publisher.name = Publishers.GALLIMARD;

        bookService.saveBookInfo(bookInfo);

        BookInfo bookInfoDb = bookService.getBookInfo(bookInfo.id);

        Assert.assertNotNull(bookInfoDb.publisherId);
        Publisher publisherDb = bookInfoDb.publisher;

        Assert.assertEquals(publisher.id, publisherDb.id);
        Assert.assertEquals(Publishers.GALLIMARD, publisherDb.name);
    }
}
