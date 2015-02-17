package com.blackbooks.fragments;

import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.R;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.BookServices;
import com.blackbooks.services.SummaryServices;

import java.util.List;

/**
 * A fragment to display favourite books.
 */
public final class BookListFavouriteFragment extends AbstractBookListFragment2 {

    @Override
    protected int getBookCount(SQLiteDatabase db) {
        return SummaryServices.getFavouriteBooks(db);
    }

    @Override
    protected List<BookInfo> loadBookInfoList(SQLiteDatabase db, int limit, int offset) {
        return BookServices.getBookInfoListFavourite(db, limit, offset);
    }

    @Override
    protected String getTitle() {
        return getString(R.string.title_activity_books_favourites);
    }
}
