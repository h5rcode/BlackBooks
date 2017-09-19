package com.blackbooks.test.repositories.bookrepository;

import com.blackbooks.BuildConfig;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.BookLocation;
import com.blackbooks.sql.Broker;
import com.blackbooks.sql.BrokerManager;
import com.blackbooks.test.data.BookLocations;
import com.blackbooks.test.data.Books;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class GetBooksByBookLocationTest extends AbstractBookRepositoryTest {

    @Test
    public void getBooksByBookLocation_should_return_the_expected_results() {
        BookLocation bookLocation = new BookLocation();
        bookLocation.name = BookLocations.LIVING_ROOM;

        Book book1 = new Book();
        book1.title = Books.HARRY_POTTER_AND_THE_CHAMBER_OF_SECRETS;

        Book book2 = new Book();
        book2.title = Books.HARRY_POTTER_AND_THE_PHILOSOPHER_S_STONE;

        Broker<Book> bookBroker = BrokerManager.getBroker(Book.class);
        Broker<BookLocation> bookLocationBroker = BrokerManager.getBroker(BookLocation.class);

        bookLocationBroker.save(db, bookLocation);

        book1.bookLocationId = bookLocation.id;
        book2.bookLocationId = bookLocation.id;

        bookBroker.save(db, book1);
        bookBroker.save(db, book2);

        List<Book> books = bookRepository.getBooksByBookLocation(bookLocation.id, Integer.MAX_VALUE, 0);

        Assert.assertEquals(2, books.size());

        Book bookResult1 = books.get(0);
        Book bookResult2 = books.get(1);

        Assert.assertEquals(book1.id.longValue(), bookResult1.id.longValue());
        Assert.assertEquals(book2.id.longValue(), bookResult2.id.longValue());
    }
}
