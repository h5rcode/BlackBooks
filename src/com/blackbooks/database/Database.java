package com.blackbooks.database;

import java.util.ArrayList;

import android.util.SparseArray;

import com.blackbooks.model.metadata.Table;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.BookAuthor;
import com.blackbooks.model.persistent.BookCategory;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.model.persistent.Identifier;
import com.blackbooks.model.persistent.Publisher;

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
	private static ArrayList<Class<?>> mTables;
	private static SparseArray<ArrayList<Class<?>>> mVersionTableMap;

	/**
	 * Constructor.
	 */
	private Database() {
		mTables = new ArrayList<Class<?>>();
		mVersionTableMap = new SparseArray<ArrayList<Class<?>>>();

		mTables.add(Publisher.class);
		mTables.add(Book.class);
		mTables.add(Author.class);
		mTables.add(BookAuthor.class);
		mTables.add(Identifier.class);
		mTables.add(Category.class);
		mTables.add(BookCategory.class);

		initVersionTableMap();
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
	 * @return ArrayList<Class<?>>.
	 */
	public ArrayList<Class<?>> getTables() {
		return mTables;
	}

	/**
	 * Get the list of tables that were added at a given version.
	 * 
	 * @param version
	 *            Version.
	 * @return ArrayList<Class<?>>.
	 */
	public ArrayList<Class<?>> getTablesByVersion(int version) {
		return mVersionTableMap.get(version);
	}

	/**
	 * Fills a map that associates a version and the list of tables that were
	 * added at this version.
	 */
	private void initVersionTableMap() {
		for (Class<?> table : mTables) {
			Table tableAnnotation = table.getAnnotation(Table.class);
			int version = tableAnnotation.version();

			ArrayList<Class<?>> tableList = mVersionTableMap.get(version);
			if (tableList == null) {
				tableList = new ArrayList<Class<?>>();
				mVersionTableMap.put(version, tableList);
			}
			tableList.add(table);
		}
	}
}
