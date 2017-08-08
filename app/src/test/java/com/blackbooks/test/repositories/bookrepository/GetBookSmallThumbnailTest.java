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
public class GetBookSmallThumbnailTest extends AbstractBookRepositoryTest {

    @Test
    public void getBookSmallThumbnail_should_return_the_expected_value() {
        byte[] smallThumbnail = new byte[]{0, 1, 2, 3};

        Book book = new Book();
        book.title = Books.THE_CATCHER_IN_THE_RYE;
        book.smallThumbnail = smallThumbnail;

        Broker<Book> bookBroker = BrokerManager.getBroker(Book.class);
        bookBroker.save(db, book);

        byte[] smallThumbnailDb = bookRepository.getBookSmallThumbnail(book.id);

        Assert.assertNotNull(smallThumbnailDb);
        Assert.assertEquals(smallThumbnail.length, smallThumbnailDb.length);
    }
}
