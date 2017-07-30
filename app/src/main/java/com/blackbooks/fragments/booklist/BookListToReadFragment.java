package com.blackbooks.fragments.booklist;

import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.R;
import com.blackbooks.fragments.booklist.AbstractBookListFragment2;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.BookServices;
import com.blackbooks.services.SummaryServices;

import java.util.List;

/**
 * A fragment to display the books to read.
 */
public final class BookListToReadFragment extends AbstractBookListFragment2 {

    @Override
    protected int getBookCount(SQLiteDatabase db) {
        return SummaryServices.getBookToReadCount(db);
    }

    @Override
    protected List<BookInfo> loadBookInfoList(SQLiteDatabase db, int limit, int offset) {
        return BookServices.getBookInfoListToRead(db, limit, offset);
    }

    @Override
    protected String getTitle() {
        return getString(R.string.title_activity_books_to_read);
    }
}
