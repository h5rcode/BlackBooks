package com.blackbooks.fragments;

import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.model.nonpersistent.BookGroup;
import com.blackbooks.services.BookGroupServices;

import java.util.List;

/**
 * A fragment to display the first letters of the book titles in the library.
 */
public final class BookGroupListFirstLetterFragment extends AbstractBookGroupListFragment {

    @Override
    protected BookGroup.BookGroupType getBookGroupType() {
        return BookGroup.BookGroupType.FIRST_LETTER;
    }

    @Override
    protected int getBookGroupCount(SQLiteDatabase db) {
        return 0;
    }

    @Override
    protected List<BookGroup> loadBookGroupList(SQLiteDatabase db, int limit, int offset) {
        return BookGroupServices.getBookGroupListFirstLetter(db, limit, offset);
    }
}
