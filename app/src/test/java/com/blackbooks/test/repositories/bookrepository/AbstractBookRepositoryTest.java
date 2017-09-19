package com.blackbooks.test.repositories.bookrepository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.repositories.BookRepositoryImpl;

import org.junit.After;
import org.junit.Before;
import org.mockito.Mock;
import org.robolectric.RuntimeEnvironment;

class AbstractBookRepositoryTest {

    @Mock
    private Context context;

    private SQLiteHelper sqLiteHelper;

    SQLiteDatabase db;

    BookRepositoryImpl bookRepository;

    @Before
    public void abstractBookRepositoryTestSetup() throws Exception {
        SQLiteHelper.initialize(RuntimeEnvironment.application);
        sqLiteHelper = SQLiteHelper.getInstance();
        db = sqLiteHelper.getWritableDatabase();
        bookRepository = new BookRepositoryImpl(sqLiteHelper);
    }

    @After
    public void abstractBookRepositoryTestTearDown() {
        sqLiteHelper.close();
    }
}
