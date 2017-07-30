package com.blackbooks.test.services;

import com.blackbooks.database.TransactionManager;
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
import com.blackbooks.test.data.Authors;
import com.blackbooks.test.data.Books;
import com.blackbooks.test.data.Categories;
import com.blackbooks.test.data.Publishers;
import com.blackbooks.test.data.Seriez;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DeleteBookTest extends AbstractDatabaseTest {

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
     * Test the deletion of a book that only has a title.
     */
    public void testDeleteBookBasic() {
        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.ASTERIX_LE_GAULOIS;

        BookService bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);

        bookService.deleteBook(bookInfo.id);

        // TODO
    }

    /**
     * Verify that a deleted book is no longer present in the corresponding
     * Full-Text-Search table (FTS).
     */
    public void testDeleteBookFullTextSearch() {
        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.THE_CATCHER_IN_THE_RYE;

        BookService bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);
        bookService.deleteBook(bookInfo.id);

        // TODO
    }

    /**
     * Test the deletion of a book having authors. The authors must also be
     * deleted because there is no other books referring to them.
     */
    public void testDeleteBookWithAuthors() {
        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.ASTERIX_LE_GAULOIS;

        Author author1 = new Author();
        author1.name = Authors.RENE_GOSCINNY;

        Author author2 = new Author();
        author2.name = Authors.ALBERT_UDERZO;

        bookInfo.authors.add(author1);
        bookInfo.authors.add(author2);

        BookService bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);
        bookService.deleteBook(bookInfo.id);

        // TODO
    }

    /**
     * Test the deletion of a book having categories. The categories must also
     * be deleted because there is no other books referring to them.
     */
    public void testDeleteBookWithCategories() {
        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.ASTERIX_LE_GAULOIS;

        Category category1 = new Category();
        category1.name = Categories.FRENCH_COMICS;

        Category category2 = new Category();
        category2.name = Categories.HUMOR;

        bookInfo.categories.add(category1);
        bookInfo.categories.add(category2);

        BookService bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);
        bookService.deleteBook(bookInfo.id);

        // TODO
    }

    /**
     * Test the deletion of a book having a publisher. The publisher must also
     * be deleted because there is no other books referring to it.
     */
    public void testDeleteBookWithPublisher() {
        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.LE_MYTHE_DE_SISYPHE;
        bookInfo.publisher.name = Publishers.GALLIMARD;

        BookService bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);
        bookService.deleteBook(bookInfo.id);

        // TODO
    }

    /**
     * Test the deletion of a book having a series. The series must also be
     * deleted because there is no other books referring to it.
     */
    public void testDeleteBookWithSeries() {
        BookInfo bookInfo = new BookInfo();
        bookInfo.title = Books.ASTERIX_LE_GAULOIS;
        bookInfo.series.name = Seriez.ASTERIX;

        BookService bookService = new BookServiceImpl(authorRepository, bookAuthorRepository, bookCategoryRepository, bookFTSRepository, bookLocationRepository, bookRepository, categoryRepository, publisherRepository, seriesRepository, transactionManager);
        bookService.deleteBook(bookInfo.id);

        // TODO
    }
}
