package com.blackbooks.test.repositories.bookrepository;

import com.blackbooks.BuildConfig;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.BookAuthor;
import com.blackbooks.sql.Broker;
import com.blackbooks.sql.BrokerManager;
import com.blackbooks.test.data.Authors;
import com.blackbooks.test.data.Books;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class GetBooksByAuthorTest extends AbstractBookRepositoryTest {

    @Test
    public void getBooksByAuthor_should_return_the_exepected_results() {
        Author author = new Author();
        author.name = Authors.J_K_ROWLING;

        Book book1 = new Book();
        book1.title = Books.HARRY_POTTER_AND_THE_CHAMBER_OF_SECRETS;

        Book book2 = new Book();
        book2.title = Books.HARRY_POTTER_AND_THE_PHILOSOPHER_S_STONE;

        Broker<Book> bookBroker = BrokerManager.getBroker(Book.class);
        Broker<Author> authorBroker = BrokerManager.getBroker(Author.class);
        Broker<BookAuthor> bookAuthorBroker = BrokerManager.getBroker(BookAuthor.class);

        bookBroker.save(db, book1);
        bookBroker.save(db, book2);
        authorBroker.save(db, author);

        BookAuthor bookAuthor1 = new BookAuthor();
        bookAuthor1.authorId = author.id;
        bookAuthor1.bookId = book1.id;

        BookAuthor bookAuthor2 = new BookAuthor();
        bookAuthor2.authorId = author.id;
        bookAuthor2.bookId = book2.id;

        bookAuthorBroker.save(db, bookAuthor1);
        bookAuthorBroker.save(db, bookAuthor2);

        List<Book> books = bookRepository.getBooksByAuthor(author.id, Integer.MAX_VALUE, 0);

        Assert.assertEquals(2, books.size());

        Book bookResult1 = books.get(0);
        Book bookResult2 = books.get(1);

        Assert.assertEquals(book1.id.longValue(), bookResult1.id.longValue());
        Assert.assertEquals(book2.id.longValue(), bookResult2.id.longValue());
    }
}
