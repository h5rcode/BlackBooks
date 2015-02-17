package com.blackbooks.fragments;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.blackbooks.R;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.BookLocation;
import com.blackbooks.services.BookLocationServices;
import com.blackbooks.services.BookServices;

import java.util.List;

/**
 * A fragment to display the books of a given book location.
 */
public final class BookListByBookLocationFragment2 extends AbstractBookListFragment2 {

    private static final String ARG_BOOK_LOCATION_ID = "ARG_BOOK_LOCATION_ID";

    private long mBookLocationId;
    private BookLocation mBookLocation;

    /**
     * Return a new instance of BookListByBookLocationFragment2, initialized to display the books of an author.
     *
     * @param bookLocationId Id of the book location.
     * @return BookListByBookLocationFragment2.
     */
    public static BookListByBookLocationFragment2 newInstance(long bookLocationId) {
        BookListByBookLocationFragment2 fragment = new BookListByBookLocationFragment2();

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

        SQLiteDatabase db = SQLiteHelper.getInstance().getReadableDatabase();
        mBookLocation = BookLocationServices.getBookLocation(db, mBookLocationId);
    }

    @Override
    protected int getBookCount(SQLiteDatabase db) {
        return BookServices.getBookCountByBookLocation(db, mBookLocationId);
    }

    @Override
    protected List<BookInfo> loadBookInfoList(SQLiteDatabase db, int limit, int offset) {
        return BookServices.getBookInfoListByBookLocation(db, mBookLocationId, limit, offset);
    }

    @Override
    protected String getTitle() {
        return getString(R.string.title_activity_books_by_book_location, mBookLocation.name);
    }
}
