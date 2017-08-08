package com.blackbooks.test.repositories.bookrepository;


import android.database.sqlite.SQLiteConstraintException;

import com.blackbooks.BuildConfig;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.repositories.BookRepositoryImpl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SaveBookInfoTest extends AbstractBookRepositoryTest {

    @Test(expected = SQLiteConstraintException.class)
    public void saveBookInfo_should_throw_a_SQLiteConstraintException_when_title_is_null() {

        BookRepositoryImpl bookRepository = new BookRepositoryImpl(db);

        BookInfo bookInfo = new BookInfo();
        bookRepository.save(bookInfo);
    }
}
