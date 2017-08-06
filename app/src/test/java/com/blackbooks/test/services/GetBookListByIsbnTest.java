package com.blackbooks.test.services;

import com.blackbooks.database.TransactionManager;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Book;
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
import com.blackbooks.test.data.Books;
import com.blackbooks.test.data.Isbn10s;
import com.blackbooks.test.data.Isbn13s;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.security.InvalidParameterException;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class GetBookListByIsbnTest extends AbstractDatabaseTest {

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
     * Test of getBookListByIsbn with an ISBN-10.
     */
    public void testGetBookListByIsbnIsbn10() {
        BookService bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);

        BookInfo theGoldenBough = new BookInfo();
        theGoldenBough.title = Books.THE_GOLDEN_BOUGH;
        theGoldenBough.isbn10 = Isbn10s.THE_GOLDEN_BOUGH;

        bookService.saveBookInfo(theGoldenBough);

        List<Book> bookList = bookService.getBookListByIsbn(Isbn10s.THE_GOLDEN_BOUGH);

        assertEquals(1, bookList.size());

        Book book = bookList.get(0);

        assertEquals(theGoldenBough.id.longValue(), book.id.longValue());
        assertEquals(theGoldenBough.title, book.title);
    }

    /**
     * Test of getBookListByIsbn with an ISBN-13.
     */
    public void testGetBookListByIsbnIsbn13() {
        BookService bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);

        BookInfo beowulf = new BookInfo();
        beowulf.title = Books.BEOWULF;
        beowulf.isbn13 = Isbn13s.BEOWULF;

        bookService.saveBookInfo(beowulf);

        List<Book> bookList = bookService.getBookListByIsbn(Isbn13s.BEOWULF);

        assertEquals(1, bookList.size());

        Book book = bookList.get(0);

        assertEquals(beowulf.id.longValue(), book.id.longValue());
        assertEquals(beowulf.title, book.title);
    }

    /**
     * Test of getBookListByIsbn with an invalid ISBN.
     */
    public void testGetBookListByIsbnInvalidIsbn() {
        BookService bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);

        BookInfo beowulf = new BookInfo();
        beowulf.title = Books.BEOWULF;
        beowulf.isbn13 = Isbn13s.BEOWULF;

        bookService.saveBookInfo(beowulf);

        try {
            bookService.getBookListByIsbn("Invalid!");
            fail("InvalidParameterException expected.");
        } catch (InvalidParameterException e) {
            assertEquals("isbn", e.getMessage());
        }
    }

    /**
     * Test of getBookListByIsbn.
     * Check that it is case insensitive (ISBN-10s can contain the letter 'X').
     */
    public void testGetBookListByIsbnCaseInsensitive() {
        BookService bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);

        BookInfo lettresDeMonMoulin = new BookInfo();
        lettresDeMonMoulin.title = Books.LETTRES_DE_MON_MOULIN;
        lettresDeMonMoulin.isbn10 = Isbn10s.LETTRES_DE_MON_MOULIN.toLowerCase();

        bookService.saveBookInfo(lettresDeMonMoulin);

        List<Book> bookList = bookService.getBookListByIsbn(Isbn10s.LETTRES_DE_MON_MOULIN.toUpperCase());

        assertEquals(1, bookList.size());

        Book book = bookList.get(0);

        assertEquals(lettresDeMonMoulin.id.longValue(), book.id.longValue());
        assertEquals(lettresDeMonMoulin.title, book.title);
    }

    /**
     * Test of getBookListByIsbn with several books having the same ISBN.
     */
    public void testGetBookListByIsbnMultipleResults() {
        BookService bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);

        BookInfo lettresDeMonMoulin1 = new BookInfo();
        lettresDeMonMoulin1.title = Books.LETTRES_DE_MON_MOULIN;
        lettresDeMonMoulin1.isbn10 = Isbn10s.LETTRES_DE_MON_MOULIN;

        BookInfo lettresDeMonMoulin2 = new BookInfo(lettresDeMonMoulin1);

        bookService.saveBookInfo(lettresDeMonMoulin1);
        bookService.saveBookInfo(lettresDeMonMoulin2);

        List<Book> bookList = bookService.getBookListByIsbn(Isbn10s.LETTRES_DE_MON_MOULIN);

        assertEquals(2, bookList.size());

        Book book1 = bookList.get(0);
        assertEquals(lettresDeMonMoulin1.id.longValue(), book1.id.longValue());
        assertEquals(lettresDeMonMoulin1.title, book1.title);

        Book book2 = bookList.get(1);
        assertEquals(lettresDeMonMoulin2.id.longValue(), book2.id.longValue());
        assertEquals(lettresDeMonMoulin2.title, book2.title);
    }
}
