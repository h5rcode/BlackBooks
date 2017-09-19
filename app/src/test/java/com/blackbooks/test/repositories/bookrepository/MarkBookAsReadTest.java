package com.blackbooks.test.repositories.bookrepository;

import com.blackbooks.BuildConfig;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.sql.Broker;
import com.blackbooks.sql.BrokerManager;
import com.blackbooks.test.data.Books;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MarkBookAsReadTest extends AbstractBookRepositoryTest {

    @Test
    public void markBookAsRead_should_set_isRead_to_1_when_isRead_is_0() {
        Book book = new Book();
        book.title = Books.THE_CATCHER_IN_THE_RYE;

        Broker<Book> bookBroker = BrokerManager.getBroker(Book.class);
        bookBroker.save(db, book);

        bookRepository.markBookAsRead(book.id);

        Book bookDb = bookBroker.get(db, book.id);

        Assert.assertEquals(1L, bookDb.isRead.longValue());
    }

    @Test
    public void markBookAsRead_should_set_isRead_to_0_when_isRead_is_1() {
        Book book = new Book();
        book.title = Books.THE_CATCHER_IN_THE_RYE;
        book.isRead = 1L;

        Broker<Book> bookBroker = BrokerManager.getBroker(Book.class);
        bookBroker.save(db, book);

        bookRepository.markBookAsRead(book.id);

        Book bookDb = bookBroker.get(db, book.id);

        Assert.assertEquals(0L, bookDb.isFavourite.longValue());
    }
}
