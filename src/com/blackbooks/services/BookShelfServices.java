package com.blackbooks.services;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.model.persistent.BookShelf;
import com.blackbooks.sql.BrokerManager;

/**
 * Book shelf services.
 */
public class BookShelfServices {

	/**
	 * Get a book shelf.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param bookShelfId
	 *            Id of a book shelf.
	 * @return BookShelf.
	 */
	public static BookShelf getBookShelf(SQLiteDatabase db, Long bookShelfId) {
		return BrokerManager.getBroker(BookShelf.class).get(db, bookShelfId);
	}

	/**
	 * Get the one row matching a criteria. If no rows or more that one rows
	 * match the criteria, the method returs null.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param criteria
	 *            The search criteria.
	 * @return Author.
	 */
	public static BookShelf getBookShelfByCriteria(SQLiteDatabase db, BookShelf criteria) {
		return BrokerManager.getBroker(BookShelf.class).getByCriteria(db, criteria);
	}

	/**
	 * Get the list of book shelves whose name contains a given text.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param text
	 *            Text.
	 * @return List of BookShelf.
	 */
	public static List<BookShelf> getBookShelfListByText(SQLiteDatabase db, String text) {
		String sql = "SELECT * FROM BOOK_SHELF WHERE LOWER(BSH_NAME) LIKE '%' || LOWER(?) || '%' ORDER BY BSH_NAME";
		String[] selectionArgs = { text };
		return BrokerManager.getBroker(BookShelf.class).rawSelect(db, sql, selectionArgs);
	}

	/**
	 * Save a book shelf.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param bookShelf
	 *            BookShelf.
	 * @return Id of the saved BookShelf.
	 */
	public static long saveBookShelf(SQLiteDatabase db, BookShelf bookShelf) {
		return BrokerManager.getBroker(BookShelf.class).save(db, bookShelf);
	}
}
