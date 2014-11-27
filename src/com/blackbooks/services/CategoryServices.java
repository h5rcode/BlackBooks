package com.blackbooks.services;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.support.v4.util.LongSparseArray;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.nonpersistent.CategoryInfo;
import com.blackbooks.model.persistent.BookCategory;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.sql.BrokerManager;

/**
 * Services related to the Category class.
 */
public class CategoryServices {

	/**
	 * Delete the categories that are not referred by any books in the database.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 */
	public static void deleteCategoriesWithoutBooks(SQLiteDatabase db) {
		String sql = "DELETE FROM CATEGORY WHERE CAT_ID IN (SELECT cat.CAT_ID FROM CATEGORY cat LEFT JOIN BOOK_CATEGORY bca ON bca.CAT_ID = cat.CAT_ID WHERE bca.BCA_ID IS NULL)";
		BrokerManager.getBroker(Category.class).executeSql(db, sql);
	}

	/**
	 * Delete a category.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param categoryId
	 *            Id of a category.
	 */
	public static void deleteCategory(SQLiteDatabase db, long categoryId) {
		BrokerManager.getBroker(Category.class).delete(db, categoryId);
	}

	/**
	 * Get a category from the database.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param catId
	 *            Id of the category.
	 * @return Category.
	 */
	public static Category getCategory(SQLiteDatabase db, long catId) {
		return BrokerManager.getBroker(Category.class).get(db, catId);
	}

	/**
	 * Get the one row matching a criteria. If no rows or more that one rows
	 * match the criteria, the method returs null.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param criteria
	 *            The search criteria.
	 * @return Category.
	 */
	public static Category getCategoryByCriteria(SQLiteDatabase db, Category criteria) {
		return BrokerManager.getBroker(Category.class).getByCriteria(db, criteria);
	}

	public static List<CategoryInfo> getCategoryInfoList(SQLiteDatabase db) {

		List<BookInfo> bookInfoList = BookServices.getBookInfoList(db);
		List<BookCategory> bcList = BrokerManager.getBroker(BookCategory.class).getAll(db);
		List<Category> categoryList = BrokerManager.getBroker(Category.class).getAll(db, null,
				new String[] { Category.Cols.CAT_NAME });

		LongSparseArray<CategoryInfo> categoryMap = new LongSparseArray<CategoryInfo>();
		LongSparseArray<List<Long>> bcMap = new LongSparseArray<List<Long>>();

		List<CategoryInfo> categoryInfoList = new ArrayList<CategoryInfo>();
		for (Category category : categoryList) {
			CategoryInfo categoryInfo = new CategoryInfo(category);
			categoryInfoList.add(categoryInfo);
			categoryMap.put(categoryInfo.id, categoryInfo);
		}
		for (BookCategory bookCategory : bcList) {
			List<Long> cList = bcMap.get(bookCategory.bookId);
			if (cList == null) {
				cList = new ArrayList<Long>();
				bcMap.put(bookCategory.bookId, cList);
			}
			cList.add(bookCategory.categoryId);
		}

		CategoryInfo categoryWithoutBooks = null;
		for (BookInfo bookInfo : bookInfoList) {
			List<Long> cList = bcMap.get(bookInfo.id);
			if (cList == null) {
				if (categoryWithoutBooks == null) {
					categoryWithoutBooks = new CategoryInfo();
				}
				categoryWithoutBooks.books.add(bookInfo);
			} else {
				for (Long c : cList) {
					CategoryInfo category = categoryMap.get(c);
					category.books.add(bookInfo);
				}
			}
		}
		if (categoryWithoutBooks != null) {
			categoryInfoList.add(0, categoryWithoutBooks);
		}

		return categoryInfoList;
	}

	/**
	 * Get the list of categories whose name contains a given text.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param text
	 *            Text.
	 * @return List of Category.
	 */
	public static List<Category> getCategoryListByText(SQLiteDatabase db, String text) {
		String sql = "SELECT * FROM CATEGORY WHERE LOWER(CAT_NAME) LIKE '%' || LOWER(?) || '%' ORDER BY CAT_NAME";
		String[] selectionArgs = { text };
		return BrokerManager.getBroker(Category.class).rawSelect(db, sql, selectionArgs);
	}

	/**
	 * Save a category.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param category
	 *            Category.
	 * @return Id of the saved category.
	 */
	public static long saveCategory(SQLiteDatabase db, Category category) {
		return BrokerManager.getBroker(Category.class).save(db, category);
	}
}
