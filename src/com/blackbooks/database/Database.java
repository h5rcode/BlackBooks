package com.blackbooks.database;

import java.util.ArrayList;
import java.util.List;

import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.BookAuthor;
import com.blackbooks.model.persistent.BookCategory;
import com.blackbooks.model.persistent.BookShelf;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.model.persistent.Publisher;
import com.blackbooks.model.persistent.Series;
import com.blackbooks.model.persistent.fts.BookFTS;

/**
 * Class describing the data base.
 * 
 */
public final class Database {

	/**
	 * Name of the database.
	 */
	public final static String NAME = "BLACK_BOOKS_DB";

	/**
	 * Current version of the database.
	 */
	public final static int VERSION = 1;

	private static Database mInstance = new Database();
	private static List<Class<?>> mTables;
	private static List<Class<?>> mFTSTables;

	/**
	 * Constructor.
	 */
	private Database() {
		mTables = new ArrayList<Class<?>>();

		mTables.add(Series.class);
		mTables.add(BookShelf.class);
		mTables.add(Publisher.class);
		mTables.add(Book.class);
		mTables.add(Author.class);
		mTables.add(BookAuthor.class);
		mTables.add(Category.class);
		mTables.add(BookCategory.class);

		mFTSTables = new ArrayList<Class<?>>();
		mFTSTables.add(BookFTS.class);
	}

	/**
	 * Get the instance of the database.
	 * 
	 * @return Database.
	 */
	public static Database getInstance() {
		return mInstance;
	}

	/**
	 * Get the list of all the tables in the database.
	 * 
	 * @return List<Class<?>>.
	 */
	public List<Class<?>> getTables() {
		return mTables;
	}

	/**
	 * Get the list of all the Full-Text-Search tables in the database.
	 * 
	 * @return List<Class<?>>.
	 */
	public List<Class<?>> getFTSTables() {
		return mFTSTables;
	}
}
