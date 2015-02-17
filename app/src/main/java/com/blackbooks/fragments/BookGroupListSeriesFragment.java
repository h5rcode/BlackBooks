package com.blackbooks.fragments;

import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.R;
import com.blackbooks.model.nonpersistent.BookGroup;
import com.blackbooks.services.BookGroupServices;
import com.blackbooks.services.SummaryServices;

import java.util.List;

/**
 * A fragment to display the series in the library.
 */
public final class BookGroupListSeriesFragment extends AbstractBookGroupListFragment {

    @Override
    protected BookGroup.BookGroupType getBookGroupType() {
        return BookGroup.BookGroupType.SERIES;
    }

    @Override
    protected int getBookGroupCount(SQLiteDatabase db) {
        return SummaryServices.getSeriesCount(db);
    }

    @Override
    protected List<BookGroup> loadBookGroupList(SQLiteDatabase db, int limit, int offset) {
        return BookGroupServices.getBookGroupListSeries(db, limit, offset);
    }

    @Override
    protected String getFooterText(int displayedBookGroupCount, int totalBookGroupCount) {
        Resources res = getResources();
        return res.getQuantityString(R.plurals.footer_fragment_book_groups_series, displayedBookGroupCount, displayedBookGroupCount, totalBookGroupCount);
    }

    @Override
    protected String getMoreGroupsLoadedText(int bookGroupCount) {
        Resources res = getResources();
        return res.getQuantityString(R.plurals.message_book_groups_loaded_series, bookGroupCount, bookGroupCount);
    }
}
