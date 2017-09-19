package com.blackbooks.fragments.booklist;

import android.os.Bundle;

import com.blackbooks.R;
import com.blackbooks.model.nonpersistent.BookInfo;

import java.util.List;


/**
 * A fragment to display the books loaned to a given person.
 */
public final class BookListByLoanedFragment extends AbstractBookListFragment {

    private static final String ARG_LOANED_TO = "ARG_LOANED_TO";

    private String mLoanedTo;

    /**
     * Return a new instance of BookListByLoanedFragment, initialized to display the books loaned to a person.
     *
     * @param loanedTo Person the book is loaned to.
     * @return BookListByLoanedFragment.
     */
    public static BookListByLoanedFragment newInstance(String loanedTo) {
        BookListByLoanedFragment fragment = new BookListByLoanedFragment();

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
    protected int getBookCount() {
        return bookService.getBookCountByLoanedTo(mLoanedTo);
    }

    @Override
    protected List<BookInfo> loadBookInfoList(int limit, int offset) {
        return bookService.getBookInfoListByLoanedTo(mLoanedTo, limit, offset);
    }

    @Override
    protected String getTitle() {
        return getString(R.string.title_activity_books_by_loaned, mLoanedTo);
    }
}
