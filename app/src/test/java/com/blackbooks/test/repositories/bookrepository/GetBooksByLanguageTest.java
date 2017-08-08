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

import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class GetBooksByLanguageTest extends AbstractBookRepositoryTest {

    @Test
    public void getBooksByLanguage_should_return_the_expected_results() {
        Book book1 = new Book();
        book1.title = Books.THE_GOLDEN_BOUGH;
        book1.languageCode = Languages.ENGLISH;

        Book book2 = new Book();
        book2.title = Books.HEART_OF_DARKNESS;
        book2.languageCode = Languages.ENGLISH;

        Broker<Book> bookBroker = BrokerManager.getBroker(Book.class);
        bookBroker.save(db, book1);
        bookBroker.save(db, book2);

        List<Book> books = bookRepository.getBooksByLanguage(Languages.ENGLISH, Integer.MAX_VALUE, 0);

        Assert.assertEquals(2, books.size());

        Book bookResult1 = books.get(0);
        Book bookResult2 = books.get(1);

        Assert.assertEquals(book2.id.longValue(), bookResult1.id.longValue());
        Assert.assertEquals(book1.id.longValue(), bookResult2.id.longValue());
    }
}
