package com.blackbooks.test.repositories.bookrepository;

import com.blackbooks.BuildConfig;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.sql.Broker;
import com.blackbooks.sql.BrokerManager;
import com.blackbooks.test.data.Books;
import com.blackbooks.test.data.People;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class GetBooksByLoanedToTest extends AbstractBookRepositoryTest {

    @Test
    public void getBookCountByLoanedTo_should_return_the_expected_value() {
        Book book1 = new Book();
        book1.title = Books.THE_GOLDEN_BOUGH;
        book1.loanedTo = People.JOHN_DOE;

        Book book2 = new Book();
        book2.title = Books.HEART_OF_DARKNESS;
        book2.loanedTo = People.JOHN_DOE;

        Broker<Book> bookBroker = BrokerManager.getBroker(Book.class);

        bookBroker.save(db, book1);
        bookBroker.save(db, book2);

        List<Book> books = bookRepository.getBooksByLoanedTo(People.JOHN_DOE, Integer.MAX_VALUE, 0);

        Assert.assertEquals(2, books.size());

        Book bookResult1 = books.get(0);
        Book bookResult2 = books.get(1);

        Assert.assertEquals(book2.id.longValue(), bookResult1.id.longValue());
        Assert.assertEquals(book1.id.longValue(), bookResult2.id.longValue());
    }
}
