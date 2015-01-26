package com.blackbooks.fragments;

import android.content.res.Resources;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Implements {@link AbstractBookListFragment}. A fragment that lists books by
 * language.
 */
public class BookListByLanguageFragment extends AbstractBookListFragment {

    private String mFooterText;

    @Override
    protected String getActionBarSubtitle() {
        return getString(R.string.action_sort_by_language);
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
        int bookCount = 0;
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

                bookCount++;
            }
        }

        Resources res = getResources();
        String languages = res.getQuantityString(R.plurals.label_footer_language, languageCount, languageCount);
        String books = res.getQuantityString(R.plurals.label_footer_books, bookCount, bookCount);

        mFooterText = getString(R.string.footer_fragment_books_by_language, languages, books);

        return listItems;
    }
}
