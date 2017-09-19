package com.blackbooks.fragments.booklist;

import android.content.Context;

import com.blackbooks.R;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.repositories.BookRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

/**
 * A fragment to display the books to read.
 */
public final class BookListToReadFragment extends AbstractBookListFragment {

    @Inject
    BookRepository summaryService;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    protected int getBookCount() {
        return summaryService.getBookToReadCount();
    }

    @Override
    protected List<BookInfo> loadBookInfoList(int limit, int offset) {
        return bookService.getBookInfoListToRead(limit, offset);
    }

    @Override
    protected String getTitle() {
        return getString(R.string.title_activity_books_to_read);
    }
}
