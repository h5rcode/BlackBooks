package com.blackbooks.services;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.model.persistent.BookCategory;
import com.blackbooks.sql.BrokerManager;

/**
 * Services related to the BookCategory class.
 */
public class BookCategoryServices {

	/**
	 * Delete all the BookCategory relationships involving a given book.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param bookId
	 *            Id of a book.
	 */
	public static void deleteBookCategoryListByBook(SQLiteDatabase db, long bookId) {
		BookCategory bookCategory = new BookCategory();
		bookCategory.bookId = bookId;
		BrokerManager.getBroker(BookCategory.class).deleteAllByCriteria(db, bookCategory);
	}

	/**
	 * Get the list of BookCategory referencing a book.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param bookId
	 *            Id of a book.
	 * @return List of BookCategory.
	 */
	public static List<BookCategory> getBookCategoryListByBook(SQLiteDatabase db, long bookId) {
		BookCategory bookCategory = new BookCategory();
		bookCategory.bookId = bookId;
		return BrokerManager.getBroker(BookCategory.class).getAllByCriteria(db, bookCategory);
	}

	/**
	 * Get the list of BookCategory referencing a category.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param categoryId
	 *            Id of a category.
	 * @return List of BookCategory.
	 */
	public static List<BookCategory> getBookCategoryListByCategory(SQLiteDatabase db, long categoryId) {
		BookCategory bookCategory = new BookCategory();
		bookCategory.categoryId = categoryId;
		return BrokerManager.getBroker(BookCategory.class).getAllByCriteria(db, bookCategory);
	}

	/**
	 * Save a BookCategory.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param bookCategory
	 *            BookCategory.
	 * @return Id of the saved BookCategory
	 */
	public static long saveBookCategory(SQLiteDatabase db, BookCategory bookCategory) {
		return BrokerManager.getBroker(BookCategory.class).save(db, bookCategory);
	}
}
