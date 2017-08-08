package com.blackbooks.test.repositories.bookrepository;

import com.blackbooks.BuildConfig;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.sql.Broker;
import com.blackbooks.sql.BrokerManager;
import com.blackbooks.test.data.Books;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class GetFavouriteBooksTest extends AbstractBookRepositoryTest {

    @Test
    public void getFavouriteBooks_should_return_the_expected_results() {
        Book book1 = new Book();
        book1.title = Books.HARRY_POTTER_AND_THE_CHAMBER_OF_SECRETS;
        book1.isFavourite = 1L;

        Book book2 = new Book();
        book2.title = Books.HARRY_POTTER_AND_THE_CHAMBER_OF_SECRETS;
        book2.isFavourite = 1L;

        Broker<Book> bookBroker = BrokerManager.getBroker(Book.class);

        bookBroker.save(db, book1);
        bookBroker.save(db, book2);

        List<Book> bookInfoList = bookRepository.getFavouriteBooks(Integer.MAX_VALUE, 0);

        Assert.assertEquals(2, bookInfoList.size());

        Book bookInfoResult1 = bookInfoList.get(0);
        Book bookInfoResult2 = bookInfoList.get(1);

        Assert.assertEquals(book1.id.longValue(), bookInfoResult1.id.longValue());
        Assert.assertEquals(book2.id.longValue(), bookInfoResult2.id.longValue());
    }
}
