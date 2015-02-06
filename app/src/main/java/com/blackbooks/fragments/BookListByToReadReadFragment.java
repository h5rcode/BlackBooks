package com.blackbooks.fragments;

import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;

import com.blackbooks.R;
import com.blackbooks.adapters.BookItem;
import com.blackbooks.adapters.BooksByToReadReadAdapter;
import com.blackbooks.adapters.BooksByToReadReadAdapter.ToReadReadItem;
import com.blackbooks.adapters.ListItem;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.BookServices;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements {@link AbstractBookListFragment}. A fragment that lists books in
 * two groups: "To read" or "Read".
 */
public class BookListByToReadReadFragment extends AbstractBookListFragment {

    private String mFooterText;

    @Override
    protected String getActionBarSubtitle() {
        return getString(R.string.action_sort_by_to_read_read);
    }

    @Override
    protected ArrayAdapter<ListItem> getBookListAdapter() {
        return new BooksByToReadReadAdapter(getActivity());
    }

    @Override
    protected String getFooterText() {
        return mFooterText;
    }

    @Override
    protected List<ListItem> loadBookList() {

        SQLiteDatabase db = SQLiteHelper.getInstance().getReadableDatabase();
        List<BookInfo> bookInfoList = BookServices.getBookInfoList(db);

        List<ListItem> listItems = new ArrayList<ListItem>();

        List<BookInfo> toReadBookList = new ArrayList<BookInfo>();
        List<BookInfo> readBookList = new ArrayList<BookInfo>();

        for (BookInfo bookInfo : bookInfoList) {
            if (bookInfo.isRead == 1L) {
                readBookList.add(bookInfo);
            } else {
                toReadBookList.add(bookInfo);
            }
        }

        if (!toReadBookList.isEmpty()) {
            ToReadReadItem toReadItem = new ToReadReadItem(false, toReadBookList.size());
            listItems.add(toReadItem);

            for (BookInfo bookInfo : toReadBookList) {
                BookItem bookItem = new BookItem(bookInfo);
                listItems.add(bookItem);
            }
        }

        if (!readBookList.isEmpty()) {
            ToReadReadItem readItem = new ToReadReadItem(true, readBookList.size());
            listItems.add(readItem);

            for (BookInfo bookInfo : readBookList) {
                BookItem bookItem = new BookItem(bookInfo);
                listItems.add(bookItem);
            }
        }

        int readCount = readBookList.size();
        int toReadCount = toReadBookList.size();
        int bookCount = bookInfoList.size();

        Resources res = getResources();
        String read = res.getQuantityString(R.plurals.label_footer_read, readCount, readCount);
        String toRead = res.getQuantityString(R.plurals.label_footer_to_read, toReadCount, toReadCount);
        String books = res.getQuantityString(R.plurals.label_footer_books, bookCount, bookCount);
        mFooterText = getString(R.string.footer_fragment_books_by_to_read_read, books, toRead, read);

        return listItems;
    }

}
