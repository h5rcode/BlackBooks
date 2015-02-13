package com.blackbooks.fragments;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.BookServices;

import java.util.List;

/**
 * A fragment to display the books of a given author.
 */
public final class BookListByAuthorFragment2 extends AbstractBookListFragment2 {

    private static final String ARG_AUTHOR_ID = "ARG_AUTHOR_ID";

    private long mAuthorId;

    /**
     * Return a new instance of BookListByAuthorFragment2, initialized to display the books of an author.
     *
     * @param authorId Id of the author.
     * @return BookListByAuthorFragment2.
     */
    public static BookListByAuthorFragment2 newInstance(long authorId) {
        BookListByAuthorFragment2 fragment = new BookListByAuthorFragment2();

        Bundle args = new Bundle();
        args.putLong(ARG_AUTHOR_ID, authorId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mAuthorId = args.getLong(ARG_AUTHOR_ID);
    }

    @Override
    protected int getBookCount(SQLiteDatabase db) {
        return BookServices.getBookCountByAuthor(db, mAuthorId);
    }

    @Override
    protected List<BookInfo> loadBookInfoList(SQLiteDatabase db, int limit, int offset) {
        return BookServices.getBookInfoListByAuthor(db, mAuthorId, limit, offset);
    }
}
