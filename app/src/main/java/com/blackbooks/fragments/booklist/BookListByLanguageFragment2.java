package com.blackbooks.fragments.booklist;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.blackbooks.R;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.BookServices;
import com.blackbooks.utils.LanguageUtils;

import java.util.List;

/**
 * A fragment to display the books of a given language.
 */
public final class BookListByLanguageFragment2 extends AbstractBookListFragment2 {

    private static final String ARG_LANGUAGE_CODE = "ARG_LANGUAGE_CODE";

    private String mLanguageCode;

    /**
     * Return a new instance of BookListByLanguageFragment2, initialized to display the books of a language.
     *
     * @param languageCode Language code.
     * @return BookListByCategoryFragment2.
     */
    public static BookListByLanguageFragment2 newInstance(String languageCode) {
        BookListByLanguageFragment2 fragment = new BookListByLanguageFragment2();

        Bundle args = new Bundle();
        args.putString(ARG_LANGUAGE_CODE, languageCode);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mLanguageCode = args.getString(ARG_LANGUAGE_CODE);
    }

    @Override
    protected int getBookCount(SQLiteDatabase db) {
        return BookServices.getBookCountByLanguage(db, mLanguageCode);
    }

    @Override
    protected List<BookInfo> loadBookInfoList(SQLiteDatabase db, int limit, int offset) {
        return BookServices.getBookInfoListByLanguage(db, mLanguageCode, limit, offset);
    }

    @Override
    protected String getTitle() {
        return getString(R.string.title_activity_books_by_language, LanguageUtils.getDisplayLanguage(mLanguageCode));
    }
}
