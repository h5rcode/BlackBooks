package com.blackbooks.fragments.bookgrouplist;

import android.content.Context;
import android.content.res.Resources;

import com.blackbooks.R;
import com.blackbooks.model.nonpersistent.BookGroup;
import com.blackbooks.repositories.BookRepository;
import com.blackbooks.services.BookGroupService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

/**
 * A fragment to display the languages in the library.
 */
public final class BookGroupListLanguageFragment extends AbstractBookGroupListFragment {

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
        return BookGroup.BookGroupType.LANGUAGE;
    }

    @Override
    protected int getBookGroupCount() {
        return summaryService.getLanguageCount();
    }

    @Override
    protected List<BookGroup> loadBookGroupList(int limit, int offset) {
        List<BookGroup> bookGroupList;
        if (offset == 0) {
            bookGroupList = bookGroupService.getBookGroupListLanguage();
        } else {
            bookGroupList = new ArrayList<>();
        }
        return bookGroupList;
    }

    @Override
    protected String getFooterText(int displayedBookGroupCount, int totalBookGroupCount) {
        Resources res = getResources();
        return res.getQuantityString(R.plurals.footer_fragment_book_groups_languages, displayedBookGroupCount, displayedBookGroupCount, totalBookGroupCount);
    }

    @Override
    protected String getMoreGroupsLoadedText(int bookGroupCount) {
        Resources res = getResources();
        return res.getQuantityString(R.plurals.message_book_groups_loaded_languages, bookGroupCount, bookGroupCount);
    }
}
