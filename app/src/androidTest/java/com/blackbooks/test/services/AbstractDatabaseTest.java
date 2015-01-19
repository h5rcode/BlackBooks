package com.blackbooks.test.services;

import junit.framework.Assert;
import android.database.sqlite.SQLiteConstraintException;
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

	private static String SQLITE_ERROR_19 = "error code 19: constraint failed";

	private SQLiteDatabase mDb;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		RenamingDelegatingContext ctx = new RenamingDelegatingContext(getContext(), "test-");
		SQLiteHelper dbHelper = new SQLiteHelper(ctx);
		mDb = dbHelper.getWritableDatabase();
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

	/**
	 * Asserts that the cause of a given {@link SQLiteConstraintException} is a
	 * constraint violation.
	 * 
	 * @param e
	 *            SQLiteConstraintException.
	 */
	protected static void assertConstraintFailed(SQLiteConstraintException e) {
		Assert.assertEquals(SQLITE_ERROR_19, e.getMessage());
	}
}
