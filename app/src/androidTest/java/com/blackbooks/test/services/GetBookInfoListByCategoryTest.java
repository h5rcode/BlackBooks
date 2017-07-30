package com.blackbooks.test.services;

import com.blackbooks.database.TransactionManager;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Category;
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
import com.blackbooks.test.data.Categories;

import junit.framework.Assert;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class GetBookInfoListByCategoryTest extends AbstractDatabaseTest {

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
     * Make sure the method returns the expected result.
     */
    public void testGetBookInfoListByCategory() {
        BookService bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);

        Category category = new Category();
        category.name = Categories.ADVENTURE;

        BookInfo bookInfo1 = new BookInfo();
        bookInfo1.title = Books.CASINO_ROYALE;
        bookInfo1.categories.add(category);
        bookService.saveBookInfo(bookInfo1);

        BookInfo bookInfo2 = new BookInfo();
        bookInfo2.title = Books.LA_MAGICIENNE_TRAHIE;
        bookInfo2.categories.add(category);
        bookService.saveBookInfo(bookInfo2);

        List<BookInfo> bookInfoList = bookService.getBookInfoListByCategory(category.id, Integer.MAX_VALUE, 0);

        Assert.assertEquals(2, bookInfoList.size());

        BookInfo bookInfoResult1 = bookInfoList.get(0);
        BookInfo bookInfoResult2 = bookInfoList.get(1);

        Assert.assertEquals(bookInfo1.id.longValue(), bookInfoResult1.id.longValue());
        Assert.assertEquals(bookInfo2.id.longValue(), bookInfoResult2.id.longValue());
    }
}
