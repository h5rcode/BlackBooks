package com.blackbooks.fragments.booklist;

import android.os.Bundle;

import com.blackbooks.R;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.utils.LanguageUtils;

import java.util.List;

/**
 * A fragment to display the books of a given language.
 */
public final class BookListByLanguageFragment extends AbstractBookListFragment {

    private static final String ARG_LANGUAGE_CODE = "ARG_LANGUAGE_CODE";

    private String mLanguageCode;

    /**
     * Return a new instance of BookListByLanguageFragment, initialized to display the books of a language.
     *
     * @param languageCode Language code.
     * @return BookListByCategoryFragment.
     */
    public static BookListByLanguageFragment newInstance(String languageCode) {
        BookListByLanguageFragment fragment = new BookListByLanguageFragment();

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
    protected int getBookCount() {
        return bookService.getBookCountByLanguage(mLanguageCode);
    }

    @Override
    protected List<BookInfo> loadBookInfoList(int limit, int offset) {
        return bookService.getBookInfoListByLanguage(mLanguageCode, limit, offset);
    }

    @Override
    protected String getTitle() {
        return getString(R.string.title_activity_books_by_language, LanguageUtils.getDisplayLanguage(mLanguageCode));
    }
}
