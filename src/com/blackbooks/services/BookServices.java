package com.blackbooks.services;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;

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
			ArrayList<BookAuthor> baListByBook = BookAuthorServices.getBookAuthorListByBook(db, bookId);
			ArrayList<BookCategory> bcListByBook = BookCategoryServices.getBookCategoryListByBook(db, bookId);
			BrokerManager.getBroker(Book.class).delete(db, bookId);

			for (BookAuthor ba : baListByBook) {
				ArrayList<BookAuthor> baListByAuthor = BookAuthorServices.getBookAuthorListByAuthor(db, ba.authorId);

				if (baListByAuthor.size() == 0) {
					BrokerManager.getBroker(Author.class).delete(db, ba.authorId);
				}
			}

			for (BookCategory bc : bcListByBook) {
				ArrayList<BookCategory> bcListByCategory = BookCategoryServices.getBookCategoryListByCategory(db, bc.categoryId);

				if (bcListByCategory.size() == 0) {
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
	public static ArrayList<Book> getBookListByAuthor(SQLiteDatabase db, long authorId) {
		String sql = "SELECT boo.* FROM BOOK_AUTHOR bka JOIN BOOk boo ON boo.BOO_ID = bka.BOO_ID WHERE bka.AUT_ID = ?";
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
	public static ArrayList<Book> getBookListByPublisher(SQLiteDatabase db, long publisherId) {
		Book book = new Book();
		book.publisherId = publisherId;

		return BrokerManager.getBroker(Book.class).getAllByCriteria(db, book);
	}

	/**
	 * Get the titles of all the books in the database.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @return Instances of Book with only id, title and smallThumbnail set.
	 */
	public static ArrayList<Book> getBookListMinimal(SQLiteDatabase db) {
		String[] selectedColumns = new String[] { Book.Cols.BOO_ID, Book.Cols.BOO_TITLE, Book.Cols.BOO_SMALL_THUMBNAIL };
		String[] sortingColumns = new String[] { Book.Cols.BOO_TITLE };
		return BrokerManager.getBroker(Book.class).getAll(db, selectedColumns, sortingColumns);
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

		ArrayList<BookAuthor> bookAuthorList = BookAuthorServices.getBookAuthorListByBook(db, book.id);
		for (BookAuthor bookAuthor : bookAuthorList) {
			Author author = AuthorServices.getAuthor(db, bookAuthor.authorId);
			bookInfo.authors.add(author);
		}

		if (book.publisherId != null) {
			bookInfo.publisher = PublisherServices.getPublisher(db, book.publisherId);
		}

		ArrayList<BookCategory> bookCategoryList = BookCategoryServices.getBookCategoryListByBook(db, book.id);
		for (BookCategory bookCategory : bookCategoryList) {
			Category category = CategoryServices.getCategory(db, bookCategory.categoryId);
			bookInfo.categories.add(category);
		}

		ArrayList<Identifier> identifierList = IdentifierServices.getIdentifierListByBook(db, bookId);
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
	@SuppressLint("UseSparseArrays")
	public static ArrayList<BookInfo> getBookInfoList(SQLiteDatabase db) {

		ArrayList<Book> bookList = BrokerManager.getBroker(Book.class).getAll(db);
		ArrayList<BookAuthor> bookAuthorList = BrokerManager.getBroker(BookAuthor.class).getAll(db);
		ArrayList<Author> authorList = BrokerManager.getBroker(Author.class).getAll(db);

		HashMap<Long, ArrayList<BookAuthor>> bookAuthorMap = new HashMap<Long, ArrayList<BookAuthor>>();
		HashMap<Long, Author> authorMap = new HashMap<Long, Author>();
		for (BookAuthor bookAuthor : bookAuthorList) {
			if (!bookAuthorMap.containsKey(bookAuthor.bookId)) {
				bookAuthorMap.put(bookAuthor.bookId, new ArrayList<BookAuthor>());
			}
			ArrayList<BookAuthor> baList = bookAuthorMap.get(bookAuthor.bookId);
			baList.add(bookAuthor);
		}
		for (Author author : authorList) {
			authorMap.put(author.id, author);
		}

		ArrayList<BookInfo> bookInfoList = new ArrayList<BookInfo>();
		for (Book book : bookList) {
			BookInfo bookInfo = new BookInfo(book);

			ArrayList<BookAuthor> baList = bookAuthorMap.get(book.id);
			for (BookAuthor bookAuthor : baList) {
				Author author = authorMap.get(bookAuthor.authorId);
				bookInfo.authors.add(author);
			}

			bookInfoList.add(bookInfo);
		}

		return bookInfoList;
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
		AuthorServices.deleteAuthorsWithoutBooks(db);
		for (Author author : bookInfo.authors) {
			AuthorServices.saveAuthor(db, author);

			BookAuthor bookAuthor = new BookAuthor();
			bookAuthor.authorId = author.id;
			bookAuthor.bookId = bookInfo.id;

			BookAuthorServices.saveBookAuthor(db, bookAuthor);
		}
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
		CategoryServices.deleteCategoriesWithoutBooks(db);
		for (Category category : bookInfo.categories) {
			CategoryServices.saveCategory(db, category);

			BookCategory bookCategory = new BookCategory();
			bookCategory.bookId = bookInfo.id;
			bookCategory.categoryId = category.id;

			BookCategoryServices.saveBookCategory(db, bookCategory);
		}
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
			identifier.bookId = bookInfo.id;
			IdentifierServices.saveIdentifier(db, identifier);
		}
	}
}
