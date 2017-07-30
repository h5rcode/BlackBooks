package com.blackbooks.fragments.booklist;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Series;
import com.blackbooks.services.BookServices;
import com.blackbooks.services.SeriesServices;

import java.util.List;

/**
 * A fragment to display the books of a given series.
 */
public final class BookListBySeriesFragment2 extends AbstractBookListFragment2 {

    private static final String ARG_SERIES_ID = "ARG_SERIES_ID";

    private long mSeriesId;
    private Series mSeries;

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

        SQLiteDatabase db = SQLiteHelper.getInstance().getReadableDatabase();
        mSeries = SeriesServices.getSeries(db, mSeriesId);
    }

    @Override
    protected int getBookCount(SQLiteDatabase db) {
        return BookServices.getBookCountBySeries(db, mSeriesId);
    }

    @Override
    protected List<BookInfo> loadBookInfoList(SQLiteDatabase db, int limit, int offset) {
        return BookServices.getBookInfoListBySeries(db, mSeriesId, limit, offset);
    }

    @Override
    protected String getTitle() {
        return mSeries.name;
    }
}
