package com.blackbooks.fragments;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.adapters.BookItem;
import com.blackbooks.adapters.BooksByCategoryAdapter;
import com.blackbooks.adapters.CategoryItem;
import com.blackbooks.adapters.ListItem;
import com.blackbooks.adapters.ListItemType;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.fragments.CategoryDeleteFragment.CategoryDeleteListener;
import com.blackbooks.fragments.CategoryEditFragment.CategoryEditListener;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.nonpersistent.CategoryInfo;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.services.CategoryServices;

/**
 * Implements {@link AbstractBookListFragment}. A fragment that lists books by
 * categories.
 */
public class BookListByCategoryFragment extends AbstractBookListFragment implements CategoryEditListener, CategoryDeleteListener {

	private static final String CATEGORY_DELETE_FRAGMENT_TAG = "CATEGORY_DELETE_FRAGMENT_TAG";
	private static final String CATEGORY_EDIT_FRAGMENT_TAG = "CATEGORY_EDIT_FRAGMENT_TAG";

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ListView listView = getListView();
		registerForContextMenu(listView);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		ListItem listItem = (ListItem) getListView().getAdapter().getItem(info.position);
		if (listItem.getListItemType() == ListItemType.Header) {
			CategoryItem categoryItem = (CategoryItem) listItem;
			Category category = categoryItem.getCategory();
			if (category.id != null) {
				MenuInflater inflater = getActivity().getMenuInflater();
				inflater.inflate(R.menu.category_edit, menu);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		boolean result = true;

		CategoryItem categoryItem;
		Category category;

		switch (item.getItemId()) {
		case R.id.categoryEdit_actionEdit:
			categoryItem = (CategoryItem) getListAdapter().getItem(info.position);
			category = categoryItem.getCategory();
			CategoryEditFragment editfragment = CategoryEditFragment.newInstance(category, false);
			editfragment.setTargetFragment(this, 0);
			editfragment.show(getFragmentManager(), CATEGORY_EDIT_FRAGMENT_TAG);
			break;

		case R.id.categoryEdit_actionDelete:
			categoryItem = (CategoryItem) getListAdapter().getItem(info.position);
			category = categoryItem.getCategory();
			CategoryDeleteFragment deleteFragment = CategoryDeleteFragment.newInstance(category);
			deleteFragment.setTargetFragment(this, 0);
			deleteFragment.show(getFragmentManager(), CATEGORY_DELETE_FRAGMENT_TAG);
			break;

		default:
			result = super.onContextItemSelected(item);
		}
		return result;
	}

	@Override
	public String onCategoryEdited(Category category, String newName) {
		String errorMessage = null;
		String oldName = category.name;
		newName = newName.trim();
		if (!oldName.equals(newName)) {
			category.name = newName;

			SQLiteHelper dbHelper = new SQLiteHelper(this.getActivity());
			SQLiteDatabase db = dbHelper.getWritableDatabase();

			Category categoryDb = CategoryServices.getCategoryByCriteria(db, category);

			if (categoryDb == null) {
				CategoryServices.saveCategory(db, category);

				String message = getString(R.string.message_category_modified);
				message = String.format(message, oldName, newName);
				Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

			} else {
				errorMessage = "There is already a category with this name";
			}

			db.close();

			super.loadData();
		}
		return errorMessage;
	}

	@Override
	public void onCategoryDeleted(Category category) {
		SQLiteHelper dbHelper = new SQLiteHelper(getActivity());
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		CategoryServices.deleteCategory(db, category.id);
		db.close();
		String text = getString(R.string.message_category_deleted);
		text = String.format(text, category.name);
		Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();

		super.loadData();
	}

	@Override
	protected ArrayAdapter<ListItem> getBookListAdapter() {
		return new BooksByCategoryAdapter(getActivity());
	}

	@Override
	protected List<ListItem> loadBookList() {

		SQLiteHelper dbHelper = new SQLiteHelper(this.getActivity());
		SQLiteDatabase db = null;
		List<CategoryInfo> categoryList;
		try {
			db = dbHelper.getReadableDatabase();
			categoryList = CategoryServices.getCategoryInfoList(db);
		} finally {
			if (db != null) {
				db.close();
			}
		}

		List<ListItem> listItems = new ArrayList<ListItem>();
		for (CategoryInfo categoryInfo : categoryList) {
			if (categoryInfo.id == null) {
				categoryInfo.name = getString(R.string.label_unspecified_category);
			}
			CategoryItem categoryItem = new CategoryItem(categoryInfo);
			listItems.add(categoryItem);
			for (BookInfo book : categoryInfo.books) {
				BookItem bookItem = new BookItem(book);
				listItems.add(bookItem);
			}
		}

		return listItems;
	}
}
