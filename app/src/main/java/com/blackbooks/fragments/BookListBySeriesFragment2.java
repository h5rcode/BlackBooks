package com.blackbooks.fragments;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.BookServices;

import java.util.List;

/**
 * A fragment to display the books of a given series.
 */
public final class BookListBySeriesFragment2 extends AbstractBookListFragment2 {

    private static final String ARG_SERIES_ID = "ARG_SERIES_ID";

    private long mSeriesId;

    /**
     * Return a new instance of BookListBySeriesFragment2, initialized to display the books of a series.
     *
     * @param seriesId Series id.
     * @return BookListBySeriesFragment2.
     */
    public static BookListBySeriesFragment2 newInstance(long seriesId) {
        BookListBySeriesFragment2 fragment = new BookListBySeriesFragment2();

        Bundle args = new Bundle();
        args.putLong(ARG_SERIES_ID, seriesId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mSeriesId = args.getLong(ARG_SERIES_ID);
    }

    @Override
    protected int getBookCount(SQLiteDatabase db) {
        return BookServices.getBookCountBySeries(db, mSeriesId);
    }

    @Override
    protected List<BookInfo> loadBookInfoList(SQLiteDatabase db, int limit, int offset) {
        return BookServices.getBookInfoListBySeries(db, mSeriesId, limit, offset);
    }
}
