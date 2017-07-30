package com.blackbooks.test.services;

import com.blackbooks.database.TransactionManager;
import com.blackbooks.model.nonpersistent.BookExport;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Author;
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
import com.blackbooks.services.ExportService;
import com.blackbooks.services.ExportServiceImpl;
import com.blackbooks.test.data.Authors;
import com.blackbooks.test.data.BookLocations;
import com.blackbooks.test.data.Books;
import com.blackbooks.test.data.Categories;
import com.blackbooks.test.data.Languages;
import com.blackbooks.test.data.Publishers;
import com.blackbooks.test.data.Seriez;
import com.blackbooks.utils.DateUtils;

import junit.framework.Assert;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class GetBookExportListTest extends AbstractDatabaseTest {

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
     * Test of getBookExportList.
     */
    public void testGetBookExportList() {
        BookService bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);

        Author author1 = new Author();
        author1.name = Authors.GRZEGORZ_ROSINSKI;

        Author author2 = new Author();
        author2.name = Authors.JEAN_VAN_HAMME;

        Category category1 = new Category();
        category1.name = Categories.BELGIAN_COMICS;

        Category category2 = new Category();
        category2.name = Categories.ADVENTURE;

        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.LA_MAGICIENNE_TRAHIE;
        bookInfo.subtitle = Books.LA_MAGICIENNE_TRAHIE;
        bookInfo.authors.add(author1);
        bookInfo.authors.add(author2);
        bookInfo.categories.add(category1);
        bookInfo.categories.add(category2);
        bookInfo.series.name = Seriez.THORGAL;
        bookInfo.number = 1L;
        bookInfo.publisher.name = Publishers.LE_LOMBARD;
        bookInfo.publishedDate = new Date();
        bookInfo.pageCount = 48L;
        bookInfo.languageCode = Languages.FRENCH;
        bookInfo.description = "Thorgal Aegirsson est condamne a mort par Gandalf-le-fou, roi des Vikings.\n";
        bookInfo.isbn10 = "2803603586";
        bookInfo.isbn13 = "9782803603589";
        bookInfo.isRead = 1L;
        bookInfo.isFavourite = 0L;
        bookInfo.bookLocation.name = BookLocations.LIVING_ROOM;

        bookService.saveBookInfo(bookInfo);

        ExportService exportService = new ExportServiceImpl(getDb());
        List<BookExport> bookExportList = exportService.getBookExportList(null);

        Assert.assertEquals(1, bookExportList.size());
        BookExport bookExport = bookExportList.get(0);

        Assert.assertNotNull(bookExport.id);
        Assert.assertNotNull(bookExport.title);
        Assert.assertNotNull(bookExport.subtitle);
        Assert.assertNotNull(bookExport.authors);
        Assert.assertNotNull(bookExport.categories);
        Assert.assertNotNull(bookExport.series);
        Assert.assertNotNull(bookExport.number);
        Assert.assertNotNull(bookExport.pageCount);
        Assert.assertNotNull(bookExport.languageCode);
        Assert.assertNotNull(bookExport.description);
        Assert.assertNotNull(bookExport.isbn10);
        Assert.assertNotNull(bookExport.isbn13);

        Assert.assertEquals(bookInfo.id.longValue(), bookExport.id.longValue());
        Assert.assertEquals(bookInfo.title, bookExport.title);
        Assert.assertEquals(bookInfo.subtitle, bookExport.subtitle);
        Assert.assertEquals(author1.name + "," + author2.name, bookExport.authors);
        Assert.assertEquals(category1.name + "," + category2.name, bookExport.categories);
        Assert.assertEquals(bookInfo.series.name, bookExport.series);
        Assert.assertEquals(bookInfo.number.longValue(), bookExport.number.longValue());
        Assert.assertEquals(bookInfo.pageCount.longValue(), bookExport.pageCount.longValue());
        Assert.assertEquals(bookInfo.languageCode, bookExport.languageCode);
        Assert.assertTrue(bookInfo.description.contains("\n"));
        Assert.assertEquals(bookInfo.description.replace("\n", ""), bookExport.description);
        Assert.assertEquals(bookInfo.publisher.name, bookExport.publisher);
        Assert.assertEquals(DateUtils.DEFAULT_DATE_FORMAT.format(bookInfo.publishedDate), bookExport.publishedDate);
        Assert.assertEquals(bookInfo.isbn10, bookExport.isbn10);
        Assert.assertEquals(bookInfo.isbn13, bookExport.isbn13);

    }
}
