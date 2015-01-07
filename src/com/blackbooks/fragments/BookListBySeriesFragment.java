package com.blackbooks.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;

import com.blackbooks.R;
import com.blackbooks.adapters.BookItem;
import com.blackbooks.adapters.BooksBySeriesAdapter;
import com.blackbooks.adapters.BooksBySeriesAdapter.SeriesItem;
import com.blackbooks.adapters.ListItem;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.nonpersistent.SeriesInfo;
import com.blackbooks.services.SeriesServices;

/**
 * Implements {@link AbstractBookListFragment}. A fragment that lists books by
 * series.
 */
public class BookListBySeriesFragment extends AbstractBookListFragment {

	private String mFooterText;

	@Override
	protected String getActionBarSubtitle() {
		return getString(R.string.action_sort_by_series);
	}

	@Override
	protected String getFooterText() {
		return mFooterText;
	}

	@Override
	protected List<ListItem> loadBookList() {
		SQLiteHelper dbHelper = new SQLiteHelper(this.getActivity());
		SQLiteDatabase db = null;
		List<SeriesInfo> seriesInfoList;
		try {
			db = dbHelper.getReadableDatabase();
			seriesInfoList = SeriesServices.getSeriesInfoList(db);
		} finally {
			if (db != null) {
				db.close();
			}
		}

		int seriesCount = 0;
		int bookCount = 0;
		List<ListItem> listItems = new ArrayList<ListItem>();
		for (SeriesInfo seriesInfo : seriesInfoList) {
			if (seriesInfo.id == null) {
				seriesInfo.name = getString(R.string.label_unspecified_series);
			} else {
				seriesCount++;
			}
			SeriesItem seriesItem = new SeriesItem(seriesInfo);
			listItems.add(seriesItem);

			for (BookInfo book : seriesInfo.books) {
				BookItem bookItem = new BookItem(book);
				listItems.add(bookItem);
				bookCount++;
			}
		}

		Resources res = getResources();
		String series = res.getQuantityString(R.plurals.label_footer_series, seriesCount, seriesCount);
		String books = res.getQuantityString(R.plurals.label_footer_books, bookCount, bookCount);

		mFooterText = getString(R.string.footer_fragment_books_by_series, series, books);

		return listItems;
	}

	@Override
	protected ArrayAdapter<ListItem> getBookListAdapter() {
		return new BooksBySeriesAdapter(getActivity());
	}

}
