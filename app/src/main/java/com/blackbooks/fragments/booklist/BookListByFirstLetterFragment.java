package com.blackbooks.fragments.booklist;


import android.os.Bundle;

import com.blackbooks.R;
import com.blackbooks.model.nonpersistent.BookInfo;

import java.util.List;

/**
 * A fragment to display the books whose title begins with a given letter.
 */
public final class BookListByFirstLetterFragment extends AbstractBookListFragment {

    private static final String ARG_FIRST_LETTER = "ARG_FIRST_LETTER";

    private String mFirstLetter;

    /**
     * Return a new instance of BookListByFirstLetterFragment, initialized to display the books
     * whose title begins with a given letter.
     *
     * @param firstLetter First letter.
     * @return BookListByFirstLetterFragment.
     */
    public static BookListByFirstLetterFragment newInstance(String firstLetter) {
        BookListByFirstLetterFragment fragment = new BookListByFirstLetterFragment();

        Bundle args = new Bundle();
        args.putString(ARG_FIRST_LETTER, firstLetter);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mFirstLetter = args.getString(ARG_FIRST_LETTER);
    }

    @Override
    protected int getBookCount() {
        return bookService.getBookCountByFirstLetter(mFirstLetter);
    }

    @Override
    protected List<BookInfo> loadBookInfoList(int limit, int offset) {
        return bookService.getBookInfoListByFirstLetter(mFirstLetter, limit, offset);
    }

    @Override
    protected String getTitle() {
        return getString(R.string.title_activity_books_by_first_letter, mFirstLetter);
    }
}
