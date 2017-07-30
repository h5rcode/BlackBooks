package com.blackbooks.fragments.bookgrouplist;

import android.content.Context;
import android.content.res.Resources;

import com.blackbooks.R;
import com.blackbooks.model.nonpersistent.BookGroup;
import com.blackbooks.repositories.BookRepository;
import com.blackbooks.services.BookGroupService;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

/**
 * A fragment to display the people who are loaned a book
 */
public final class BookGroupListLoanFragment extends AbstractBookGroupListFragment {
    @Inject
    BookGroupService bookGroupService;

    @Inject
    BookRepository summaryService;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    protected BookGroup.BookGroupType getBookGroupType() {
        return BookGroup.BookGroupType.LOAN;
    }

    @Override
    protected int getBookGroupCount() {
        return summaryService.getBookLoanCount();
    }

    @Override
    protected List<BookGroup> loadBookGroupList(int limit, int offset) {
        return bookGroupService.getBookGroupListLoan(limit, offset);
    }

    @Override
    protected String getFooterText(int displayedBookGroupCount, int totalBookGroupCount) {
        Resources res = getResources();
        return res.getQuantityString(R.plurals.footer_fragment_book_groups_loans, displayedBookGroupCount, displayedBookGroupCount, totalBookGroupCount);
    }

    @Override
    protected String getMoreGroupsLoadedText(int bookGroupCount) {
        Resources res = getResources();
        return res.getQuantityString(R.plurals.message_book_groups_loaded_loans, bookGroupCount, bookGroupCount);
    }
}
