package com.blackbooks.fragments;

import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;

import com.blackbooks.R;
import com.blackbooks.adapters.BookItem;
import com.blackbooks.adapters.BooksByAuthorAdapter;
import com.blackbooks.adapters.BooksByAuthorAdapter.AuthorItem;
import com.blackbooks.adapters.BooksByAuthorAdapter.SeriesItem;
import com.blackbooks.adapters.ListItem;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.AuthorInfo;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.nonpersistent.SeriesInfo;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.services.AuthorServices;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements {@link AbstractBookListFragment}. A fragment that lists books by
 * author.
 *
 * @deprecated Replaced by BookListByAuthorFragment2.
 */
@Deprecated
public class BookListByAuthorFragment extends AbstractBookListFragment {

    private String mFooterText;

    @Override
    protected String getActionBarSubtitle() {
        return getString(R.string.action_sort_by_author);
    }

    @Override
    protected ArrayAdapter<ListItem> getBookListAdapter() {
        return new BooksByAuthorAdapter(this.getActivity());
    }

    @Override
    protected String getFooterText() {
        return mFooterText;
    }

    @Override
    protected List<ListItem> loadBookList() {
        List<AuthorInfo> authorInfoList;
        SQLiteDatabase db = SQLiteHelper.getInstance().getReadableDatabase();
        authorInfoList = AuthorServices.getAuthorInfoList(db);

        List<ListItem> listItems = new ArrayList<ListItem>();

        int authorCount = 0;
        List<Long> bookIdList = new ArrayList<Long>();
        for (AuthorInfo authorInfo : authorInfoList) {
            if (authorInfo.id == null) {
                authorInfo.name = getString(R.string.label_unspecified_author);
            } else {
                authorCount++;
            }
            AuthorItem authorItem = new AuthorItem(authorInfo);
            listItems.add(authorItem);

            for (SeriesInfo series : authorInfo.series) {
                SeriesItem seriesItem = new SeriesItem(series);
                listItems.add(seriesItem);

                for (Book book : series.books) {
                    BookInfo bookInfo = new BookInfo(book);
                    BookItem bookEntry = new BookItem(bookInfo);
                    listItems.add(bookEntry);

                    if (!bookIdList.contains(book.id)) {
                        bookIdList.add(book.id);
                    }
                }
            }
        }

        Resources res = getResources();
        int bookCount = bookIdList.size();
        String authors = res.getQuantityString(R.plurals.label_footer_authors, authorCount, authorCount);
        String books = res.getQuantityString(R.plurals.label_footer_books, bookCount, bookCount);

        mFooterText = getString(R.string.footer_fragment_books_by_author, authors, books);

        return listItems;
    }
}
