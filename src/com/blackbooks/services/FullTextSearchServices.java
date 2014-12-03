package com.blackbooks.services;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.fts.BookFTS;
import com.blackbooks.sql.BrokerManager;
import com.blackbooks.utils.StringUtils;

/**
 * Full-Text-Search services.
 */
public class FullTextSearchServices {

	/**
	 * Search the books whose title matched the query.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param query
	 *            The searched text.
	 * @return The books whose title contains the given query.
	 */
	public static List<BookInfo> searchBooks(SQLiteDatabase db, String query) {

		String sql = "SELECT book." + Book.Cols.BOO_ID + ", book." + Book.Cols.BOO_TITLE + ", book." + Book.Cols.BOO_SUBTITLE
				+ ", book." + Book.Cols.BOO_DESCRIPTION + " FROM " + BookFTS.NAME + " book_fts JOIN " + Book.NAME
				+ " book ON book." + Book.Cols.BOO_ID + " = book_fts." + BookFTS.Cols.DOCID + " WHERE book_fts MATCH ?";

		String term = StringUtils.normalize(query);
		term += "*";
		String selection[] = new String[] { term };
		List<Book> bookList = BrokerManager.getBroker(Book.class).rawSelect(db, sql, selection);
		return BookServices.getBookInfoListFromBookList(db, bookList);
	}

}
