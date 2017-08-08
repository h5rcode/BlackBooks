package com.blackbooks.test.repositories.bookrepository;

import com.blackbooks.BuildConfig;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.BookAuthor;
import com.blackbooks.sql.Broker;
import com.blackbooks.sql.BrokerManager;
import com.blackbooks.test.data.Authors;
import com.blackbooks.test.data.Books;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class GetBookCountByAuthorTest extends AbstractBookRepositoryTest {

    @Test
    public void getBookCountByAuthor_should_return_the_expected_value() {
        Author author = new Author();
        author.name = Authors.ALBERT_CAMUS;

        Book book1 = new Book();
        book1.title = Books.LE_MYTHE_DE_SISYPHE;

        Book book2 = new Book();
        book2.title = Books.LA_PESTE;

        Broker<Author> authorBroker = BrokerManager.getBroker(Author.class);
        Broker<Book> bookBroker = BrokerManager.getBroker(Book.class);
        Broker<BookAuthor> bookAuthorBroker = BrokerManager.getBroker(BookAuthor.class);

        authorBroker.save(db, author);
        bookBroker.save(db, book1);
        bookBroker.save(db, book2);

        BookAuthor bookAuthor1 = new BookAuthor();
        bookAuthor1.authorId = author.id;
        bookAuthor1.bookId = book1.id;

        BookAuthor bookAuthor2 = new BookAuthor();
        bookAuthor2.authorId = author.id;
        bookAuthor2.bookId = book2.id;

        bookAuthorBroker.save(db, bookAuthor1);
        bookAuthorBroker.save(db, bookAuthor2);

        int bookCount = bookRepository.getBookCountByAuthor(author.id);

        assertEquals(2, bookCount);
    }
}
