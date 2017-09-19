package com.blackbooks.test.services;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import com.blackbooks.database.SQLiteHelper;

/**
 * An abstract class extending {@link AndroidTestCase} that allows the testing
 * of methods requiring a database.<br />
 * <br />
 * The method {@link #setUp()} creates a {@link SQLiteDatabase} that will be
 * available through the method {@link #getDb()}.
 */
public abstract class AbstractDatabaseTest extends AndroidTestCase {

    private SQLiteDatabase mDb;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        RenamingDelegatingContext ctx = new RenamingDelegatingContext(getContext(), "test-");
        SQLiteHelper.initialize(ctx);
        mDb = SQLiteHelper.getInstance().getWritableDatabase();
    }

    /**
     * Return the writable {@link SQLiteDatabase}.
     *
     * @return SQLiteDatabase.
     */
    protected final SQLiteDatabase getDb() {
        return mDb;
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mDb.close();
    }
}
