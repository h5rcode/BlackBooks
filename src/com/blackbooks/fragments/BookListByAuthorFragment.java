package com.blackbooks.fragments;

import java.util.ArrayList;

import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;

import com.blackbooks.R;
import com.blackbooks.adapters.AuthorItem;
import com.blackbooks.adapters.BookItem;
import com.blackbooks.adapters.BooksByAuthorAdapter;
import com.blackbooks.adapters.ListItem;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.AuthorInfo;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.services.AuthorServices;

/**
 * Implements {@link AbstractBookListFragment}. A fragment that lists books by
 * author.
 */
public class BookListByAuthorFragment extends AbstractBookListFragment {

	@Override
	protected ArrayAdapter<ListItem> getBookListAdapter() {
		return new BooksByAuthorAdapter(this.getActivity());
	}
	
	@Override
	protected ArrayList<ListItem> loadBookList() {
		SQLiteHelper dbHelper = new SQLiteHelper(this.getActivity());
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		ArrayList<AuthorInfo> authorInfoList = AuthorServices.getAuthorInfoList(db);
		db.close();

		ArrayList<ListItem> listItems = new ArrayList<ListItem>();

		for (AuthorInfo authorInfo : authorInfoList) {
			if (authorInfo.id == null) {
				authorInfo.name = getString(R.string.label_unspecified_author);
			}
			AuthorItem authorItem = new AuthorItem(authorInfo.id, authorInfo.name, authorInfo.books.size());
			listItems.add(authorItem);

			for (Book book : authorInfo.books) {
				BookItem bookEntry = new BookItem(book.id, book.title, book.smallThumbnail);
				listItems.add(bookEntry);
			}
		}
		return listItems;
	}
}
