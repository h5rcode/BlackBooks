package com.blackbooks.test.repositories.bookrepository;

import com.blackbooks.BuildConfig;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.sql.Broker;
import com.blackbooks.sql.BrokerManager;
import com.blackbooks.test.data.Authors;
import com.blackbooks.test.data.Books;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ReturnBookTest extends AbstractBookRepositoryTest {

    @Test
    public void returnBook_should_set_loanedTo_an_loanDate_to_null() {
        Book bookInfo = new Book();
        bookInfo.title = Books.THE_VOYNICH_MANUSCRIPT;
        bookInfo.loanedTo = Authors.RENE_GOSCINNY;
        bookInfo.loanDate = new Date();

        Broker<Book> bookBroker = BrokerManager.getBroker(Book.class);

        bookBroker.save(db, bookInfo);

        bookRepository.returnBook(bookInfo.id);

        Book bookDb = bookBroker.get(db, bookInfo.id);

        Assert.assertNull(bookDb.loanedTo);
        Assert.assertNull(bookDb.loanDate);
    }
}
