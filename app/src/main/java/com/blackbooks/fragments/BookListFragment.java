package com.blackbooks.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.activities.BookDisplayActivity;
import com.blackbooks.activities.BookEditActivity;
import com.blackbooks.adapters.BookListAdapter;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookGroup;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.services.BookServices;
import com.blackbooks.utils.VariableUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Fragment to display the books belonging to a particular group.
 */
public final class BookListFragment extends ListFragment {

    private static final String AMAZON_SEARCH_RESULT_URL = "http://www.amazon.com/gp/search?ie=UTF8&index=books&keywords=%s&tag=h5rcode-20";

    private static final String ARG_BOOK_GROUP_TYPE = "ARG_BOOK_GROUP_TYPE";
    private static final String ARG_BOOK_GROUP_ID = "ARG_BOOK_GROUP_ID";

    private static final int BOOKS_BY_PAGE = 50;

    private static final int ITEM_BOOK_EDIT = 0x1;
    private static final int ITEM_BOOK_LOAN = 0x2;
    private static final int ITEM_BOOK_MARK_AS_READ = 0x3;
    private static final int ITEM_BOOK_MARK_AS_FAVOURITE = 0x4;
    private static final int ITEM_BOOK_SEARCH_AUTHOR_ON_AMAZON = 0x5;
    private static final int ITEM_BOOK_DELETE = 0x6;

    private BookGroup.BookGroupType mBookGroupType;
    private Serializable mBookGroupId;
    private Integer mBookCount;
    private int mLastPage = 1;
    private int mLastItem = -1;
    private BookLoadTask mBookLoadTask;

    private TextView textViewFooter;

    private BookListAdapter mBookListAdapter;

