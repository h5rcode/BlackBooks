package com.blackbooks.test.services;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.BookAuthor;
import com.blackbooks.model.persistent.BookCategory;
import com.blackbooks.model.persistent.BookLocation;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.model.persistent.Publisher;
import com.blackbooks.model.persistent.Series;
import com.blackbooks.services.BookServiceImpl;
import com.blackbooks.test.data.Authors;
import com.blackbooks.test.data.BookLocations;
import com.blackbooks.test.data.Books;
import com.blackbooks.test.data.Categories;
import com.blackbooks.test.data.Languages;
import com.blackbooks.test.data.Publishers;
import com.blackbooks.test.data.Seriez;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetBookInfoTest extends AbstractBookServiceTest {

    private Book book;
    private Author author1;
    private Author author2;
    private Publisher publisher;
    private BookLocation bookLocation;
    private Category category1;
    private Category category2;
    private Series series;

    @Before
    public void setUp() {
        // Publisher.
        publisher = new Publisher();
        publisher.id = 2958L;
        publisher.name = Publishers.LE_LOMBARD;

        // Book location.
        bookLocation = new BookLocation();
        bookLocation.id = 818L;
        bookLocation.name = BookLocations.LIVING_ROOM;

        // Series
        series = new Series();
        series.id = 9529L;
        series.name = Seriez.ASTERIX;

        // Book.
        book = new Book();
        book.id = 6529L;
        book.title = Books.CASINO_ROYALE;
        book.subtitle = Books.CASINO_ROYALE;
        book.number = 1L;
        book.pageCount = 213L;
        book.languageCode = Languages.ENGLISH;
        book.bookLocationId = bookLocation.id;
        book.publisherId = publisher.id;
        book.publishedDate = new Date();
        book.seriesId = series.id;
        book.description = "James Bond, the finest gambler in the service, has a deadly new mission: to outplay Le Chiffre and shatter his Soviet cell";
        book.isbn10 = "0099575973";
        book.isbn13 = "9780099575979";
        book.isRead = 1L;
        book.isFavourite = 0L;

        // Authors.
        author1 = new Author();
        author1.id = 356L;
        author1.name = Authors.ALBERT_UDERZO;

        author2 = new Author();
        author2.id = 6437L;
        author2.name = Authors.RENE_GOSCINNY;

        BookAuthor bookAuthor1 = new BookAuthor();
        bookAuthor1.authorId = author1.id;

        BookAuthor bookAuthor2 = new BookAuthor();
        bookAuthor2.authorId = author2.id;

        List<BookAuthor> bookAuthors = new ArrayList<>();
        bookAuthors.add(bookAuthor1);
        bookAuthors.add(bookAuthor2);

        // Categories.
        category1 = new Category();
        category1.id = 647L;
        category1.name = Categories.FRENCH_COMICS;

        category2 = new Category();
        category2.id = 592594L;
        category2.name = Categories.HUMOR;

        BookCategory bookCategory1 = new BookCategory();
        bookCategory1.categoryId = category1.id;

        BookCategory bookCategory2 = new BookCategory();
        bookCategory2.categoryId = category2.id;

        List<BookCategory> bookCategories = new ArrayList<>();
        bookCategories.add(bookCategory1);
        bookCategories.add(bookCategory2);

        // Mocks.
        when(publisherRepository.getPublisher(publisher.id)).thenReturn(publisher);

        when(bookLocationRepository.getBookLocation(bookLocation.id)).thenReturn(bookLocation);

        when(bookRepository.getBook(book.id)).thenReturn(book);

        when(seriesRepository.getSeries(series.id)).thenReturn(series);

        when(bookAuthorRepository.getBookAuthorListByBook(book.id)).thenReturn(bookAuthors);
        when(authorRepository.getAuthor(author1.id)).thenReturn(author1);
        when(authorRepository.getAuthor(author2.id)).thenReturn(author2);

        when(bookCategoryRepository.getBookCategoryListByBook(book.id)).thenReturn(bookCategories);
        when(categoryRepository.getCategory(category1.id)).thenReturn(category1);
        when(categoryRepository.getCategory(category2.id)).thenReturn(category2);
    }

    @Test
    public void getBookInfo_should_return_the_correct_book_info() {
        long bookId = 6529L;

        when(bookRepository.getBook(bookId)).thenReturn(book);

        BookInfo bookInfo = bookService.getBookInfo(bookId);

        assertEquals(book.id, bookInfo.id);
        assertEquals(book.title, bookInfo.title);
        assertEquals(book.subtitle, bookInfo.subtitle);
        assertEquals(book.number, bookInfo.number);
        assertEquals(book.pageCount, bookInfo.pageCount);
        assertEquals(book.languageCode, bookInfo.languageCode);
        assertEquals(book.publishedDate, bookInfo.publishedDate);
        assertEquals(book.description, bookInfo.description);
        assertEquals(book.isbn10, bookInfo.isbn10);
        assertEquals(book.isbn13, bookInfo.isbn13);
        assertEquals(book.isRead, bookInfo.isRead);
        assertEquals(book.isFavourite, bookInfo.isFavourite);
    }

    @Test
    public void getBookInfo_should_return_the_correct_authors_info() {
        BookInfo bookInfo = bookService.getBookInfo(book.id);

        assertEquals(2, bookInfo.authors.size());

        assertEquals(author1, bookInfo.authors.get(0));
        assertEquals(author2, bookInfo.authors.get(1));
    }

    @Test
    public void getBookInfo_should_return_the_correct_publisher_info() {
        BookInfo bookInfo = bookService.getBookInfo(book.id);

        assertEquals(publisher.id, bookInfo.publisherId);
        assertEquals(publisher.id, bookInfo.publisher.id);
        assertEquals(publisher.name, bookInfo.publisher.name);
    }

    @Test
    public void getBookInfo_should_return_the_correct_categories_info() {
        BookInfo bookInfo = bookService.getBookInfo(book.id);

        assertEquals(2, bookInfo.categories.size());

        assertEquals(category1, bookInfo.categories.get(0));
        assertEquals(category2, bookInfo.categories.get(1));
    }

    @Test
    public void getBookInfo_should_return_the_correct_book_location_info() {
        BookInfo bookInfo = bookService.getBookInfo(book.id);

        assertEquals(bookLocation.id, bookInfo.bookLocationId);
        assertEquals(bookLocation.id, bookInfo.bookLocation.id);
        assertEquals(bookLocation.name, bookInfo.bookLocation.name);
    }

    @Test
    public void getBookInfo_should_return_the_correct_series_info() {
        BookInfo bookInfo = bookService.getBookInfo(book.id);

        assertEquals(series.id, bookInfo.seriesId);
        assertEquals(series.id, bookInfo.series.id);
        assertEquals(series.name, bookInfo.series.name);
    }
}
