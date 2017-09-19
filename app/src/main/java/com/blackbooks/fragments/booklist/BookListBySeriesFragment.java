package com.blackbooks.fragments.booklist;

import android.content.Context;
import android.os.Bundle;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Series;
import com.blackbooks.repositories.SeriesRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

/**
 * A fragment to display the books of a given series.
 */
public final class BookListBySeriesFragment extends AbstractBookListFragment {

    private static final String ARG_SERIES_ID = "ARG_SERIES_ID";

    private long mSeriesId;
    private Series mSeries;

    @Inject
    SeriesRepository seriesService;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    /**
     * Return a new instance of BookListBySeriesFragment, initialized to display the books of a series.
     *
     * @param seriesId Series id.
     * @return BookListBySeriesFragment.
     */
    public static BookListBySeriesFragment newInstance(long seriesId) {
        BookListBySeriesFragment fragment = new BookListBySeriesFragment();

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

        mSeries = seriesService.getSeries(mSeriesId);
    }

    @Override
    protected int getBookCount() {
        return bookService.getBookCountBySeries(mSeriesId);
    }

    @Override
    protected List<BookInfo> loadBookInfoList(int limit, int offset) {
        return bookService.getBookInfoListBySeries(mSeriesId, limit, offset);
    }

    @Override
    protected String getTitle() {
        return mSeries.name;
    }
}
