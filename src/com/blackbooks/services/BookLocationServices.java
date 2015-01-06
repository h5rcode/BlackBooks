package com.blackbooks.services;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.support.v4.util.LongSparseArray;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.nonpersistent.BookLocationInfo;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.BookLocation;
import com.blackbooks.sql.BrokerManager;

/**
 * Book location services.
 */
public class BookLocationServices {

	/**
	 * Delete the book locations that are not referred by any books in the
	 * database.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 */
	public static void deleteBookLocationsWithoutBooks(SQLiteDatabase db) {
		String sql = "DELETE FROM " + BookLocation.NAME + " WHERE " + BookLocation.Cols.BKL_ID + " IN (SELECT bkl."
				+ BookLocation.Cols.BKL_ID + " FROM " + BookLocation.NAME + " bkl LEFT JOIN " + Book.NAME + " boo ON boo."
				+ Book.Cols.BKL_ID + " = bkl." + BookLocation.Cols.BKL_ID + " WHERE boo." + Book.Cols.BOO_ID + " IS NULL)";
		BrokerManager.getBroker(BookLocation.class).executeSql(db, sql);
	}

	/**
	 * Get a book location.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param bookLocationId
	 *            Id of a book location.
	 * @return BookLocation.
	 */
	public static BookLocation getBookLocation(SQLiteDatabase db, Long bookLocationId) {
		return BrokerManager.getBroker(BookLocation.class).get(db, bookLocationId);
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
	public static BookLocation getBookLocationByCriteria(SQLiteDatabase db, BookLocation criteria) {
		return BrokerManager.getBroker(BookLocation.class).getByCriteria(db, criteria);
	}

	/**
	 * Get the info of all the book locations in the database.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @return List of BookLocationInfo.
	 */
	public static List<BookLocationInfo> getBookLocationInfoList(SQLiteDatabase db) {
		List<BookLocation> bookLocationList = BrokerManager.getBroker(BookLocation.class).getAll(db);
		List<BookInfo> bookInfoList = BookServices.getBookInfoList(db);
		LongSparseArray<BookLocationInfo> bookLocationMap = new LongSparseArray<BookLocationInfo>();

		List<BookLocationInfo> bookLocationInfoList = new ArrayList<BookLocationInfo>();
		for (BookLocation bookLocation : bookLocationList) {
			BookLocationInfo bookLocationInfo = new BookLocationInfo(bookLocation);
			bookLocationMap.put(bookLocation.id, bookLocationInfo);
			bookLocationInfoList.add(bookLocationInfo);
		}

		BookLocationInfo unspecifiedBookLocation = new BookLocationInfo();
		for (BookInfo bookInfo : bookInfoList) {
			if (bookInfo.bookLocationId == null) {
				unspecifiedBookLocation.books.add(bookInfo);
			} else {
				BookLocationInfo bookLocationInfo = bookLocationMap.get(bookInfo.bookLocationId);
				bookLocationInfo.books.add(bookInfo);
			}
		}

		if (!unspecifiedBookLocation.books.isEmpty()) {
			bookLocationInfoList.add(unspecifiedBookLocation);
		}

		return bookLocationInfoList;
	}

	/**
	 * Get the list of book locations whose name contains a given text.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param text
	 *            Text.
	 * @return List of BookLocation.
	 */
	public static List<BookLocation> getBookLocationListByText(SQLiteDatabase db, String text) {
		String sql = "SELECT * FROM " + BookLocation.NAME + " WHERE LOWER(" + BookLocation.Cols.BKL_NAME
				+ ") LIKE '%' || LOWER(?) || '%' ORDER BY " + BookLocation.Cols.BKL_NAME;
		String[] selectionArgs = { text };
		return BrokerManager.getBroker(BookLocation.class).rawSelect(db, sql, selectionArgs);
	}

	/**
	 * Save a book location.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param bookLocation
	 *            BookLocation.
	 * @return Id of the saved BookLocation.
	 */
	public static long saveBookLocation(SQLiteDatabase db, BookLocation bookLocation) {
		return BrokerManager.getBroker(BookLocation.class).save(db, bookLocation);
	}
}
