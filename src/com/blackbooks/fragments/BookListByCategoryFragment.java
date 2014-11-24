package com.blackbooks.fragments;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;

import com.blackbooks.R;
import com.blackbooks.adapters.BookItem;
import com.blackbooks.adapters.BooksByCategoryAdapter;
import com.blackbooks.adapters.CategoryItem;
import com.blackbooks.adapters.ListItem;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.nonpersistent.CategoryInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.services.CategoryServices;

/**
 * Implements {@link AbstractBookListFragment}. A fragment that lists books by
 * categories.
 */
public class BookListByCategoryFragment extends AbstractBookListFragment {

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
			CategoryItem categoryItem = new CategoryItem(categoryInfo.name, categoryInfo.books.size());
			listItems.add(categoryItem);
			for (BookInfo book : categoryInfo.books) {
				BookItem bookItem = new BookItem(book.id, book.title, book.smallThumbnail);
				for (Author author : book.authors) {
					bookItem.getAuthors().add(author.name);
				}
				listItems.add(bookItem);
			}
		}

		return listItems;
	}
}
