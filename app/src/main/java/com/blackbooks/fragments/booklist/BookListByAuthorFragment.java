package com.blackbooks.fragments.booklist;

import android.content.Context;
import android.os.Bundle;

import com.blackbooks.R;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.services.AuthorService;
import com.blackbooks.services.BookService;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

/**
 * A fragment to display the books of a given author.
 */
public final class BookListByAuthorFragment extends AbstractBookListFragment {

    private static final String ARG_AUTHOR_ID = "ARG_AUTHOR_ID";

    private long mAuthorId;
    private Author mAuthor;

    @Inject
    AuthorService authorService;

    @Inject
    BookService bookService;

    /**
     * Return a new instance of BookListByAuthorFragment, initialized to display the books of an author.
     *
     * @param authorId Id of the author.
     * @return BookListByAuthorFragment.
     */
    public static BookListByAuthorFragment newInstance(long authorId) {
        BookListByAuthorFragment fragment = new BookListByAuthorFragment();

        Bundle args = new Bundle();
        args.putLong(ARG_AUTHOR_ID, authorId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mAuthorId = args.getLong(ARG_AUTHOR_ID);

        mAuthor = authorService.getAuthor(mAuthorId);
    }

    @Override
    protected int getBookCount() {
        return bookService.getBookCountByAuthor(mAuthorId);
    }

    @Override
    protected List<BookInfo> loadBookInfoList(int limit, int offset) {
        return bookService.getBookInfoListByAuthor(mAuthorId, limit, offset);
    }

    @Override
    protected String getTitle() {
        return getString(R.string.title_activity_books_by_author, mAuthor.name);
    }
}
