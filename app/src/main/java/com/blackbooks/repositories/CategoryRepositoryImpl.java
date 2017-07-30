package com.blackbooks.repositories;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.model.persistent.BookCategory;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.sql.BrokerManager;

import java.util.List;

public class CategoryRepositoryImpl implements CategoryRepository {
    private final SQLiteDatabase db;

    public CategoryRepositoryImpl(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public void deleteCategory(Long categoryId) {
        BrokerManager.getBroker(Category.class).delete(db, categoryId);
    }

    public void deleteCategoriesWithoutBooks() {
        String sql = "DELETE FROM " + Category.NAME + " WHERE " + Category.Cols.CAT_ID + " IN (SELECT cat."
                + Category.Cols.CAT_ID + " FROM " + Category.NAME + " cat LEFT JOIN " + BookCategory.NAME + " bca ON bca."
                + BookCategory.Cols.CAT_ID + " = cat." + Category.Cols.CAT_ID + " WHERE bca." + BookCategory.Cols.BCA_ID
                + " IS NULL)";
        db.execSQL(sql);
    }

    @Override
    public Category getCategory(long categoryId) {
        return BrokerManager.getBroker(Category.class).get(db, categoryId);
    }

    @Override
    public long saveCategory(Category category) {
        return BrokerManager.getBroker(Category.class).save(db, category);
    }

    @Override
    public Category getCategoryByCriteria(Category criteria) {
        return BrokerManager.getBroker(Category.class).getByCriteria(db, criteria);
    }

    @Override
    public List<Category> getCategoryListByText(String text) {
        String sql = "SELECT * FROM " + Category.NAME + " WHERE LOWER(" + Category.Cols.CAT_NAME
                + ") LIKE '%' || LOWER(?) || '%' ORDER BY " + Category.Cols.CAT_NAME;
        String[] selectionArgs = {text};
        return BrokerManager.getBroker(Category.class).rawSelect(db, sql, selectionArgs);
    }

    @Override
    public void updateCategory(long categoryId, String newName) {
        ContentValues values = new ContentValues();
        values.put(Category.Cols.CAT_NAME, newName);
        String whereClause = Category.Cols.CAT_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(categoryId)};
        db.updateWithOnConflict(Category.NAME, values, whereClause, whereArgs, SQLiteDatabase.CONFLICT_ROLLBACK);
    }

    @Override
    public int getCategoryCount() {
        String sql = "SELECT COUNT(*) FROM " + Category.NAME;
        return queryInt(db, sql);
    }

    private int queryInt(SQLiteDatabase db, String sql) {
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToNext();
        int result = cursor.getInt(0);
        cursor.close();
        return result;
    }
}
