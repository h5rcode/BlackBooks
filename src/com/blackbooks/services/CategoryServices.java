package com.blackbooks.services;

import java.util.ArrayList;

import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.model.persistent.Category;
import com.blackbooks.sql.BrokerManager;

/**
 * Services related to the Category class.
 */
public class CategoryServices {

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

	/**
	 * Get the list of categories whose name contains a given text.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param text
	 *            Text.
	 * @return List of Category.
	 */
	public static ArrayList<Category> getCategoryListByText(SQLiteDatabase db, String text) {
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
