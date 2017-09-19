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

import java.util.Date;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class GetBookCountByLoanedToTest extends AbstractBookRepositoryTest {

    @Test
    public void getBookCountByLoanedTo_should_return_the_expected_value() {
        Book book1 = new Book();
        book1.title = Books.HEART_OF_DARKNESS;
        book1.loanedTo = People.JOHN_DOE;
        book1.loanDate = new Date();

        Book book2 = new Book();
        book2.title = Books.HARRY_POTTER_AND_THE_CHAMBER_OF_SECRETS;
        book2.loanedTo = People.JOHN_DOE;
        book2.loanDate = new Date();

        Broker<Book> bookBroker = BrokerManager.getBroker(Book.class);
        bookBroker.save(db, book1);
        bookBroker.save(db, book2);

        int bookCount = bookRepository.getBookCountByLoanedTo(People.JOHN_DOE);

        Assert.assertEquals(2, bookCount);
    }
}