    /**
     * Return a new instance of BookListFragment initialized to display the books of a given book group.
     *
     * @param bookGroupType Type of the book group.
     * @param bookGroupId   Id of the book group.
     * @return BookListFragment.
     */
    public static BookListFragment newInstance(BookGroup.BookGroupType bookGroupType, Serializable bookGroupId) {
        BookListFragment fragment = new BookListFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOK_GROUP_TYPE, bookGroupType);
        args.putSerializable(ARG_BOOK_GROUP_ID, bookGroupId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Bundle args = getArguments();
        mBookGroupType = (BookGroup.BookGroupType) args.getSerializable(ARG_BOOK_GROUP_TYPE);
        mBookGroupId = args.getSerializable(ARG_BOOK_GROUP_ID);

        mBookListAdapter = new BookListAdapter(getActivity());
        setListAdapter(mBookListAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.abstract_book_list_fragment, container, false);

        ListView listView = (ListView) view.findViewById(android.R.id.list);
        textViewFooter = (TextView) view.findViewById(R.id.abstractBookList_textFooter);

        registerForContextMenu(listView);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // Do nothing.
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                switch (view.getId()) {
                    case android.R.id.list:

                        final int lastItem = firstVisibleItem + visibleItemCount;
                        if (lastItem == totalItemCount) {
                            if (mLastItem != lastItem) {
                                mLastItem = lastItem;
                                mBookLoadTask = new BookLoadTask(BOOKS_BY_PAGE, BOOKS_BY_PAGE * (mLastPage - 1));
                                mBookLoadTask.execute();
                                mLastPage++;
                            }
                        }
                }
            }
        });
        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        BookInfo book = (BookInfo) getListView().getAdapter().getItem(info.position);
        menu.setHeaderTitle(book.title);
        menu.add(Menu.NONE, ITEM_BOOK_EDIT, Menu.NONE, R.string.action_edit_book);
        int resIdLoanBook;
        if (book.loanedTo != null) {
            resIdLoanBook = R.string.action_return_book;
        } else {
            resIdLoanBook = R.string.action_loan_book;
        }
        int resIdMarkAsRead;
        if (book.isRead == 1L) {
            resIdMarkAsRead = R.string.action_mark_book_as_to_read;
        } else {
            resIdMarkAsRead = R.string.action_mark_book_as_read;
        }
        int resIdMarkAsFavourite;
        if (book.isFavourite == 1L) {
            resIdMarkAsFavourite = R.string.action_unmark_book_as_favourite;
        } else {
            resIdMarkAsFavourite = R.string.action_mark_book_as_favourite;
        }
        menu.add(Menu.NONE, ITEM_BOOK_LOAN, Menu.NONE, resIdLoanBook);
        menu.add(Menu.NONE, ITEM_BOOK_MARK_AS_READ, Menu.NONE, resIdMarkAsRead);
        menu.add(Menu.NONE, ITEM_BOOK_MARK_AS_FAVOURITE, Menu.NONE, resIdMarkAsFavourite);
        if (!book.authors.isEmpty()) {
            menu.add(Menu.NONE, ITEM_BOOK_SEARCH_AUTHOR_ON_AMAZON, Menu.NONE, R.string.action_search_author_on_amazon);
        }
        menu.add(Menu.NONE, ITEM_BOOK_DELETE, Menu.NONE, R.string.action_delete_book);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setFooterText();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (VariableUtils.getInstance().getReloadBookList()) {
            VariableUtils.getInstance().setReloadBookList(false);
            mLastItem = -1;
            mLastPage = 1;
            mBookListAdapter.clear();
            mBookListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBookLoadTask != null) {
            mBookLoadTask.cancel(true);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Book book = (Book) getListAdapter().getItem(position);
        Intent i = new Intent(this.getActivity(), BookDisplayActivity.class);
        i.putExtra(BookDisplayActivity.EXTRA_BOOK_ID, book.id);
        this.startActivity(i);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        boolean result = true;

        final BookInfo book = (BookInfo) getListAdapter().getItem(info.position);
        Intent i;

        switch (item.getItemId()) {
            case ITEM_BOOK_EDIT:
                i = new Intent(this.getActivity(), BookEditActivity.class);
                i.putExtra(BookEditActivity.EXTRA_MODE, BookEditActivity.MODE_EDIT);
                i.putExtra(BookEditActivity.EXTRA_BOOK_ID, book.id);
                startActivity(i);
                break;

            case ITEM_BOOK_LOAN:
                if (book.loanedTo == null) {
                    i = new Intent(this.getActivity(), BookDisplayActivity.class);
                    i.putExtra(BookDisplayActivity.EXTRA_MODE, BookDisplayActivity.MODE_LOAN);
                    i.putExtra(BookDisplayActivity.EXTRA_BOOK_ID, book.id);
                    startActivity(i);
                } else {
                    showConfirmReturnDialog(book);
                }
                break;

            case ITEM_BOOK_MARK_AS_READ:
                markBookAsRead(book);
                break;

            case ITEM_BOOK_MARK_AS_FAVOURITE:
                markBookAsFavourite(book);
                break;

            case ITEM_BOOK_SEARCH_AUTHOR_ON_AMAZON:
                Author author = book.authors.get(0);
                String authorName = author.name;
                authorName = Uri.encode(authorName);
                String url = String.format(AMAZON_SEARCH_RESULT_URL, authorName);

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                break;

            case ITEM_BOOK_DELETE:
                showDeleteConfirmDialog(book);
                break;

            default:
                result = super.onContextItemSelected(item);
        }
        return result;
    }

    /**
     * Delete a book.
     *
     * @param book BookInfo.
     */
    private void deleteBook(BookInfo book) {
        String title = book.title;
        String message = String.format(getString(R.string.message_book_deleted), title);

        SQLiteDatabase db = SQLiteHelper.getInstance().getWritableDatabase();
        BookServices.deleteBook(db, book.id);
        VariableUtils.getInstance().setReloadBookList(true);
        Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT).show();
        mBookCount--;
        mBookListAdapter.remove(book);
        mBookListAdapter.notifyDataSetChanged();
    }

    /**
     * Mark or unmark a book as favourite.
     *
     * @param book Book.
     */
    private void markBookAsFavourite(Book book) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getWritableDatabase();
        BookServices.markBookAsFavourite(db, book.id);

        String text;
        if (book.isFavourite == 1L) {
            text = getString(R.string.message_book_unmarked_as_favourite, book.title);
        } else {
            text = getString(R.string.message_book_marked_as_favourite, book.title);
        }
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();

        book.isFavourite = 1L - book.isFavourite;
        mBookListAdapter.notifyDataSetChanged();
    }

    /**
     * Mark or unmark a book as read.
     *
     * @param book Book.
     */
    private void markBookAsRead(Book book) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getWritableDatabase();
        BookServices.markBookAsRead(db, book.id);

        String text;
        if (book.isRead == 1L) {
            text = getString(R.string.message_book_marked_as_to_read, book.title);
        } else {
            text = getString(R.string.message_book_marked_as_read, book.title);
        }
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();

        book.isRead = 1L - book.isRead;
        mBookListAdapter.notifyDataSetChanged();
    }

    /**
     * Return a book.
     *
     * @param book Book.
     */
    private void returnBook(Book book) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getWritableDatabase();
        BookServices.returnBook(db, book.id);

        String message = getString(R.string.message_book_returned, book.title);
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

        book.loanedTo = null;
        mBookListAdapter.notifyDataSetChanged();
    }

    /**
     * Set the footer text.
     */
    private void setFooterText() {
        if (mBookCount != null) {

            int displayedBookCount = mBookListAdapter.getCount();

            Resources res = getResources();
            String footerText = res.getQuantityString(R.plurals.footer_fragment_books, displayedBookCount, displayedBookCount, mBookCount);

            textViewFooter.setText(footerText);
        }
    }

    /**
     * Show the return book confirm dialog.
     *
     * @param book BookInfo.
     */
    private void showConfirmReturnDialog(final BookInfo book) {
        String message = getString(R.string.message_confirm_return_book, book.title);

        String cancelText = getString(R.string.message_confirm_return_book_cancel);
        String confirmText = getString(R.string.message_confirm_return_book_confirm);

        new AlertDialog.Builder(this.getActivity()) //
                .setTitle(R.string.title_dialog_return_book) //
                .setMessage(message) //
                .setPositiveButton(confirmText, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        returnBook(book);
                    }
                }).setNegativeButton(cancelText, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing.
            }
        }).show();
    }

    /**
     * Show the delete confirm dialog.
     *
     * @param book Book.
     */
    private void showDeleteConfirmDialog(final BookInfo book) {
        String message = getString(R.string.message_confirm_delete_book);
        message = String.format(message, book.title);

        String cancelText = getString(R.string.message_confirm_delete_book_cancel);
        String confirmText = getString(R.string.message_confirm_delete_book_confirm);

        new AlertDialog.Builder(this.getActivity()) //
                .setTitle(R.string.title_dialog_delete_book) //
                .setMessage(message) //
                .setPositiveButton(confirmText, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BookListFragment.this.deleteBook(book);
                    }
                }).setNegativeButton(cancelText, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing.
            }
        }).show();
    }

    /**
     * A task to load the books of the current group.
     */
    private final class BookLoadTask extends AsyncTask<Void, Void, List<BookInfo>> {

        private final int mLimit;
        private final int mOffset;

        /**
         * Constructor.
         *
         * @param limit  Max number of books to load.
         * @param offset The offset when loading books.
         */
        public BookLoadTask(int limit, int offset) {
            super();
            mLimit = limit;
            mOffset = offset;
        }

        @Override
        protected List<BookInfo> doInBackground(Void... params) {
            SQLiteDatabase db = SQLiteHelper.getInstance().getReadableDatabase();
            mBookCount = BookServices.getBookCountByBookGroup(db, mBookGroupType, mBookGroupId);
            return BookServices.getBookInfoListByBookGroup(db, mBookGroupType, mBookGroupId, mLimit, mOffset);
        }

        @Override
        protected void onPostExecute(List<BookInfo> bookInfoList) {
            super.onPostExecute(bookInfoList);
            mBookListAdapter.addAll(bookInfoList);
            mBookListAdapter.notifyDataSetChanged();

            int bookCount = bookInfoList.size();
            if (bookCount > 0) {
                Resources res = getResources();
                String message = res.getQuantityString(R.plurals.message_books_loaded, bookCount, bookCount);
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                setFooterText();
            }
        }
    }
}
