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

import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class GetBooksBySeriesTest extends AbstractBookRepositoryTest {

    @Test
    public void getBooksBySeries_should_return_the_expected_results() {
        Series series = new Series();
        series.name = Seriez.HARRY_POTTER;

        Book book1 = new Book();
        book1.title = Books.HARRY_POTTER_AND_THE_CHAMBER_OF_SECRETS;
        book1.number = 1L;

        Book book2 = new Book();
        book2.title = Books.HARRY_POTTER_AND_THE_CHAMBER_OF_SECRETS;
        book2.number = 2L;

        Broker<Book> bookBroker = BrokerManager.getBroker(Book.class);
        Broker<Series> seriesBroker = BrokerManager.getBroker(Series.class);

        seriesBroker.save(db, series);

        book1.seriesId = series.id;
        book2.seriesId = series.id;

        bookBroker.save(db, book1);
        bookBroker.save(db, book2);

        List<Book> books = bookRepository.getBooksBySeries(book1.seriesId, Integer.MAX_VALUE, 0);

        Assert.assertEquals(2, books.size());

        Book bookResult1 = books.get(0);
        Book bookResult2 = books.get(1);

        Assert.assertEquals(book1.id.longValue(), bookResult1.id.longValue());
        Assert.assertEquals(book2.id.longValue(), bookResult2.id.longValue());
    }
}
