package com.blackbooks.fragments.booklist;

import android.os.Bundle;

import com.blackbooks.R;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.services.CategoryService;

import java.util.List;

import javax.inject.Inject;

/**
 * A fragment to display the books of a given category.
 */
public final class BookListByCategoryFragment extends AbstractBookListFragment {

    private static final String ARG_CATEGORY_ID = "ARG_CATEGORY_ID";

    private long mCategoryId;
    private Category mCategory;

    @Inject
    CategoryService categoryService;

    /**
     * Return a new instance of BookListByCategoryFragment, initialized to display the books of a category.
     *
     * @param categoryId Id of the category.
     * @return BookListByCategoryFragment.
     */
    public static BookListByCategoryFragment newInstance(long categoryId) {
        BookListByCategoryFragment fragment = new BookListByCategoryFragment();

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

        mCategory = categoryService.getCategory(mCategoryId);
    }

    @Override
    protected int getBookCount() {
        return bookService.getBookCountByCategory(mCategoryId);
    }

    @Override
    protected List<BookInfo> loadBookInfoList(int limit, int offset) {
        return bookService.getBookInfoListByCategory(mCategoryId, limit, offset);
    }

    @Override
    protected String getTitle() {
        return getString(R.string.title_activity_books_by_category, mCategory.name);
    }
}
