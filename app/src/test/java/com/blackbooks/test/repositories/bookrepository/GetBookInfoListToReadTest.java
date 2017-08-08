package com.blackbooks.test.repositories.bookrepository;

import com.blackbooks.BuildConfig;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.sql.Broker;
import com.blackbooks.sql.BrokerManager;
import com.blackbooks.test.data.Books;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class GetBookInfoListToReadTest extends AbstractBookRepositoryTest {

    @Test
    public void getBookInfoListToRead_should_return_the_expected_result() {
        Book book1 = new Book();
        book1.title = Books.HARRY_POTTER_AND_THE_CHAMBER_OF_SECRETS;

        Book book2 = new Book();
        book2.title = Books.HARRY_POTTER_AND_THE_PHILOSOPHER_S_STONE;

        Broker<Book> bookBroker = BrokerManager.getBroker(Book.class);
        bookBroker.save(db, book1);
        bookBroker.save(db, book2);

        List<Book> books = bookRepository.getBookInfoListToRead(Integer.MAX_VALUE, 0);

        Assert.assertEquals(2, books.size());

        Book bookResult1 = books.get(0);
        Book bookResult2 = books.get(1);

        Assert.assertEquals(book1.id.longValue(), bookResult1.id.longValue());
        Assert.assertEquals(book2.id.longValue(), bookResult2.id.longValue());
    }
}
