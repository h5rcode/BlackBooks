package com.blackbooks.test.repositories.bookrepository;


import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.BuildConfig;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.repositories.BookRepositoryImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SaveBookInfoTest {

    @Mock
    private Context context;

    private SQLiteHelper sqLiteHelper;

    @Before
    public void setUp() throws Exception {
        SQLiteHelper.initialize(RuntimeEnvironment.application);
        sqLiteHelper = SQLiteHelper.getInstance();
    }

    @After
    public void tearDown() {
        sqLiteHelper.close();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void saveBookInfo_should_throw_a_SQLiteConstraintException_when_title_is_null() {
        SQLiteDatabase db = sqLiteHelper.getWritableDatabase();

        BookRepositoryImpl bookRepository = new BookRepositoryImpl(db);

        BookInfo bookInfo = new BookInfo();
        bookRepository.save(bookInfo);
    }
}
