package com.blackbooks.test.repositories.bookrepository;

import com.blackbooks.BuildConfig;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.sql.Broker;
import com.blackbooks.sql.BrokerManager;
import com.blackbooks.test.data.Books;
import com.blackbooks.test.data.Languages;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class GetBookCountByLanguageTest extends AbstractBookRepositoryTest {

    @Test
    public void getBookCountByLanguage_should_return_the_expected_value() {
        Book book1 = new Book();
        book1.title = Books.HEART_OF_DARKNESS;
        book1.languageCode = Languages.ENGLISH;

        Book book2 = new Book();
        book2.title = Books.HARRY_POTTER_AND_THE_CHAMBER_OF_SECRETS;
        book2.languageCode = Languages.ENGLISH;

        Broker<Book> bookBroker = BrokerManager.getBroker(Book.class);

        bookBroker.save(db, book1);
        bookBroker.save(db, book2);

        int bookCount = bookRepository.getBookCountByLanguage(Languages.ENGLISH);

        Assert.assertEquals(2, bookCount);
    }
}
