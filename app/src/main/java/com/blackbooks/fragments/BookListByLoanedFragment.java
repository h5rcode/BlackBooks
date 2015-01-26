package com.blackbooks.fragments;

import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;

import com.blackbooks.R;
import com.blackbooks.adapters.BookItem;
import com.blackbooks.adapters.BooksByLoanedAdapter;
import com.blackbooks.adapters.BooksByLoanedAdapter.LoanDateItem;
import com.blackbooks.adapters.BooksByLoanedAdapter.LoanedAvailableItem;
import com.blackbooks.adapters.BooksByLoanedAdapter.LoanedToItem;
import com.blackbooks.adapters.ListItem;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.BookServices;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BookListByLoanedFragment extends AbstractBookListFragment {

    private String mFooterText;

    @Override
    protected String getActionBarSubtitle() {
        return getString(R.string.action_sort_by_loaned);
    }

    @Override
    protected ArrayAdapter<ListItem> getBookListAdapter() {
        return new BooksByLoanedAdapter(getActivity());
    }

    @Override
    protected String getFooterText() {
        return mFooterText;
    }

    @Override
    protected List<ListItem> loadBookList() {
        SQLiteHelper dbHelper = new SQLiteHelper(this.getActivity());
        SQLiteDatabase db = null;
        List<BookInfo> bookInfoList;
        try {
            db = dbHelper.getReadableDatabase();
            bookInfoList = BookServices.getBookInfoList(db);
        } finally {
            db.close();
        }

        Map<String, Map<Date, List<BookInfo>>> loanedToMap = new TreeMap<String, Map<Date, List<BookInfo>>>();

        List<BookInfo> availableBookList = new ArrayList<BookInfo>();

        int loanedBookCount = 0;
        int availableBookCount = 0;
        for (BookInfo bookInfo : bookInfoList) {
            String loanedTo = bookInfo.loanedTo;
            Date loanDate = bookInfo.loanDate;

            if (loanedTo == null) {
                availableBookCount++;
                availableBookList.add(bookInfo);
            } else {
                loanedBookCount++;
                Map<Date, List<BookInfo>> loanDateMap = loanedToMap.get(loanedTo);

                if (loanDateMap == null) {
                    loanDateMap = new TreeMap<Date, List<BookInfo>>();
                    loanedToMap.put(loanedTo, loanDateMap);
                }

                List<BookInfo> loanedBookInfoList = loanDateMap.get(loanDate);

                if (loanedBookInfoList == null) {
                    loanedBookInfoList = new ArrayList<BookInfo>();
                    loanDateMap.put(loanDate, loanedBookInfoList);
                }

                loanedBookInfoList.add(bookInfo);
            }
        }

        List<ListItem> listItems = new ArrayList<ListItem>();

        if (!loanedToMap.isEmpty()) {
            LoanedAvailableItem loanedAvailable = new LoanedAvailableItem(true, loanedBookCount);
            listItems.add(loanedAvailable);

            for (String loanedTo : loanedToMap.keySet()) {
                LoanedToItem loanedToItem = new LoanedToItem(loanedTo, 0);

                listItems.add(loanedToItem);

                Map<Date, List<BookInfo>> loanedBookInfoMap = loanedToMap.get(loanedTo);
                for (Date loanDate : loanedBookInfoMap.keySet()) {
                    LoanDateItem loanDateItem = new LoanDateItem(getActivity(), loanDate);

                    listItems.add(loanDateItem);

                    List<BookInfo> loandBookInfoList = loanedBookInfoMap.get(loanDate);

                    for (BookInfo bookInfo : loandBookInfoList) {
                        BookItem bookItem = new BookItem(bookInfo);

                        listItems.add(bookItem);
                    }
                }
            }
        }

        if (!availableBookList.isEmpty()) {
            LoanedAvailableItem loanedAvailable = new LoanedAvailableItem(false, availableBookCount);
            listItems.add(loanedAvailable);
            for (BookInfo bookInfo : availableBookList) {
                BookItem bookItem = new BookItem(bookInfo);
                listItems.add(bookItem);
            }
        }

        int bookCount = bookInfoList.size();

        Resources res = getResources();
        String books = res.getQuantityString(R.plurals.label_footer_books, bookCount, bookCount);
        String loaned = res.getQuantityString(R.plurals.label_footer_loaned, loanedBookCount, loanedBookCount);
        String available = res.getQuantityString(R.plurals.label_footer_available, availableBookCount, availableBookCount);

        mFooterText = getString(R.string.footer_fragment_books_by_loaned, books, loaned, available);

        return listItems;
    }
}
