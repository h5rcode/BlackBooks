package com.blackbooks.test.services;

import com.blackbooks.cache.ThumbnailManager;
import com.blackbooks.database.TransactionManager;
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

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractBookServiceTest {

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

    @Mock
    ThumbnailManager thumbnailManager;

    BookServiceImpl bookService;

    @Before
    public void abstractBookServiceTestSetup() {
        bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, thumbnailManager, transactionManager);
    }
}
