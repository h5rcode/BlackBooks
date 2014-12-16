package com.blackbooks.services;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.support.v4.util.LongSparseArray;

import com.blackbooks.cache.ThumbnailManager;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.BookAuthor;
import com.blackbooks.model.persistent.BookCategory;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.model.persistent.fts.BookFTS;
import com.blackbooks.sql.BrokerManager;
import com.blackbooks.sql.FTSBroker;
import com.blackbooks.sql.FTSBrokerManager;

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

			FTSBrokerManager.getBroker(BookFTS.class).delete(db, bookId);

			PublisherServices.deletePublishersWithoutBooks(db);
			SeriesServices.deleteSeriesWithoutBooks(db);

			ThumbnailManager.getInstance().removeThumbnails(bookId);

			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * Get the books of an author.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param authorId
	 *            Id of an author.
	 * @return List of books.
	 */
	public static List<Book> getBookListByAuthor(SQLiteDatabase db, long authorId) {
		String sql = "SELECT boo.* FROM " + BookAuthor.NAME + " bka JOIN " + Book.NAME + " boo ON boo." + Book.Cols.BOO_ID
				+ " = bka." + BookAuthor.Cols.BOO_ID + " WHERE bka." + BookAuthor.Cols.AUT_ID + " = ? ORDER BY "
				+ Book.Cols.BOO_TITLE;
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
	 * Get a book.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param bookId
	 *            Id of a book.
	 * @return Book.
	 */
	public static Book getBook(SQLiteDatabase db, long bookId) {
		return BrokerManager.getBroker(Book.class).get(db, bookId);
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
		if (book.bookShelfId != null) {
			bookInfo.bookShelf = BookShelfServices.getBookShelf(db, book.bookShelfId);
		}
		if (book.seriesId != null) {
			bookInfo.series = SeriesServices.getSeries(db, book.seriesId);
		}

		List<BookCategory> bookCategoryList = BookCategoryServices.getBookCategoryListByBook(db, book.id);
		for (BookCategory bookCategory : bookCategoryList) {
			Category category = CategoryServices.getCategory(db, bookCategory.categoryId);
			bookInfo.categories.add(category);
		}

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
		String[] selectedColumns = new String[] { Book.Cols.BOO_ID, Book.Cols.BOO_TITLE, Book.Cols.BOO_IS_READ,
				Book.Cols.BOO_IS_FAVOURITE };
		String[] sortingColumns = new String[] { Book.Cols.BOO_TITLE };
		List<Book> bookList = BrokerManager.getBroker(Book.class).getAll(db, selectedColumns, sortingColumns);
		return getBookInfoListFromBookList(db, bookList);
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
	public static List<BookInfo> getBookInfoListFromBookList(SQLiteDatabase db, List<Book> bookList) {
		List<BookInfo> bookInfoList = new ArrayList<BookInfo>();

		if (!bookList.isEmpty()) {
			List<Long> bookIdList = new ArrayList<Long>();
			for (Book book : bookList) {
				bookIdList.add(book.id);
			}

			List<BookAuthor> bookAuthorList = BrokerManager.getBroker(BookAuthor.class).getAllWhereIn(db, BookAuthor.Cols.BOO_ID,
					bookIdList);

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
	 * Save a BookInfo.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param bookInfo
	 *            BookInfo.
	 */
	public static void saveBookInfo(SQLiteDatabase db, BookInfo bookInfo) {
		db.beginTransaction();
		int a = 3 / 0;
		try {
			boolean isCreation = bookInfo.id == null;

			if (bookInfo.publisher.name != null) {
				PublisherServices.savePublisher(db, bookInfo.publisher);
				bookInfo.publisherId = bookInfo.publisher.id;
			} else {
				bookInfo.publisherId = null;
			}

			if (bookInfo.bookShelf.name != null) {
				BookShelfServices.saveBookShelf(db, bookInfo.bookShelf);
				bookInfo.bookShelfId = bookInfo.bookShelf.id;
			} else {
				bookInfo.bookShelfId = null;
			}

			if (bookInfo.series.name != null) {
				SeriesServices.saveSeries(db, bookInfo.series);
				bookInfo.seriesId = bookInfo.series.id;
			} else {
				bookInfo.series.id = null;
			}

			BrokerManager.getBroker(Book.class).save(db, bookInfo);

			FTSBroker<BookFTS> brokerBookFTS = FTSBrokerManager.getBroker(BookFTS.class);
			BookFTS bookFts = new BookFTS(bookInfo);
			if (isCreation) {
				brokerBookFTS.insert(db, bookFts);
			} else {
				brokerBookFTS.update(db, bookFts);

				PublisherServices.deletePublishersWithoutBooks(db);
				SeriesServices.deleteSeriesWithoutBooks(db);

				ThumbnailManager.getInstance().removeThumbnails(bookInfo.id);
			}

			updateBookAuthorList(db, bookInfo);
			updateBookCategoryList(db, bookInfo);

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
}
