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
import com.blackbooks.test.data.People;

import junit.framework.Assert;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class GetBookCountByLoanedToTest extends AbstractDatabaseTest {

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
     * Make sure the method returns the expected value.
     */
    public void testGetBookCountByLoanedTo() {
        BookService bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);

        BookInfo bookInfo1 = new BookInfo();
        bookInfo1.title = Books.HEART_OF_DARKNESS;
        bookInfo1.loanedTo = People.JOHN_DOE;
        bookInfo1.loanDate = new Date();
        bookService.saveBookInfo(bookInfo1);

        BookInfo bookInfo2 = new BookInfo();
        bookInfo2.title = Books.HARRY_POTTER_AND_THE_CHAMBER_OF_SECRETS;
        bookInfo2.loanedTo = People.JOHN_DOE;
        bookInfo2.loanDate = new Date();
        bookService.saveBookInfo(bookInfo2);

        int bookCount = bookService.getBookCountByLoanedTo(People.JOHN_DOE);

        Assert.assertEquals(2, bookCount);
    }
}