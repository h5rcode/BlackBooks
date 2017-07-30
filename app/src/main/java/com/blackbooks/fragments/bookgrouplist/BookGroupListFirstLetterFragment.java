package com.blackbooks.fragments.bookgrouplist;

import android.content.res.Resources;
import android.os.Bundle;

import com.blackbooks.R;
import com.blackbooks.model.nonpersistent.BookGroup;
import com.blackbooks.services.BookGroupService;
import com.blackbooks.services.SummaryService;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

/**
 * A fragment to display the first letters of the book titles in the library.
 */
public final class BookGroupListFirstLetterFragment extends AbstractBookGroupListFragment {

    @Inject
    BookGroupService bookGroupService;

    @Inject
    SummaryService summaryService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected BookGroup.BookGroupType getBookGroupType() {
        return BookGroup.BookGroupType.FIRST_LETTER;
    }

    @Override
    protected int getBookGroupCount() {
        return summaryService.getFirstLetterCount();
    }

    @Override
    protected List<BookGroup> loadBookGroupList(int limit, int offset) {
        return bookGroupService.getBookGroupListFirstLetter(limit, offset);
    }

    @Override
    protected String getFooterText(int displayedBookGroupCount, int totalBookGroupCount) {
        Resources res = getResources();
        return res.getQuantityString(R.plurals.footer_fragment_book_groups_first_letters, displayedBookGroupCount, displayedBookGroupCount, totalBookGroupCount);
    }

    @Override
    protected String getMoreGroupsLoadedText(int bookGroupCount) {
        Resources res = getResources();
        return res.getQuantityString(R.plurals.message_book_groups_loaded_first_letter, bookGroupCount, bookGroupCount);
    }
}
