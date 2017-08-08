package com.blackbooks.test.services;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.BuildConfig;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookExport;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.BookAuthor;
import com.blackbooks.model.persistent.BookCategory;
import com.blackbooks.model.persistent.BookLocation;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.model.persistent.Publisher;
import com.blackbooks.model.persistent.Series;
import com.blackbooks.services.ExportService;
import com.blackbooks.services.ExportServiceImpl;
import com.blackbooks.sql.Broker;
import com.blackbooks.sql.BrokerManager;
import com.blackbooks.test.data.Authors;
import com.blackbooks.test.data.BookLocations;
import com.blackbooks.test.data.Books;
import com.blackbooks.test.data.Categories;
import com.blackbooks.test.data.Languages;
import com.blackbooks.test.data.Publishers;
import com.blackbooks.test.data.Seriez;
import com.blackbooks.utils.DateUtils;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Date;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class GetBookExportListTest {

    @Mock
    private Context context;

    private SQLiteHelper sqLiteHelper;

    private SQLiteDatabase db;

    @Before
    public void abstractBookRepositoryTestSetup() throws Exception {
        SQLiteHelper.initialize(RuntimeEnvironment.application);
        sqLiteHelper = SQLiteHelper.getInstance();
        db = sqLiteHelper.getWritableDatabase();
    }

    @After
    public void abstractBookRepositoryTestTearDown() {
        sqLiteHelper.close();
    }

    @Test
    public void getBookExportList_should_return_the_expected_results() {
        Author author1 = new Author();
        author1.name = Authors.GRZEGORZ_ROSINSKI;

        Author author2 = new Author();
        author2.name = Authors.JEAN_VAN_HAMME;

        Category category1 = new Category();
        category1.name = Categories.BELGIAN_COMICS;

        Category category2 = new Category();
        category2.name = Categories.ADVENTURE;

        Publisher publisher = new Publisher();
        publisher.name = Publishers.LE_LOMBARD;

        BookLocation bookLocation = new BookLocation();
        bookLocation.name = BookLocations.LIVING_ROOM;

        Series series = new Series();
        series.name = Seriez.THORGAL;

        Broker<Book> bookBroker = BrokerManager.getBroker(Book.class);
        Broker<Category> categoryBroker = BrokerManager.getBroker(Category.class);
        Broker<BookCategory> bookCategoryBroker = BrokerManager.getBroker(BookCategory.class);
        Broker<Author> authorBroker = BrokerManager.getBroker(Author.class);
        Broker<BookAuthor> bookAuthorBroker = BrokerManager.getBroker(BookAuthor.class);
        Broker<BookLocation> bookLocationBroker = BrokerManager.getBroker(BookLocation.class);
        Broker<Publisher> publisherBroker = BrokerManager.getBroker(Publisher.class);
        Broker<Series> seriesBroker = BrokerManager.getBroker(Series.class);

        categoryBroker.save(db, category1);
        categoryBroker.save(db, category2);

        authorBroker.save(db, author1);
        authorBroker.save(db, author2);

        bookLocationBroker.save(db, bookLocation);

        publisherBroker.save(db, publisher);

        seriesBroker.save(db, series);

        Book book = new Book();
        book.title = Books.LA_MAGICIENNE_TRAHIE;
        book.bookLocationId = bookLocation.id;
        book.publisherId = publisher.id;
        book.seriesId = series.id;
        book.subtitle = Books.LA_MAGICIENNE_TRAHIE;
        book.number = 1L;
        book.publishedDate = new Date();
        book.pageCount = 48L;
        book.languageCode = Languages.FRENCH;
        book.description = "Thorgal Aegirsson est condamne a mort par Gandalf-le-fou, roi des Vikings.\n";
        book.isbn10 = "2803603586";
        book.isbn13 = "9782803603589";
        book.isRead = 1L;
        book.isFavourite = 0L;

        bookBroker.save(db, book);

        BookAuthor bookAuthor1 = new BookAuthor();
        BookAuthor bookAuthor2 = new BookAuthor();

        bookAuthor1.authorId = author1.id;
        bookAuthor1.bookId = book.id;

        bookAuthor2.authorId = author2.id;
        bookAuthor2.bookId = book.id;

        bookAuthorBroker.save(db, bookAuthor1);
        bookAuthorBroker.save(db, bookAuthor2);

        BookCategory bookCategory1 = new BookCategory();
        BookCategory bookCategory2 = new BookCategory();

        bookCategory1.categoryId = category1.id;
        bookCategory1.bookId = book.id;

        bookCategory2.categoryId = category2.id;
        bookCategory2.bookId = book.id;

        bookCategoryBroker.save(db, bookCategory1);
        bookCategoryBroker.save(db, bookCategory2);

        ExportService exportService = new ExportServiceImpl(db);
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

        Assert.assertEquals(book.id.longValue(), bookExport.id.longValue());
        Assert.assertEquals(book.title, bookExport.title);
        Assert.assertEquals(book.subtitle, bookExport.subtitle);
        Assert.assertEquals(author1.name + "," + author2.name, bookExport.authors);
        Assert.assertEquals(category1.name + "," + category2.name, bookExport.categories);
        Assert.assertEquals(series.name, bookExport.series);
        Assert.assertEquals(book.number.longValue(), bookExport.number.longValue());
        Assert.assertEquals(book.pageCount.longValue(), bookExport.pageCount.longValue());
        Assert.assertEquals(book.languageCode, bookExport.languageCode);
        Assert.assertTrue(book.description.contains("\n"));
        Assert.assertEquals(book.description.replace("\n", ""), bookExport.description);
        Assert.assertEquals(publisher.name, bookExport.publisher);
        Assert.assertEquals(DateUtils.DEFAULT_DATE_FORMAT.format(book.publishedDate), bookExport.publishedDate);
        Assert.assertEquals(book.isbn10, bookExport.isbn10);
        Assert.assertEquals(book.isbn13, bookExport.isbn13);

    }
}
