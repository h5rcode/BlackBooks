package com.blackbooks.test.services;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.services.BookServices;
import com.blackbooks.test.data.Books;
import com.blackbooks.test.data.Isbn10s;
import com.blackbooks.test.data.Isbn13s;

import java.security.InvalidParameterException;
import java.util.List;

/**
 * Test of {@link com.blackbooks.services.BookServices#getBookListByIsbn(android.database.sqlite.SQLiteDatabase, String)}.
 */
public class GetBookListByIsbnTest extends AbstractDatabaseTest {

    /**
     * Test of getBookListByIsbn with an ISBN-10.
     */
    public void testGetBookListByIsbnIsbn10() {

        BookInfo theGoldenBough = new BookInfo();
        theGoldenBough.title = Books.THE_GOLDEN_BOUGH;
        theGoldenBough.isbn10 = Isbn10s.THE_GOLDEN_BOUGH;

        BookServices.saveBookInfo(getDb(), theGoldenBough);

        List<Book> bookList = BookServices.getBookListByIsbn(getDb(), Isbn10s.THE_GOLDEN_BOUGH);

        assertEquals(1, bookList.size());

        Book book = bookList.get(0);

        assertEquals(theGoldenBough.id.longValue(), book.id.longValue());
        assertEquals(theGoldenBough.title, book.title);
    }

    /**
     * Test of getBookListByIsbn with an ISBN-13.
     */
    public void testGetBookListByIsbnIsbn13() {

        BookInfo beowulf = new BookInfo();
        beowulf.title = Books.BEOWULF;
        beowulf.isbn13 = Isbn13s.BEOWULF;

        BookServices.saveBookInfo(getDb(), beowulf);

        List<Book> bookList = BookServices.getBookListByIsbn(getDb(), Isbn13s.BEOWULF);

        assertEquals(1, bookList.size());

        Book book = bookList.get(0);

        assertEquals(beowulf.id.longValue(), book.id.longValue());
        assertEquals(beowulf.title, book.title);
    }

    /**
     * Test of getBookListByIsbn with an invalid ISBN.
     */
    public void testGetBookListByIsbnInvalidIsbn() {

        BookInfo beowulf = new BookInfo();
        beowulf.title = Books.BEOWULF;
        beowulf.isbn13 = Isbn13s.BEOWULF;

        BookServices.saveBookInfo(getDb(), beowulf);

        try {
            BookServices.getBookListByIsbn(getDb(), "Invalid!");
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
        BookInfo lettresDeMonMoulin = new BookInfo();
        lettresDeMonMoulin.title = Books.LETTRES_DE_MON_MOULIN;
        lettresDeMonMoulin.isbn10 = Isbn10s.LETTRES_DE_MON_MOULIN.toLowerCase();

        BookServices.saveBookInfo(getDb(), lettresDeMonMoulin);

        List<Book> bookList = BookServices.getBookListByIsbn(getDb(), Isbn10s.LETTRES_DE_MON_MOULIN.toUpperCase());

        assertEquals(1, bookList.size());

        Book book = bookList.get(0);

        assertEquals(lettresDeMonMoulin.id.longValue(), book.id.longValue());
        assertEquals(lettresDeMonMoulin.title, book.title);
    }

    /**
     * Test of getBookListByIsbn with several books having the same ISBN.
     */
    public void testGetBookListByIsbnMultipleResults() {
        BookInfo lettresDeMonMoulin1 = new BookInfo();
        lettresDeMonMoulin1.title = Books.LETTRES_DE_MON_MOULIN;
        lettresDeMonMoulin1.isbn10 = Isbn10s.LETTRES_DE_MON_MOULIN;

        BookInfo lettresDeMonMoulin2 = new BookInfo(lettresDeMonMoulin1);

        BookServices.saveBookInfo(getDb(), lettresDeMonMoulin1);
        BookServices.saveBookInfo(getDb(), lettresDeMonMoulin2);

        List<Book> bookList = BookServices.getBookListByIsbn(getDb(), Isbn10s.LETTRES_DE_MON_MOULIN);

        assertEquals(2, bookList.size());

        Book book1 = bookList.get(0);
        assertEquals(lettresDeMonMoulin1.id.longValue(), book1.id.longValue());
        assertEquals(lettresDeMonMoulin1.title, book1.title);

        Book book2 = bookList.get(1);
        assertEquals(lettresDeMonMoulin2.id.longValue(), book2.id.longValue());
        assertEquals(lettresDeMonMoulin2.title, book2.title);
    }
}
