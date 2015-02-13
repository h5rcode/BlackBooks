package com.blackbooks.fragments;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.BookServices;

import java.util.List;

/**
 * A fragment to display the books of a given category.
 */
public final class BookListByCategoryFragment2 extends AbstractBookListFragment2 {

    private static final String ARG_CATEGORY_ID = "ARG_CATEGORY_ID";

    private long mCategoryId;

    /**
     * Return a new instance of BookListByCategoryFragment2, initialized to display the books of a category.
     *
     * @param categoryId Id of the category.
     * @return BookListByCategoryFragment2.
     */
    public static BookListByCategoryFragment2 newInstance(long categoryId) {
        BookListByCategoryFragment2 fragment = new BookListByCategoryFragment2();

        Bundle args = new Bundle();
        args.putLong(ARG_CATEGORY_ID, categoryId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mCategoryId = args.getLong(ARG_CATEGORY_ID);
    }

    @Override
    protected int getBookCount(SQLiteDatabase db) {
        return BookServices.getBookCountByCategory(db, mCategoryId);
    }

    @Override
    protected List<BookInfo> loadBookInfoList(SQLiteDatabase db, int limit, int offset) {
        return BookServices.getBookInfoListByCategory(db, mCategoryId, limit, offset);
    }
}
