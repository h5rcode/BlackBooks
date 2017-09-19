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

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class GetBookCountByBookLocationTest extends AbstractBookRepositoryTest {

    @Test
    public void getBookCountByBookLocation_should_return_the_expected_value() {

        BookLocation bookLocation = new BookLocation();
        bookLocation.name = BookLocations.LIVING_ROOM;

        Book book1 = new Book();
        book1.title = Books.LE_MYTHE_DE_SISYPHE;

        Book book2 = new Book();
        book2.title = Books.LA_PESTE;

        Book book3 = new Book();
        book3.title = Books.HEART_OF_DARKNESS;

        Broker<Book> bookBroker = BrokerManager.getBroker(Book.class);
        Broker<BookLocation> bookLocationBroker = BrokerManager.getBroker(BookLocation.class);

        bookLocationBroker.save(db, bookLocation);

        book1.bookLocationId = bookLocation.id;
        book2.bookLocationId = bookLocation.id;
        book3.bookLocationId = bookLocation.id;

        bookBroker.save(db, book1);
        bookBroker.save(db, book2);
        bookBroker.save(db, book3);

        int bookCount = bookRepository.getBookCountByBookLocation(book1.bookLocationId);

        Assert.assertEquals(3, bookCount);
    }
}
