package com.blackbooks.fragments.booklist;


import android.content.Context;
import android.os.Bundle;

import com.blackbooks.R;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.BookLocation;
import com.blackbooks.services.BookLocationService;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

/**
 * A fragment to display the books of a given book location.
 */
public final class BookListByBookLocationFragment extends AbstractBookListFragment {

    private static final String ARG_BOOK_LOCATION_ID = "ARG_BOOK_LOCATION_ID";

    private long mBookLocationId;
    private BookLocation mBookLocation;

    @Inject
    BookLocationService bookLocationService;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    /**
     * Return a new instance of BookListByBookLocationFragment, initialized to display the books of an author.
     *
     * @param bookLocationId Id of the book location.
     * @return BookListByBookLocationFragment.
     */
    public static BookListByBookLocationFragment newInstance(long bookLocationId) {
        BookListByBookLocationFragment fragment = new BookListByBookLocationFragment();

        Bundle args = new Bundle();
        args.putLong(ARG_BOOK_LOCATION_ID, bookLocationId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mBookLocationId = args.getLong(ARG_BOOK_LOCATION_ID);

        mBookLocation = bookLocationService.getBookLocation(mBookLocationId);
    }

    @Override
    protected int getBookCount() {
        return bookService.getBookCountByBookLocation(mBookLocationId);
    }

    @Override
    protected List<BookInfo> loadBookInfoList(int limit, int offset) {
        return bookService.getBookInfoListByBookLocation(mBookLocationId, limit, offset);
    }

    @Override
    protected String getTitle() {
        return getString(R.string.title_activity_books_by_book_location, mBookLocation.name);
    }
}
