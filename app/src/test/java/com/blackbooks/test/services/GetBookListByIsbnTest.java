package com.blackbooks.test.services;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.test.data.Books;
import com.blackbooks.test.data.Isbn10s;
import com.blackbooks.test.data.Isbn13s;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetBookListByIsbnTest extends AbstractBookServiceTest {

    @Test
    public void getBookListByIsbn_should_return_books_with_the_given_isbn10_when_isbn_is_an_isbn10() {
        Book theGoldenBough = new Book();
        theGoldenBough.id = 356L;
        theGoldenBough.title = Books.THE_GOLDEN_BOUGH;

        List<Book> books = new ArrayList<>();
        books.add(theGoldenBough);

        when(bookRepository.getBooksByIsbn10(Isbn10s.THE_GOLDEN_BOUGH)).thenReturn(books);
        List<Book> bookList = bookService.getBookListByIsbn(Isbn10s.THE_GOLDEN_BOUGH);

        assertEquals(1, bookList.size());

        Book book = bookList.get(0);

        assertEquals(theGoldenBough.id.longValue(), book.id.longValue());
        assertEquals(theGoldenBough.title, book.title);
    }

    @Test
    public void getBookListByIsbn_should_return_books_with_the_given_isbn13_when_isbn_is_an_isbn13() {
        BookInfo beowulf = new BookInfo();
        beowulf.id = 1853L;
        beowulf.title = Books.BEOWULF;
        beowulf.isbn13 = Isbn13s.BEOWULF;

        List<Book> books = new ArrayList<>();
        books.add(beowulf);

        when(bookRepository.getBooksByIsbn13(Isbn13s.BEOWULF)).thenReturn(books);

        List<Book> bookList = bookService.getBookListByIsbn(Isbn13s.BEOWULF);

        assertEquals(1, bookList.size());

        Book book = bookList.get(0);

        assertEquals(beowulf.id.longValue(), book.id.longValue());
        assertEquals(beowulf.title, book.title);
    }

    @Test(expected = InvalidParameterException.class)
    public void getBookListByIsbn_should_throw_an_InvalidParameterException_when_the_isbn_parameter_is_not_a_valid_isbn() {
        bookService.getBookListByIsbn("Invalid!");
    }

    public void getBookListByIsbn_should_return_all_books_with_the_given_isbn() {
        BookInfo lettresDeMonMoulin1 = new BookInfo();
        lettresDeMonMoulin1.title = Books.LETTRES_DE_MON_MOULIN;
        lettresDeMonMoulin1.isbn10 = Isbn10s.LETTRES_DE_MON_MOULIN;

        BookInfo lettresDeMonMoulin2 = new BookInfo(lettresDeMonMoulin1);

        List<Book> books = new ArrayList<>();
        books.add(lettresDeMonMoulin1);
        books.add(lettresDeMonMoulin2);

        when(bookRepository.getBooksByIsbn10(Isbn10s.LETTRES_DE_MON_MOULIN)).thenReturn(books);
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
