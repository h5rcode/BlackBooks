package com.blackbooks.fragments;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;

import com.blackbooks.R;
import com.blackbooks.adapters.BookItem;
import com.blackbooks.adapters.BooksByLanguageAdapter;
import com.blackbooks.adapters.LanguageItem;
import com.blackbooks.adapters.ListItem;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.nonpersistent.LanguageInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.services.LanguageServices;

public class BookListByLanguageFragment extends AbstractBookListFragment {

	@Override
	protected ArrayAdapter<ListItem> getBookListAdapter() {
		return new BooksByLanguageAdapter(this.getActivity());
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

		List<ListItem> listItems = new ArrayList<ListItem>();

		for (LanguageInfo language : languageList) {
			if (language.displayName == null) {
				language.displayName = getString(R.string.label_unspecified_language);
			}

			LanguageItem languageItem = new LanguageItem(language.displayName, language.bookList.size());
			listItems.add(languageItem);

			for (BookInfo book : language.bookList) {
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
