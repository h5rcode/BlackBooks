package com.blackbooks.test.services;

import com.blackbooks.database.TransactionManager;
import com.blackbooks.model.persistent.BookAuthor;
import com.blackbooks.model.persistent.BookCategory;
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

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeleteBookTest {

    @Mock
    AuthorRepository authorRepository;

    @Mock
    BookAuthorRepository bookAuthorRepository;

    @Mock
    BookCategoryRepository bookCategoryRepository;

    @Mock
    BookFTSRepository bookFTSRepository;

    @Mock
    BookLocationRepository bookLocationRepository;

    @Mock
    BookRepository bookRepository;

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    PublisherRepository publisherRepository;

    @Mock
    SeriesRepository seriesRepository;

    @Mock
    TransactionManager transactionManager;

    @Test
    public void delete_should_delete_the_book() {
        long bookId = 674L;

        BookServiceImpl bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);
        bookService.deleteBook(bookId);

        verify(bookRepository).deleteBook(bookId);
    }

    @Test
    public void delete_should_remove_the_book_from_the_full_text_search_table() {
        long bookId = 35L;

        BookServiceImpl bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);
        bookService.deleteBook(bookId);

        verify(bookFTSRepository).deleteBook(bookId);
    }

    @Test
    public void delete_should_delete_the_book_authors_when_there_are_no_more_books_referring_to_them() {
        long bookId = 35L;

        long author1Id = 53L;
        long author2Id = 65L;

        BookAuthor bookAuthor1 = new BookAuthor();
        bookAuthor1.authorId = author1Id;

        BookAuthor bookAuthor2 = new BookAuthor();
        bookAuthor2.authorId = author2Id;

        List<BookAuthor> bookAuthorList = new ArrayList<>();
        bookAuthorList.add(bookAuthor1);
        bookAuthorList.add(bookAuthor2);

        when(bookAuthorRepository.getBookAuthorListByBook(bookId)).thenReturn(bookAuthorList);
        when(bookAuthorRepository.getBookAuthorListByAuthor(author1Id)).thenReturn(new ArrayList<BookAuthor>());
        when(bookAuthorRepository.getBookAuthorListByAuthor(author2Id)).thenReturn(new ArrayList<BookAuthor>());

        BookServiceImpl bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);
        bookService.deleteBook(bookId);

        verify(authorRepository).deleteAuthor(author1Id);
        verify(authorRepository).deleteAuthor(author2Id);
    }

    @Test
    public void delete_should_delete_categories_when_there_are_no_more_books_referring_to_them() {
        long bookId = 35L;

        long category1Id = 53L;
        long category2Id = 65L;

        BookCategory bookCategory1 = new BookCategory();
        bookCategory1.categoryId = category1Id;

        BookCategory bookCategory2 = new BookCategory();
        bookCategory2.categoryId = category2Id;

        List<BookCategory> bookCategoryList = new ArrayList<>();
        bookCategoryList.add(bookCategory1);
        bookCategoryList.add(bookCategory2);

        when(bookCategoryRepository.getBookCategoryListByBook(bookId)).thenReturn(bookCategoryList);
        when(bookCategoryRepository.getBookCategoryListByCategory(category1Id)).thenReturn(new ArrayList<BookCategory>());
        when(bookCategoryRepository.getBookCategoryListByCategory(category2Id)).thenReturn(new ArrayList<BookCategory>());

        BookServiceImpl bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);
        bookService.deleteBook(bookId);

        verify(categoryRepository).deleteCategory(category1Id);
        verify(categoryRepository).deleteCategory(category2Id);
    }

    @Test
    public void delete_should_delete_publishers_without_books() {
        BookServiceImpl bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);
        bookService.deleteBook(3129L);

        verify(seriesRepository).deleteSeriesWithoutBooks();
    }

    @Test
    public void delete_should_delete_series_without_books() {
        BookServiceImpl bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);
        bookService.deleteBook(192L);

        verify(seriesRepository).deleteSeriesWithoutBooks();
    }

    @Test
    public void delete_should_begin_a_transaction() {
        BookServiceImpl bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);
        bookService.deleteBook(32L);

        verify(transactionManager).beginTransaction();
    }

    @Test
    public void delete_should_end_the_transaction() {
        BookServiceImpl bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);
        bookService.deleteBook(264L);

        verify(transactionManager).endTransaction();
    }

    @Test
    public void delete_should_end_the_transaction_when_an_error_occurs() {
        long bookId = 554L;
        doThrow(new RuntimeException()).when(bookRepository).deleteBook(bookId);

        BookServiceImpl bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);

        try {
            bookService.deleteBook(bookId);
            Assert.fail();
        } catch (RuntimeException e) {
            verify(transactionManager).endTransaction();
        }
    }

    @Test
    public void delete_should_set_the_transaction_successful_when_no_error_occurs() {
        BookServiceImpl bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);
        bookService.deleteBook(32L);

        verify(transactionManager).setTransactionSuccessful();
    }

    @Test
    public void delete_should_not_set_the_transaction_successful_when_an_error_occurs() {
        long bookId = 986L;
        doThrow(new RuntimeException()).when(bookRepository).deleteBook(bookId);

        BookServiceImpl bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);

        try {
            bookService.deleteBook(bookId);
        } catch (RuntimeException e) {
            // Do nothing.
        } finally {
            verify(transactionManager, never()).setTransactionSuccessful();
        }
    }
}
