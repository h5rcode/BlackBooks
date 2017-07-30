package com.blackbooks.fragments.booklist;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.blackbooks.R;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.BookServices;

import java.util.List;


/**
 * A fragment to display the books loaned to a given person.
 */
public final class BookListByLoanedFragment2 extends AbstractBookListFragment2 {

    private static final String ARG_LOANED_TO = "ARG_LOANED_TO";

    private String mLoanedTo;

    /**
     * Return a new instance of BookListByLoanedFragment2, initialized to display the books loaned to a person.
     *
     * @param loanedTo Person the book is loaned to.
     * @return BookListByLoanedFragment2.
     */
    public static BookListByLoanedFragment2 newInstance(String loanedTo) {
        BookListByLoanedFragment2 fragment = new BookListByLoanedFragment2();

        Bundle args = new Bundle();
        args.putString(ARG_LOANED_TO, loanedTo);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mLoanedTo = args.getString(ARG_LOANED_TO);
    }

    @Override
    protected int getBookCount(SQLiteDatabase db) {
        return BookServices.getBookCountByLoanedTo(db, mLoanedTo);
    }

    @Override
    protected List<BookInfo> loadBookInfoList(SQLiteDatabase db, int limit, int offset) {
        return BookServices.getBookInfoListByLoanedTo(db, mLoanedTo, limit, offset);
    }

    @Override
    protected String getTitle() {
        return getString(R.string.title_activity_books_by_loaned, mLoanedTo);
    }
}
