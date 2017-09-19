package com.blackbooks.test.repositories.bookrepository;

import com.blackbooks.BuildConfig;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.sql.Broker;
import com.blackbooks.sql.BrokerManager;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class GetBookCountByFirstLetterTest extends AbstractBookRepositoryTest {

    @Test
    public void getBookCountByFirstLetter_should_return_the_expected_value() {
        Book book1 = new Book();
        book1.title = "A";

        Book book2 = new Book();
        book2.title = "a";

        Broker<Book> bookBroker = BrokerManager.getBroker(Book.class);

        bookBroker.save(db, book1);
        bookBroker.save(db, book2);

        int bookCount = bookRepository.getBookCountByFirstLetter("A");

        Assert.assertEquals(2, bookCount);
    }
}
