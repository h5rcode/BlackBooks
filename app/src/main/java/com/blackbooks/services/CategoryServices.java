package com.blackbooks.services;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.model.persistent.BookCategory;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.sql.BrokerManager;

import java.util.List;

/**
 * Services related to the Category class.
 */
public final class CategoryServices {

    /**
     * Delete a category.
     *
     * @param db         SQLiteDatabase.
     * @param categoryId Id of the category.
     */
    public static void deleteCategory(SQLiteDatabase db, long categoryId) {
        BrokerManager.getBroker(Category.class).delete(db, categoryId);
    }

    /**
     * Delete the categories that are not referred by any books in the database.
     *
     * @param db SQLiteDatabase.
     */
    public static void deleteCategoriesWithoutBooks(SQLiteDatabase db) {
        String sql = "DELETE FROM " + Category.NAME + " WHERE " + Category.Cols.CAT_ID + " IN (SELECT cat."
                + Category.Cols.CAT_ID + " FROM " + Category.NAME + " cat LEFT JOIN " + BookCategory.NAME + " bca ON bca."
                + BookCategory.Cols.CAT_ID + " = cat." + Category.Cols.CAT_ID + " WHERE bca." + BookCategory.Cols.BCA_ID
                + " IS NULL)";
        db.execSQL(sql);
    }

    /**
     * Get a category from the database.
     *
     * @param db    SQLiteDatabase.
     * @param catId Id of the category.
     * @return Category.
     */
    public static Category getCategory(SQLiteDatabase db, long catId) {
        return BrokerManager.getBroker(Category.class).get(db, catId);
    }

    /**
     * Get the one row matching a criteria. If no rows or more that one rows
     * match the criteria, the method returns null.
     *
     * @param db       SQLiteDatabase.
     * @param criteria The search criteria.
     * @return Category.
     */
    public static Category getCategoryByCriteria(SQLiteDatabase db, Category criteria) {
        return BrokerManager.getBroker(Category.class).getByCriteria(db, criteria);
    }

    /**
     * Get the list of categories whose name contains a given text.
     *
     * @param db   SQLiteDatabase.
     * @param text Text.
     * @return List of Category.
     */
    public static List<Category> getCategoryListByText(SQLiteDatabase db, String text) {
        String sql = "SELECT * FROM " + Category.NAME + " WHERE LOWER(" + Category.Cols.CAT_NAME
                + ") LIKE '%' || LOWER(?) || '%' ORDER BY " + Category.Cols.CAT_NAME;
        String[] selectionArgs = {text};
        return BrokerManager.getBroker(Category.class).rawSelect(db, sql, selectionArgs);
    }

    /**
     * Save a category.
     *
     * @param db       SQLiteDatabase.
     * @param category Category.
     * @return Id of the saved category.
     */
    public static long saveCategory(SQLiteDatabase db, Category category) {
        return BrokerManager.getBroker(Category.class).save(db, category);
    }

    /**
     * Update a category.
     *
     * @param db         SQLiteDatabase.
     * @param categoryId Id of the category.
     * @param newName    New name.
     */
    public static void updateCategory(SQLiteDatabase db, long categoryId, String newName) {
        ContentValues values = new ContentValues();
        values.put(Category.Cols.CAT_NAME, newName);
        String whereClause = Category.Cols.CAT_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(categoryId)};
        db.updateWithOnConflict(Category.NAME, values, whereClause, whereArgs, SQLiteDatabase.CONFLICT_ROLLBACK);
    }
}
