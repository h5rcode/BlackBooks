package com.blackbooks.services;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.support.v4.util.LongSparseArray;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.BookAuthor;
import com.blackbooks.model.persistent.BookCategory;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.model.persistent.Identifier;
import com.blackbooks.sql.BrokerManager;

/**
 * Book services.
 * 
 */
public class BookServices {

	/**
	 * Delete a book, and its links to its authors. If the deleted book was the
	 * only book of its author(s) in the database, the author(s) is (are) also
	 * deleted.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param bookId
	 *            Id of a book.
	 */
	public static void deleteBook(SQLiteDatabase db, long bookId) {
		db.beginTransaction();
		try {
			List<BookAuthor> baListByBook = BookAuthorServices.getBookAuthorListByBook(db, bookId);
			List<BookCategory> bcListByBook = BookCategoryServices.getBookCategoryListByBook(db, bookId);
			BrokerManager.getBroker(Book.class).delete(db, bookId);

			for (BookAuthor ba : baListByBook) {
				List<BookAuthor> baListByAuthor = BookAuthorServices.getBookAuthorListByAuthor(db, ba.authorId);

				if (baListByAuthor.isEmpty()) {
					BrokerManager.getBroker(Author.class).delete(db, ba.authorId);
				}
			}

			for (BookCategory bc : bcListByBook) {
				List<BookCategory> bcListByCategory = BookCategoryServices.getBookCategoryListByCategory(db, bc.categoryId);

				if (bcListByCategory.isEmpty()) {
					BrokerManager.getBroker(Category.class).delete(db, bc.categoryId);
				}
			}

			PublisherServices.deletePublishersWithoutBooks(db);

			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * Get the books of an autor.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param authorId
	 *            Id of an author.
	 * @return List of books.
	 */
	public static List<Book> getBookListByAuthor(SQLiteDatabase db, long authorId) {
		String sql = "SELECT boo.* FROM BOOK_AUTHOR bka JOIN BOOk boo ON boo.BOO_ID = bka.BOO_ID WHERE bka.AUT_ID = ? ORDER BY BOO_TITLE";
		String[] selectionArgs = { String.valueOf(authorId) };
		return BrokerManager.getBroker(Book.class).rawSelect(db, sql, selectionArgs);
	}

	/**
	 * Get all the books of a given publisher.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param publisherId
	 *            Id of the publisher.
	 * @return List of Book.
	 */
	public static List<Book> getBookListByPublisher(SQLiteDatabase db, long publisherId) {
		Book book = new Book();
		book.publisherId = publisherId;

		return BrokerManager.getBroker(Book.class).getAllByCriteria(db, book);
	}

	/**
	 * Get book info.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param bookId
	 *            Id of a book.
	 * @return BookInfo.
	 */
	public static BookInfo getBookInfo(SQLiteDatabase db, long bookId) {
		Book book = BrokerManager.getBroker(Book.class).get(db, bookId);
		BookInfo bookInfo = new BookInfo(book);

		List<BookAuthor> bookAuthorList = BookAuthorServices.getBookAuthorListByBook(db, book.id);
		for (BookAuthor bookAuthor : bookAuthorList) {
			Author author = AuthorServices.getAuthor(db, bookAuthor.authorId);
			bookInfo.authors.add(author);
		}

		if (book.publisherId != null) {
			bookInfo.publisher = PublisherServices.getPublisher(db, book.publisherId);
		}

		List<BookCategory> bookCategoryList = BookCategoryServices.getBookCategoryListByBook(db, book.id);
		for (BookCategory bookCategory : bookCategoryList) {
			Category category = CategoryServices.getCategory(db, bookCategory.categoryId);
			bookInfo.categories.add(category);
		}

		List<Identifier> identifierList = IdentifierServices.getIdentifierListByBook(db, bookId);
		bookInfo.identifiers = identifierList;

		return bookInfo;
	}

	/**
	 * Get the info of all the books in the database.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @return List of BookInfo.
	 */
	public static List<BookInfo> getBookInfoList(SQLiteDatabase db) {
		List<Book> bookList = BrokerManager.getBroker(Book.class).getAll(db, null, new String[] { Book.Cols.BOO_TITLE });
		return getBookInfoList(db, bookList);
	}

	/**
	 * Save a BookInfo.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param bookInfo
	 *            BookInfo.
	 */
	public static void saveBookInfo(SQLiteDatabase db, BookInfo bookInfo) {
		db.beginTransaction();
		try {
			if (bookInfo.publisher.name != null) {
				PublisherServices.savePublisher(db, bookInfo.publisher);
				bookInfo.publisherId = bookInfo.publisher.id;
			}

			BrokerManager.getBroker(Book.class).save(db, bookInfo);

			PublisherServices.deletePublishersWithoutBooks(db);
			updateBookAuthorList(db, bookInfo);
			updateBookCategoryList(db, bookInfo);
			updateIdentifierList(db, bookInfo);

			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

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
		String sql = "SELECT * FROM BOOK WHERE BOO_TITLE LIKE '%' || ? || '%' ORDER BY BOO_TITLE";
		String selection[] = new String[] { query };
		List<Book> bookList = BrokerManager.getBroker(Book.class).rawSelect(db, sql, selection);
		return getBookInfoList(db, bookList);
	}

	/**
	 * When given a list of {@link Book}, build a list of {@link BookInfo}
	 * containing the original information of the books plus the list of their
	 * authors.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param bookList
	 *            List of {@link Book}.
	 * @return List of {@link BookInfo}.
	 */
	private static List<BookInfo> getBookInfoList(SQLiteDatabase db, List<Book> bookList) {
		List<BookInfo> bookInfoList = new ArrayList<BookInfo>();

		if (!bookList.isEmpty()) {
			List<Long> bookIdList = new ArrayList<Long>();
			for (Book book : bookList) {
				bookIdList.add(book.id);
			}

			List<BookAuthor> bookAuthorList = BrokerManager.getBroker(BookAuthor.class).getAllWhereIn(db, BookAuthor.Cols.BOO_ID, bookIdList);

			List<Long> authorIdList = new ArrayList<Long>();
			for (BookAuthor bookAuthor : bookAuthorList) {
				if (!authorIdList.contains(bookAuthor.authorId)) {
					authorIdList.add(bookAuthor.authorId);
				}
			}

			List<Author> authorList = BrokerManager.getBroker(Author.class).getAllWhereIn(db, Author.Cols.AUT_ID, authorIdList);

			LongSparseArray<List<BookAuthor>> bookAuthorMap = new LongSparseArray<List<BookAuthor>>();
			LongSparseArray<Author> authorMap = new LongSparseArray<Author>();
			for (BookAuthor bookAuthor : bookAuthorList) {
				if (bookAuthorMap.get(bookAuthor.bookId) == null) {
					bookAuthorMap.put(bookAuthor.bookId, new ArrayList<BookAuthor>());
				}
				List<BookAuthor> baList = bookAuthorMap.get(bookAuthor.bookId);
				baList.add(bookAuthor);
			}
			for (Author author : authorList) {
				authorMap.put(author.id, author);
			}

			for (Book book : bookList) {
				BookInfo bookInfo = new BookInfo(book);

				List<BookAuthor> baList = bookAuthorMap.get(book.id);
				if (baList != null) {
					for (BookAuthor bookAuthor : baList) {
						Author author = authorMap.get(bookAuthor.authorId);
						bookInfo.authors.add(author);
					}
				}

				bookInfoList.add(bookInfo);
			}
		}
		return bookInfoList;
	}

	/**
	 * Delete the previous BookAuthor relationships of a book and create the new
	 * ones.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param bookInfo
	 *            BookInfo.
	 */
	private static void updateBookAuthorList(SQLiteDatabase db, BookInfo bookInfo) {
		BookAuthorServices.deleteBookAuthorListByBook(db, bookInfo.id);
		for (Author author : bookInfo.authors) {
			AuthorServices.saveAuthor(db, author);

			BookAuthor bookAuthor = new BookAuthor();
			bookAuthor.authorId = author.id;
			bookAuthor.bookId = bookInfo.id;

			BookAuthorServices.saveBookAuthor(db, bookAuthor);
		}
		AuthorServices.deleteAuthorsWithoutBooks(db);
	}

	/**
	 * Delete the previous BookCategory relationships of a book and create the
	 * new ones.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param bookInfo
	 *            BookInfo.
	 */
	private static void updateBookCategoryList(SQLiteDatabase db, BookInfo bookInfo) {
		BookCategoryServices.deleteBookCategoryListByBook(db, bookInfo.id);
		for (Category category : bookInfo.categories) {
			CategoryServices.saveCategory(db, category);

			BookCategory bookCategory = new BookCategory();
			bookCategory.bookId = bookInfo.id;
			bookCategory.categoryId = category.id;

			BookCategoryServices.saveBookCategory(db, bookCategory);
		}
		CategoryServices.deleteCategoriesWithoutBooks(db);
	}

	/**
	 * Delete the identifiers of a book and create the new ones.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param bookInfo
	 *            BookInfo.
	 */
	private static void updateIdentifierList(SQLiteDatabase db, BookInfo bookInfo) {
		IdentifierServices.deleteIdentifierListByBook(db, bookInfo.id);
		for (Identifier identifier : bookInfo.identifiers) {
			identifier.id = null;
			identifier.bookId = bookInfo.id;
			IdentifierServices.saveIdentifier(db, identifier);
		}
	}
}
