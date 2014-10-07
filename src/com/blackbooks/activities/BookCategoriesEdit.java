package com.blackbooks.activities;

import java.util.ArrayList;

import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.R;
import com.blackbooks.adapters.AutoCompleteAdapter;
import com.blackbooks.adapters.AutoCompleteAdapter.AutoCompleteSearcher;
import com.blackbooks.adapters.EditableArrayAdapter;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.services.CategoryServices;

public class BookCategoriesEdit extends ListEdit<Category> {

	public static final String EXTRA_CATEGORY_LIST = "EXTRA_CATEGORY_LIST";

	@Override
	protected int getActionIdSave() {
		return R.id.bookCategoriesEdit_actionSave;
	}

	@Override
	protected int getListViewId() {
		return R.id.bookCategoriesEdit_categoryList;
	}

	@Override
	protected int getAutoCompleteTextViewId() {
		return R.id.bookCategoriesEdit_textCategory;
	}

	@Override
	protected int getTextViewIdInfo() {
		return R.id.bookCategoriesEdit_textInfo;
	}

	@Override
	protected int getLayoutIdActivity() {
		return R.layout.activity_book_categories_edit;
	}

	@Override
	protected int getStringIdObjectAdded() {
		return R.string.message_category_added;
	}

	@Override
	protected int getStringIdObjectAlreadyPresent() {
		return R.string.message_category_already_present;
	}

	@Override
	protected int getStringIdObjectMissing() {
		return R.string.message_category_missing;
	}
	
	@Override
	protected int getStringIdEditBook() {
		return R.string.text_info_edit_categories;
	}
	
	@Override
	protected int getStringIdEditUntitledBook() {
		return R.string.text_info_edit_categories_untitled_book;
	}

	@Override
	protected Category getObject(String objectKey) {
		Category category = new Category();
		category.name = objectKey;

		SQLiteHelper dbHelper = new SQLiteHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Category categoryDb = CategoryServices.getCategoryByCriteria(db, category);
		db.close();
		if (categoryDb != null) {
			category = categoryDb;
		}
		return category;
	}

	@Override
	protected String getObjectKey(Category object) {
		return object.name;
	}

	@Override
	protected AutoCompleteAdapter<Category> getAutoCompleteAdapter() {
		return new AutoCompleteAdapter<Category>(this, android.R.layout.simple_list_item_1, new AutoCompleteSearcher<Category>() {

			@Override
			public ArrayList<Category> search(CharSequence constraint) {
				SQLiteHelper mDbHelper = new SQLiteHelper(BookCategoriesEdit.this);
				SQLiteDatabase db = mDbHelper.getReadableDatabase();
				ArrayList<Category> categoryList = CategoryServices.getCategoryListByText(db, constraint.toString());
				db.close();
				return categoryList;
			}

			@Override
			public String getDisplayLabel(Category item) {
				return item.name;
			}
		});
	}

	@Override
	protected EditableArrayAdapter<Category> getEditableArrayAdapter() {
		return new EditableArrayAdapter<Category>(this, R.id.bookCategoriesEdit_categoryList, R.layout.list_categories_item_category, R.id.item_category_name,
				R.id.item_category_button_remove, mObjectList) {

			@Override
			protected String getDisplayLabel(Category object) {
				return object.name;
			}
		};
	}

	@Override
	protected String getExtraObjectListName() {
		return EXTRA_CATEGORY_LIST;
	}

	@Override
	protected int getMenuId() {
		return R.menu.book_categories_edit;
	}

	@Override
	protected String getSavedInstanceObjectListName() {
		return "CATEGORY_LIST";
	}
}
