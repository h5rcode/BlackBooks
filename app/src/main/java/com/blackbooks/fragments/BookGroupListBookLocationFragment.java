package com.blackbooks.fragments;

import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.R;
import com.blackbooks.model.nonpersistent.BookGroup;
import com.blackbooks.services.BookGroupServices;
import com.blackbooks.services.SummaryServices;

import java.util.List;

/**
 * A fragment to display the book locations in the library.
 */
public final class BookGroupListBookLocationFragment extends AbstractBookGroupListFragment {

    @Override
    protected BookGroup.BookGroupType getBookGroupType() {
        return BookGroup.BookGroupType.BOOK_LOCATION;
    }

    @Override
    protected int getBookGroupCount(SQLiteDatabase db) {
        return SummaryServices.getBookLocationCount(db);
    }

    @Override
    protected List<BookGroup> loadBookGroupList(SQLiteDatabase db, int limit, int offset) {
        return BookGroupServices.getBookGroupListBookLocation(db, limit, offset);
    }

    @Override
    protected String getFooterText(int displayedBookGroupCount, int totalBookGroupCount) {
        Resources res = getResources();
        return res.getQuantityString(R.plurals.footer_fragment_book_groups_book_locations, displayedBookGroupCount, displayedBookGroupCount, totalBookGroupCount);
    }

    @Override
    protected String getMoreGroupsLoadedText(int bookGroupCount) {
        Resources res = getResources();
        return res.getQuantityString(R.plurals.message_book_groups_loaded_book_locations, bookGroupCount, bookGroupCount);
    }
}
