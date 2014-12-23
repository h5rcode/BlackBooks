package com.blackbooks.services;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.support.v4.util.LongSparseArray;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.nonpersistent.BookShelfInfo;
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
	 * match the criteria, the method returns null.
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
	 * Get the info of all the bookshelves in the database.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @return List of BookShelfInfo.
	 */
	public static List<BookShelfInfo> getBookShelfInfoList(SQLiteDatabase db) {
		List<BookShelf> bookShelfList = BrokerManager.getBroker(BookShelf.class).getAll(db);
		List<BookInfo> bookInfoList = BookServices.getBookInfoList(db);
		LongSparseArray<BookShelfInfo> bookShelfMap = new LongSparseArray<BookShelfInfo>();

		List<BookShelfInfo> bookShelfInfoList = new ArrayList<BookShelfInfo>();
		for (BookShelf bookShelf : bookShelfList) {
			BookShelfInfo bookShelfInfo = new BookShelfInfo(bookShelf);
			bookShelfMap.put(bookShelf.id, bookShelfInfo);
			bookShelfInfoList.add(bookShelfInfo);
		}

		BookShelfInfo unspecifiedBookShelf = new BookShelfInfo();
		for (BookInfo bookInfo : bookInfoList) {
			if (bookInfo.bookShelfId == null) {
				if (!bookShelfInfoList.contains(unspecifiedBookShelf)) {
					bookShelfInfoList.add(0, unspecifiedBookShelf);
				}
				unspecifiedBookShelf.books.add(bookInfo);
			} else {
				BookShelfInfo bookShelfInfo = bookShelfMap.get(bookInfo.bookShelfId);
				bookShelfInfo.books.add(bookInfo);
			}
		}
		return bookShelfInfoList;
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
		String sql = "SELECT * FROM " + BookShelf.NAME + " WHERE LOWER(" + BookShelf.Cols.BSH_NAME
				+ ") LIKE '%' || LOWER(?) || '%' ORDER BY " + BookShelf.Cols.BSH_NAME;
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
