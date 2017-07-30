package com.blackbooks.fragments.booklist;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.blackbooks.R;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.BookServices;

import java.util.List;

/**
 * A fragment to display the books whose title begins with a given letter.
 */
public final class BookListByFirstLetterFragment2 extends AbstractBookListFragment2 {

    private static final String ARG_FIRST_LETTER = "ARG_FIRST_LETTER";

    private String mFirstLetter;

    /**
     * Return a new instance of BookListByFirstLetterFragment2, initialized to display the books
     * whose title begins with a given letter.
     *
     * @param firstLetter First letter.
     * @return BookListByFirstLetterFragment2.
     */
    public static BookListByFirstLetterFragment2 newInstance(String firstLetter) {
        BookListByFirstLetterFragment2 fragment = new BookListByFirstLetterFragment2();

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
    protected int getBookCount(SQLiteDatabase db) {
        return BookServices.getBookCountByFirstLetter(db, mFirstLetter);
    }

    @Override
    protected List<BookInfo> loadBookInfoList(SQLiteDatabase db, int limit, int offset) {
        return BookServices.getBookInfoListByFirstLetter(db, mFirstLetter, limit, offset);
    }

    @Override
    protected String getTitle() {
        return getString(R.string.title_activity_books_by_first_letter, mFirstLetter);
    }
}
