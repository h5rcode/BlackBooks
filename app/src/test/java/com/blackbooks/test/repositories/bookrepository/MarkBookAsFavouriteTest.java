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
public class MarkBookAsFavouriteTest extends AbstractBookRepositoryTest {

    @Test
    public void markBookAsFavourite_should_set_favourite_to_1_when_favourite_is_0() {
        Book book = new Book();
        book.title = Books.THE_CATCHER_IN_THE_RYE;

        Broker<Book> bookBroker = BrokerManager.getBroker(Book.class);
        bookBroker.save(db, book);

        bookRepository.markBookAsFavourite(book.id);

        Book bookDb = bookBroker.get(db, book.id);

        Assert.assertEquals(1L, bookDb.isFavourite.longValue());
    }

    @Test
    public void markBookAsFavourite_should_set_favourite_to_0_when_favourite_is_1() {
        Book book = new Book();
        book.title = Books.THE_CATCHER_IN_THE_RYE;
        book.isFavourite = 0L;

        Broker<Book> bookBroker = BrokerManager.getBroker(Book.class);
        bookBroker.save(db, book);

        bookRepository.markBookAsFavourite(book.id);

        Book bookDb = bookBroker.get(db, book.id);

        Assert.assertEquals(1L, bookDb.isFavourite.longValue());
    }
}
