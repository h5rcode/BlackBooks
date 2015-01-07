package com.blackbooks.fragments;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;

import com.blackbooks.R;
import com.blackbooks.adapters.BookItem;
import com.blackbooks.adapters.BooksByLanguageAdapter;
import com.blackbooks.adapters.BooksByLanguageAdapter.LanguageItem;
import com.blackbooks.adapters.ListItem;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.nonpersistent.LanguageInfo;
import com.blackbooks.services.LanguageServices;

/**
 * Implements {@link AbstractBookListFragment}. A fragment that lists books by
 * language.
 */
public class BookListByLanguageFragment extends AbstractBookListFragment {

	private String mFooterText;
	
	@Override
	protected String getActionBarSubtitle() {
		return getString(R.string.subtitle_fragment_books_by_language);
	}

	@Override
	protected ArrayAdapter<ListItem> getBookListAdapter() {
		return new BooksByLanguageAdapter(this.getActivity());
	}

	@Override
	protected String getFooterText() {
		return mFooterText;
	}

	@Override
	protected List<ListItem> loadBookList() {
		SQLiteHelper dbHelper = new SQLiteHelper(this.getActivity());
		SQLiteDatabase db = null;
		List<LanguageInfo> languageList;
		try {
			db = dbHelper.getReadableDatabase();
			languageList = LanguageServices.getLanguageInfoList(db);
		} finally {
			if (db != null) {
				db.close();
			}
		}

		int languageCount = 0;
		List<ListItem> listItems = new ArrayList<ListItem>();
		for (LanguageInfo language : languageList) {
			if (language.languageCode == null) {
				language.displayName = getString(R.string.label_unspecified_language);
			} else {
				languageCount++;
			}

			LanguageItem languageItem = new LanguageItem(language);
			listItems.add(languageItem);

			for (BookInfo book : language.books) {
				BookItem bookItem = new BookItem(book);
				listItems.add(bookItem);
			}
		}
		
		mFooterText = getResources().getQuantityString(R.plurals.footer_fragment_books_by_language, languageCount, languageCount);

		return listItems;
	}
}
