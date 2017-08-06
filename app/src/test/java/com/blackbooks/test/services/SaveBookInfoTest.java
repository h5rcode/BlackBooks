package com.blackbooks.test.services;

import com.blackbooks.database.TransactionManager;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Publisher;
import com.blackbooks.model.persistent.fts.BookFTS;
import com.blackbooks.repositories.AuthorRepository;
import com.blackbooks.repositories.BookAuthorRepository;
import com.blackbooks.repositories.BookCategoryRepository;
import com.blackbooks.repositories.BookFTSRepository;
import com.blackbooks.repositories.BookLocationRepository;
import com.blackbooks.repositories.BookRepository;
import com.blackbooks.repositories.CategoryRepository;
import com.blackbooks.repositories.PublisherRepository;
import com.blackbooks.repositories.SeriesRepository;
import com.blackbooks.services.BookServiceImpl;
import com.blackbooks.test.data.Authors;
import com.blackbooks.test.data.Books;
import com.blackbooks.test.data.Publishers;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.security.InvalidParameterException;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SaveBookInfoTest {

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

    @Test
    public void saveBookInfo_should_save_book() {
        BookServiceImpl bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);

        BookInfo bookInfo = new BookInfo();
        bookService.saveBookInfo(bookInfo);

        verify(bookRepository).save(bookInfo);
    }

    @Test
    public void saveBookInfo_should_begin_transaction() {
        BookServiceImpl bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);
        bookService.saveBookInfo(new BookInfo());

        verify(transactionManager).beginTransaction();
    }

    @Test
    public void saveBookInfo_should_set_transaction_successful() {
        BookServiceImpl bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);
        bookService.saveBookInfo(new BookInfo());

        verify(transactionManager).setTransactionSuccessful();
    }

    @Test
    public void saveBookInfo_should_end_transaction() {
        BookServiceImpl bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);
        bookService.saveBookInfo(new BookInfo());

        verify(transactionManager).endTransaction();
    }

    @Test
    public void saveBookInfo_should_end_transaction_when_an_error_occurs() {
        BookServiceImpl bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);
        BookInfo bookInfo = new BookInfo();

        doThrow(new RuntimeException()).when(bookRepository).save(bookInfo);

        try {
            bookService.saveBookInfo(bookInfo);
            Assert.fail();
        } catch (RuntimeException e) {
            verify(transactionManager).endTransaction();
        }
    }

    @Test
    public void saveBookInfo_should_throw_an_InvalidParameterException_when_the_isbn10_is_invalid() {
        BookServiceImpl bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);

        BookInfo bookInfo = new BookInfo();
        bookInfo.isbn10 = "!";

        try {
            bookService.saveBookInfo(bookInfo);
            Assert.fail();
        } catch (InvalidParameterException e) {
            assertEquals("Invalid ISBN-10.", e.getMessage());
        }
    }

    @Test
    public void saveBookInfo_should_throw_an_InvalidParameterException_when_the_isbn13_is_invalid() {
        BookServiceImpl bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);

        BookInfo bookInfo = new BookInfo();
        bookInfo.isbn13 = "!";

        try {
            bookService.saveBookInfo(bookInfo);
            Assert.fail();
        } catch (InvalidParameterException e) {
            assertEquals("Invalid ISBN-13.", e.getMessage());
        }
    }

    @Test
    public void saveBookInfo_should_insert_the_book_in_the_full_text_search_table() {
        BookServiceImpl bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);

        final long bookId = 536L;

        BookInfo bookInfo = new BookInfo();
        when(bookRepository.save(bookInfo)).thenReturn(bookId);

        bookService.saveBookInfo(bookInfo);

        ArgumentCaptor<BookFTS> argument = ArgumentCaptor.forClass(BookFTS.class);
        verify(bookFTSRepository).insert(argument.capture());

        BookFTS bookFTS = argument.getValue();

        assertEquals(bookId, bookFTS.docid.longValue());
    }

    /**
     * Save a book with an author that already exists in the DB.
     */
    public void testSaveBookInfoWithExistingAuthor() {
        BookServiceImpl bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);

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

        assertEquals(1, bookInfo1Db.authors.size());
        assertEquals(1, bookInfo2Db.authors.size());

        Author authorBook1Db = bookInfo1Db.authors.get(0);
        Author authorBook2Db = bookInfo2Db.authors.get(0);

        assertEquals(author.id, authorBook1Db.id);
        assertEquals(author.id, authorBook2Db.id);
        assertEquals(Authors.ALBERT_CAMUS, authorBook1Db.name);
        assertEquals(Authors.ALBERT_CAMUS, authorBook2Db.name);
    }

    /**
     * Save a book with a publisher that already exists in the DB.
     */
    public void testSaveBookInfoWithExistingPublisher() {
        BookServiceImpl bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);

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

        assertEquals(bookInfo1.publisherId, publisher1Db.id);
        assertEquals(bookInfo2.publisherId, publisher2Db.id);

        assertEquals(Publishers.GALLIMARD, publisher1Db.name);
        assertEquals(Publishers.GALLIMARD, publisher2Db.name);
    }

    /**
     * Save a book with an author that does not already exist in the DB.
     */
    public void testSaveBookInfoWithNewAuthor() {
        BookServiceImpl bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);

        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.LE_MYTHE_DE_SISYPHE;

        Author author = new Author();
        author.name = Authors.ALBERT_CAMUS;
        bookInfo.authors.add(author);

        bookService.saveBookInfo(bookInfo);

        BookInfo bookInfoDb = bookService.getBookInfo(bookInfo.id);

        assertEquals(1, bookInfoDb.authors.size());
        Author authorDb = bookInfoDb.authors.get(0);

        assertEquals(author.id, authorDb.id);
        assertEquals(Authors.ALBERT_CAMUS, authorDb.name);
    }

    /**
     * Save a book with a publisher that does not already exist in the DB.
     */
    public void testSaveBookInfoWithNewPublisher() {
        BookServiceImpl bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);

        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.LE_MYTHE_DE_SISYPHE;

        Publisher publisher = bookInfo.publisher;
        publisher.name = Publishers.GALLIMARD;

        bookService.saveBookInfo(bookInfo);

        BookInfo bookInfoDb = bookService.getBookInfo(bookInfo.id);

        Assert.assertNotNull(bookInfoDb.publisherId);
        Publisher publisherDb = bookInfoDb.publisher;

        assertEquals(publisher.id, publisherDb.id);
        assertEquals(Publishers.GALLIMARD, publisherDb.name);
    }
}
