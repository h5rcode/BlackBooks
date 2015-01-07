package com.blackbooks.fragments;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;

import com.blackbooks.R;
import com.blackbooks.adapters.BookItem;
import com.blackbooks.adapters.BooksByAuthorAdapter;
import com.blackbooks.adapters.BooksByAuthorAdapter.AuthorItem;
import com.blackbooks.adapters.BooksByAuthorAdapter.SeriesItem;
import com.blackbooks.adapters.ListItem;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.AuthorInfo;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.nonpersistent.SeriesInfo;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.services.AuthorServices;

/**
 * Implements {@link AbstractBookListFragment}. A fragment that lists books by
 * author.
 */
public class BookListByAuthorFragment extends AbstractBookListFragment {
	
	private String mFooterText;

	@Override
	protected String getActionBarSubtitle() {
		return getString(R.string.subtitle_fragment_books_by_author);
	}

	@Override
	protected ArrayAdapter<ListItem> getBookListAdapter() {
		return new BooksByAuthorAdapter(this.getActivity());
	}

	@Override
	protected String getFooterText() {
		return mFooterText;
	}

	@Override
	protected List<ListItem> loadBookList() {
		SQLiteHelper dbHelper = new SQLiteHelper(this.getActivity());
		SQLiteDatabase db = null;
		List<AuthorInfo> authorInfoList;
		try {
			db = dbHelper.getReadableDatabase();
			authorInfoList = AuthorServices.getAuthorInfoList(db);
		} finally {
			if (db != null) {
				db.close();
			}
		}

		List<ListItem> listItems = new ArrayList<ListItem>();

		int authorCount = 0;
		for (AuthorInfo authorInfo : authorInfoList) {
			if (authorInfo.id == null) {
				authorInfo.name = getString(R.string.label_unspecified_author);
			} else ;{
				authorCount++;
			}
			AuthorItem authorItem = new AuthorItem(authorInfo);
			listItems.add(authorItem);

			for (SeriesInfo series : authorInfo.series) {
				SeriesItem seriesItem = new SeriesItem(series);
				listItems.add(seriesItem);

				for (Book book : series.books) {
					BookInfo bookInfo = new BookInfo(book);
					BookItem bookEntry = new BookItem(bookInfo);
					listItems.add(bookEntry);
				}
			}
		}
		
		
		mFooterText = getResources().getQuantityString(R.plurals.footer_fragment_books_by_author, authorCount, authorCount);
		
		return listItems;
	}
}
