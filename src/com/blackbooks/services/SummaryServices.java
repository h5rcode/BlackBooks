package com.blackbooks.services;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.model.nonpersistent.Summary;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.BookLocation;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.model.persistent.Series;

/**
 * Summary services.
 */
public class SummaryServices {

	public static Summary getSummary(SQLiteDatabase db) {
		Summary summary = new Summary();

		summary.books = getBooks(db);
		summary.authors = getAuthors(db);
		summary.categories = getCategories(db);
		summary.languages = getLanguages(db);
		summary.series = getSeries(db);
		summary.bookLocations = getBookLocations(db);

		return summary;
	}

	private static int getBooks(SQLiteDatabase db) {
		String sql = "SELECT COUNT(*) FROM " + Book.NAME;
		return queryInt(db, sql);
	}

	private static int getAuthors(SQLiteDatabase db) {
		String sql = "SELECT COUNT(*) FROM " + Author.NAME;
		return queryInt(db, sql);
	}

	private static int getCategories(SQLiteDatabase db) {
		String sql = "SELECT COUNT(*) FROM " + Category.NAME;
		return queryInt(db, sql);
	}

	private static int getLanguages(SQLiteDatabase db) {
		String sql = "SELECT COUNT(DISTINCT " + Book.Cols.BOO_LANGUAGE_CODE + ") FROM " + Book.NAME;
		return queryInt(db, sql);
	}

	private static int getSeries(SQLiteDatabase db) {
		String sql = "SELECT COUNT(*) FROM " + Series.NAME;
		return queryInt(db, sql);
	}

	private static int getBookLocations(SQLiteDatabase db) {
		String sql = "SELECT COUNT(*) FROM " + BookLocation.NAME;
		return queryInt(db, sql);
	}

	private static int queryInt(SQLiteDatabase db, String sql) {
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToNext();
		return cursor.getInt(0);
	}
}
