package com.blackbooks.activities;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.adapters.AutoCompleteAdapter;
import com.blackbooks.adapters.AutoCompleteAdapter.AutoCompleteSearcher;
import com.blackbooks.adapters.EditableArrayAdapter;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.services.CategoryServices;

public class BookCategoriesEdit extends Activity {

	public static final String EXTRA_CATEGORY_LIST = "EXTRA_CATEGORY_LIST";

	public static final String EXTRA_BOOK_TITLE = "EXTRA_BOOK_TITLE";

	private static final String BOOK_TITLE = "BOOK_TITLE";

	private static final String CATEGORY_LIST = "CATEGORY_LIST";

	private String mBookTitle;
	private ArrayList<Category> mCategoryList;
	private LinkedHashMap<String, Category> mCategoryMap;
	private AutoCompleteAdapter<Category> mAutoCompleteAdapter;
	private EditableArrayAdapter<Category> mEditableArrayAdapter;

	private TextView mTextInfo;
	private AutoCompleteTextView mTextCategory;
	private ListView mListObjects;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.book_categories_edit, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		boolean result;

		switch (item.getItemId()) {
		case R.id.bookCategoriesEdit_actionSave:
			result = true;
			addCategoryList();
			break;

		case android.R.id.home:
			result = true;
			finish();
			break;

		default:
			result = super.onMenuItemSelected(featureId, item);
			break;
		}
		return result;
	}

	/**
	 * Add the category to the list.
	 * 
	 * @param view
	 *            View.
	 */
	public void addCategory(View view) {
		String categoryName = mTextCategory.getText().toString().trim();

		if (categoryName.length() == 0) {
			mTextCategory.setError(getString(R.string.message_category_missing));
		} else {
			if (mCategoryMap.containsKey(categoryName)) {
				String message = getString(R.string.message_category_already_present);
				message = String.format(message, categoryName);
				mTextCategory.setError(message);
			} else {
				Category object = getCategory(categoryName);

				mCategoryMap.put(categoryName, object);
				mEditableArrayAdapter.add(object);

				String message = getString(R.string.message_category_added);
				message = String.format(message, categoryName);
				Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

				mTextCategory.setText(null);
				mTextCategory.setError(null);
			}
		}
	}

	/**
	 * Remove a category from the list.
	 * 
	 * @param view
	 *            View.
	 */
	public void removeCategory(View view) {
		Category object = (Category) view.getTag();
		mCategoryMap.remove(object.name);
		mEditableArrayAdapter.remove(object);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_categories_edit);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState != null) {
			mBookTitle = savedInstanceState.getString(BOOK_TITLE);
			mCategoryList = (ArrayList<Category>) savedInstanceState.getSerializable(CATEGORY_LIST);
		} else {
			Intent intent = getIntent();
			mBookTitle = intent.getStringExtra(EXTRA_BOOK_TITLE);
			mCategoryList = (ArrayList<Category>) intent.getSerializableExtra(EXTRA_CATEGORY_LIST);
		}
		if (mCategoryList == null) {
			mCategoryList = new ArrayList<Category>();
		}

		mCategoryMap = new LinkedHashMap<String, Category>();
		for (Category object : mCategoryList) {
			mCategoryMap.put(object.name, object);
		}

		mAutoCompleteAdapter = new AutoCompleteAdapter<Category>(this, android.R.layout.simple_list_item_1, new AutoCompleteSearcher<Category>() {

			public ArrayList<Category> search(CharSequence constraint) {
				SQLiteHelper mDbHelper = new SQLiteHelper(BookCategoriesEdit.this);
				SQLiteDatabase db = mDbHelper.getReadableDatabase();
				ArrayList<Category> categoryList = CategoryServices.getCategoryListByText(db, constraint.toString());
				db.close();
				return categoryList;
			}

			public String getDisplayLabel(Category item) {
				return item.name;
			}
		});
		mEditableArrayAdapter = new EditableArrayAdapter<Category>(this, R.id.bookCategoriesEdit_categoryList, R.layout.list_categories_item_category,
				R.id.item_category_name, R.id.item_category_button_remove, mCategoryList) {

			protected String getDisplayLabel(Category object) {
				return object.name;
			}
		};

		mTextInfo = (TextView) findViewById(R.id.bookCategoriesEdit_textInfo);
		mTextCategory = (AutoCompleteTextView) findViewById(R.id.bookCategoriesEdit_textCategory);
		mListObjects = (ListView) findViewById(R.id.bookCategoriesEdit_categoryList);

		if (mBookTitle == null || mBookTitle.trim().equals("")) {
			mTextInfo.setText(getString(R.string.text_info_edit_categories_untitled_book));
		} else {
			String message = getString(R.string.text_info_edit_categories);
			message = String.format(message, mBookTitle.trim());
			mTextInfo.setText(message);
		}
		mTextCategory.setAdapter(mAutoCompleteAdapter);
		mListObjects.setAdapter(mEditableArrayAdapter);

		TextView emptyText = (TextView) findViewById(android.R.id.empty);
		mListObjects.setEmptyView(emptyText);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(BOOK_TITLE, mBookTitle);
		outState.putSerializable(CATEGORY_LIST, mCategoryList);
	}

	private Category getCategory(String categoryName) {
		Category category = new Category();
		category.name = categoryName;

		SQLiteHelper dbHelper = new SQLiteHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Category categoryDb = CategoryServices.getCategoryByCriteria(db, category);
		db.close();
		if (categoryDb != null) {
			category = categoryDb;
		}
		return category;
	}

	/**
	 * Finish the activity and add the list of authors to the result of the
	 * activity.
	 */
	private void addCategoryList() {
		Intent intent = new Intent();
		ArrayList<Category> objectList = new ArrayList<Category>(mCategoryMap.values());
		intent.putExtra(EXTRA_CATEGORY_LIST, objectList);
		setResult(RESULT_OK, intent);
		finish();
	}
}
