package com.blackbooks.test.services;

import com.blackbooks.database.TransactionManager;
import com.blackbooks.model.nonpersistent.BookInfo;
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

import junit.framework.Assert;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GetBookSmallThumbnailTest extends AbstractDatabaseTest {

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
     * Try to load the small thumbnail of a book without thumbnail. The result is expected to
     * be null.
     */
    public void testGetBookSmallThumbnail() {
        BookService bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);

        byte[] smallThumbnail = new byte[]{0, 1, 2, 3};

        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.THE_CATCHER_IN_THE_RYE;
        bookInfo.smallThumbnail = smallThumbnail;

        bookService.saveBookInfo(bookInfo);

        byte[] smallThumbnailDb = bookService.getBookSmallThumbnail(bookInfo.id);

        Assert.assertNotNull(smallThumbnailDb);
        Assert.assertTrue(smallThumbnailDb.length > 0);
        Assert.assertEquals(smallThumbnail.length, smallThumbnailDb.length);
    }

    /**
     * Try to load the small thumbnail of a book without thumbnail. The result is expected to
     * be null.
     */
    public void testGetBookSmallThumbnailNoThumbnail() {
        BookService bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);

        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.THE_VOYNICH_MANUSCRIPT;

        bookService.saveBookInfo(bookInfo);

        byte[] smallThumbnail = bookService.getBookSmallThumbnail(bookInfo.id);

        Assert.assertNull(smallThumbnail);
    }

    /**
     * Try to load the small thumbnail of a book with an empty thumbnail. The result is expected to
     * be null.
     */
    public void testGetBookSmallThumbnailEmptyThumbnail() {
        BookService bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);

        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.THE_VOYNICH_MANUSCRIPT;
        bookInfo.smallThumbnail = new byte[]{};

        bookService.saveBookInfo(bookInfo);

        byte[] smallThumbnail = bookService.getBookSmallThumbnail(bookInfo.id);

        Assert.assertNull(smallThumbnail);
    }
}
