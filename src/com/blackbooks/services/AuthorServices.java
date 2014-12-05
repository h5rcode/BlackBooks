package com.blackbooks.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.comparators.BookComparator;
import com.blackbooks.model.nonpersistent.AuthorInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.BookAuthor;
import com.blackbooks.sql.BrokerManager;

/**
 * Author services.
 */
public class AuthorServices {

	/**
	 * Delete the authors that are not referred by any books in the database.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 */
	public static void deleteAuthorsWithoutBooks(SQLiteDatabase db) {
		String sql = "DELETE FROM " + Author.NAME + " WHERE " + Author.Cols.AUT_ID + " IN (SELECT aut." + Author.Cols.AUT_ID
				+ " FROM " + Author.NAME + " aut LEFT JOIN " + BookAuthor.NAME + " bka ON bka." + BookAuthor.Cols.AUT_ID
				+ " = aut." + Author.Cols.AUT_ID + " WHERE bka." + BookAuthor.Cols.BKA_ID + " IS NULL)";

		BrokerManager.getBroker(Author.class).executeSql(db, sql);
	}

	/**
	 * Get an author from the database.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param id
	 *            Id of the author.
	 * @return Author.
	 */
	public static Author getAuthor(SQLiteDatabase db, long id) {
		return BrokerManager.getBroker(Author.class).get(db, id);
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
	public static Author getAuthorByCriteria(SQLiteDatabase db, Author criteria) {
		return BrokerManager.getBroker(Author.class).getByCriteria(db, criteria);
	}

	/**
	 * Get the info of an author.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param authorId
	 *            Id of the author.
	 * @return AuthorInfo.
	 */
	public static AuthorInfo getAuthorInfo(SQLiteDatabase db, long authorId) {
		Author author = BrokerManager.getBroker(Author.class).get(db, authorId);
		AuthorInfo authorInfo = new AuthorInfo(author);
		authorInfo.books = BookServices.getBookListByAuthor(db, authorId);
		return authorInfo;
	}

	/**
	 * Get the info of all the authors in the database.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @return List of AuthorInfo.
	 */
	@SuppressLint("UseSparseArrays")
	public static List<AuthorInfo> getAuthorInfoList(SQLiteDatabase db) {

		String[] selectedColumns = new String[] { Book.Cols.BOO_ID, Book.Cols.BOO_TITLE, Book.Cols.BOO_DESCRIPTION,
				Book.Cols.BOO_IS_READ, Book.Cols.BOO_IS_FAVOURITE };
		List<Book> bookList = BrokerManager.getBroker(Book.class).getAll(db, selectedColumns, null);
		List<BookAuthor> bookAuthorList = BrokerManager.getBroker(BookAuthor.class).getAll(db);
		List<Author> authorList = BrokerManager.getBroker(Author.class).getAll(db, null, new String[] { Author.Cols.AUT_NAME });

		HashMap<Long, List<BookAuthor>> authorBookMap = new HashMap<Long, List<BookAuthor>>();
		HashMap<Long, List<BookAuthor>> bookAuthorMap = new HashMap<Long, List<BookAuthor>>();
		HashMap<Long, Book> bookMap = new HashMap<Long, Book>();
		for (BookAuthor bookAuthor : bookAuthorList) {
			if (!authorBookMap.containsKey(bookAuthor.authorId)) {
				authorBookMap.put(bookAuthor.authorId, new ArrayList<BookAuthor>());
			}
			List<BookAuthor> abList = authorBookMap.get(bookAuthor.authorId);
			abList.add(bookAuthor);

			if (!bookAuthorMap.containsKey(bookAuthor.bookId)) {
				bookAuthorMap.put(bookAuthor.bookId, new ArrayList<BookAuthor>());
			}
			List<BookAuthor> baList = bookAuthorMap.get(bookAuthor.bookId);
			baList.add(bookAuthor);
		}
		List<Book> booksWithoutAuthor = new ArrayList<Book>();
		for (Book book : bookList) {
			bookMap.put(book.id, book);
			if (!bookAuthorMap.containsKey(book.id)) {
				booksWithoutAuthor.add(book);
			}
		}

		BookComparator bookComparator = new BookComparator();
		List<AuthorInfo> authorInfoList = new ArrayList<AuthorInfo>();
		if (booksWithoutAuthor.size() > 0) {
			Collections.sort(booksWithoutAuthor, bookComparator);

			AuthorInfo unspecifiedAuthor = new AuthorInfo();
			unspecifiedAuthor.books = booksWithoutAuthor;
			authorInfoList.add(unspecifiedAuthor);
		}
		for (Author author : authorList) {
			AuthorInfo authorInfo = new AuthorInfo(author);
			List<BookAuthor> baList = authorBookMap.get(author.id);
			for (BookAuthor bookAuthor : baList) {
				Book book = bookMap.get(bookAuthor.bookId);
				authorInfo.books.add(book);
			}

			Collections.sort(authorInfo.books, bookComparator);

			authorInfoList.add(authorInfo);
		}

		return authorInfoList;
	}

	/**
	 * Get the list of authors whose name contains a given text.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param text
	 *            Text.
	 * @return List of Author.
	 */
	public static List<Author> getAuthorListByText(SQLiteDatabase db, String text) {
		String sql = "SELECT * FROM " + Author.NAME + " WHERE LOWER(" + Author.Cols.AUT_NAME
				+ ") LIKE '%' || LOWER(?) || '%' ORDER BY " + Author.Cols.AUT_NAME;
		String[] selectionArgs = { text };
		return BrokerManager.getBroker(Author.class).rawSelect(db, sql, selectionArgs);
	}

	/**
	 * Save an author in the database.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param author
	 *            Author.
	 * @return Id of the saved author.
	 */
	public static long saveAuthor(SQLiteDatabase db, Author author) {
		return BrokerManager.getBroker(Author.class).save(db, author);
	}
}
