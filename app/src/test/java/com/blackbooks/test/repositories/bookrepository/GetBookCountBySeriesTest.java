package com.blackbooks.test.repositories.bookrepository;

import com.blackbooks.BuildConfig;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.Series;
import com.blackbooks.sql.Broker;
import com.blackbooks.sql.BrokerManager;
import com.blackbooks.test.data.Books;
import com.blackbooks.test.data.Seriez;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class GetBookCountBySeriesTest extends AbstractBookRepositoryTest {

    @Test
    public void getBookCountBySeries_should_return_the_expected_value() {
        Series series = new Series();
        series.name = Seriez.HARRY_POTTER;

        Broker<Book> bookBroker = BrokerManager.getBroker(Book.class);
        Broker<Series> seriesBroker = BrokerManager.getBroker(Series.class);

        seriesBroker.save(db, series);

        Book book1 = new Book();
        book1.title = Books.HARRY_POTTER_AND_THE_CHAMBER_OF_SECRETS;
        book1.seriesId = series.id;

        Book book2 = new Book();
        book2.title = Books.HARRY_POTTER_AND_THE_CHAMBER_OF_SECRETS;
        book2.seriesId = series.id;

        bookBroker.save(db, book1);
        bookBroker.save(db, book2);

        int bookCount = bookRepository.getBookCountBySeries(book1.seriesId);

        Assert.assertEquals(2, bookCount);
    }
}
