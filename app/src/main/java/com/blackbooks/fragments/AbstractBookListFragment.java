package com.blackbooks.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.activities.BookDisplayActivity;
import com.blackbooks.activities.BookEditActivity;
import com.blackbooks.adapters.BookItem;
import com.blackbooks.adapters.ListItem;
import com.blackbooks.adapters.ListItemType;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.services.BookServices;
import com.blackbooks.utils.VariableUtils;

import java.util.List;

/**
 * Abstract book list fragment.
 */
public abstract class AbstractBookListFragment extends ListFragment {

    private static final String AMAZON_SEARCH_RESULT_URL = "http://www.amazon.com/gp/search?ie=UTF8&index=books&keywords=%s&tag=h5rcode-20";

    private static final int ITEM_BOOK_EDIT = 0x1;
    private static final int ITEM_BOOK_LOAN = 0x2;
    private static final int ITEM_BOOK_MARK_AS_READ = 0x3;
    private static final int ITEM_BOOK_MARK_AS_FAVOURITE = 0x4;
    private static final int ITEM_BOOK_SEARCH_AUTHOR_ON_AMAZON = 0x5;
    private static final int ITEM_BOOK_DELETE = 0x6;

    private View mEmptyView;
    private View mLoadingView;
    private ListView mListView;
    private TextView mTextFooter;
    private ArrayAdapter<ListItem> mBookListAdapter;
    private BookListLoadTask mBookListLoadTask;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.getActionBar().setSubtitle(getActionBarSubtitle());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBookListAdapter = getBookListAdapter();
        setListAdapter(mBookListAdapter);
        setRetainInstance(true);
        setReloadBookListToTrue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.abstract_book_list_fragment, container, false);
        mListView = (ListView) view.findViewById(android.R.id.list);
        mTextFooter = (TextView) view.findViewById(R.id.abstractBookList_textFooter);
        mEmptyView = view.findViewById(R.id.abstractBookList_emptyView);
        mLoadingView = view.findViewById(R.id.abstractBookList_loadingView);
        mListView.setFastScrollEnabled(true);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView listView = getListView();
        registerForContextMenu(listView);
        mTextFooter.setText(getFooterText());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        ListItem listItem = (ListItem) getListView().getAdapter().getItem(info.position);
        if (listItem.getListItemType() == ListItemType.ENTRY) {
            BookItem bookItem = (BookItem) listItem;
            BookInfo book = bookItem.getBook();
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
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getReloadBookList()) {
            loadData();
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        boolean result = true;

        BookItem bookItem;
        final BookInfo book;
        Intent i;

        switch (item.getItemId()) {
            case ITEM_BOOK_EDIT:
                bookItem = (BookItem) getListAdapter().getItem(info.position);
                book = bookItem.getBook();
                i = new Intent(this.getActivity(), BookEditActivity.class);
                i.putExtra(BookEditActivity.EXTRA_MODE, BookEditActivity.MODE_EDIT);
                i.putExtra(BookEditActivity.EXTRA_BOOK_ID, book.id);
                startActivity(i);
                break;

            case ITEM_BOOK_LOAN:
                bookItem = (BookItem) getListAdapter().getItem(info.position);
                book = bookItem.getBook();
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
                bookItem = (BookItem) getListAdapter().getItem(info.position);
                book = bookItem.getBook();
                markBookAsRead(book);
                break;

            case ITEM_BOOK_MARK_AS_FAVOURITE:
                bookItem = (BookItem) getListAdapter().getItem(info.position);
                book = bookItem.getBook();
                markBookAsFavourite(book);
                break;

            case ITEM_BOOK_SEARCH_AUTHOR_ON_AMAZON:
                bookItem = (BookItem) getListAdapter().getItem(info.position);
                book = bookItem.getBook();

                Author author = book.authors.get(0);
                String authorName = author.name;
                authorName = Uri.encode(authorName);
                String url = String.format(AMAZON_SEARCH_RESULT_URL, authorName);

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                break;

            case ITEM_BOOK_DELETE:
                bookItem = (BookItem) getListAdapter().getItem(info.position);
                book = bookItem.getBook();
                showDeleteConfirmDialog(book);
                break;

            default:
                result = super.onContextItemSelected(item);
        }
        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBookListLoadTask != null) {
            mBookListLoadTask.cancel(true);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ListItem item = (ListItem) getListAdapter().getItem(position);
        ListItemType itemType = item.getListItemType();

        if (itemType == ListItemType.ENTRY) {
            BookItem bookItem = (BookItem) item;
            BookInfo book = bookItem.getBook();
            Intent i = new Intent(this.getActivity(), BookDisplayActivity.class);
            i.putExtra(BookDisplayActivity.EXTRA_BOOK_ID, book.id);
            this.startActivity(i);
        }
    }

    /**
     * Return the subtitle of the action bar.
     *
     * @return Subtitle.
     */
    protected abstract String getActionBarSubtitle();

    /**
     * Return a new instance of the adapter used to draw the list of books.
     *
     * @return ArrayAdapter of {@link ListItem}.
     */
    protected abstract ArrayAdapter<ListItem> getBookListAdapter();

    /**
     * Return the text to be displayed in the footer.
     *
     * @return Text.
     */
    protected abstract String getFooterText();

    /**
     * Load the book list and notify the list adapter of the fragment. This
     * method is run inside an AsyncTask (i.e. in a different thread) to avoid
     * blocking the activity with a long running database load.
     */
    protected abstract List<ListItem> loadBookList();

    /**
     * Load the data to be displayed in the fragment.
     */
    protected final void loadData() {
        setReloadBookListToFalse();
        mBookListLoadTask = new BookListLoadTask();
        mBookListLoadTask.execute();
    }

    /**
     * Delete a book.
     *
     * @param book Book.
     */
    private void deleteBook(Book book) {
        String title = book.title;
        String message = String.format(getString(R.string.message_book_deleted), title);

        SQLiteHelper dbHelper = new SQLiteHelper(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        BookServices.deleteBook(db, book.id);
        db.close();
        VariableUtils.getInstance().setReloadBookList(true);
        Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT).show();
        loadData();
    }

    /**
     * Return a value indicating if the book list should be reloaded.
     *
     * @return True to refresh the book list, false otherwise.
     */
    private boolean getReloadBookList() {
        return VariableUtils.getInstance().getReloadBookList();
    }

    /**
     * Mark or unmark a book as favourite.
     *
     * @param book Book.
     */
    private void markBookAsFavourite(Book book) {
        SQLiteHelper dbHelper;
        SQLiteDatabase db;
        dbHelper = new SQLiteHelper(getActivity());
        db = dbHelper.getWritableDatabase();
        BookServices.markBookAsFavourite(db, book.id);
        db.close();

        String text;
        if (book.isFavourite == 1L) {
            text = getString(R.string.message_book_unmarked_as_favourite, book.title);
        } else {
            text = getString(R.string.message_book_marked_as_favourite, book.title);
        }
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();

        loadData();
    }

    /**
     * Mark or unmark a book as read.
     *
     * @param book Book.
     */
    private void markBookAsRead(Book book) {
        SQLiteHelper dbHelper;
        SQLiteDatabase db;
        dbHelper = new SQLiteHelper(getActivity());
        db = dbHelper.getWritableDatabase();
        BookServices.markBookAsRead(db, book.id);
        db.close();

        String text;
        if (book.isRead == 1L) {
            text = getString(R.string.message_book_marked_as_to_read, book.title);
        } else {
            text = getString(R.string.message_book_marked_as_read, book.title);
        }
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();

        loadData();
    }

    private void returnBook(Book book) {
        SQLiteHelper dbHelper;
        SQLiteDatabase db;
        dbHelper = new SQLiteHelper(getActivity());
        db = dbHelper.getWritableDatabase();
        BookServices.returnBook(db, book.id);
        db.close();

        String message = getString(R.string.message_book_returned, book.title);
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        loadData();
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
                .setPositiveButton(confirmText, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        returnBook(book);
                    }
                }).setNegativeButton(cancelText, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing.
            }
        }).show();
    }

    /**
     * Set the value indicating if the book list should be reloaded to false.
     */
    private void setReloadBookListToFalse() {
        VariableUtils.getInstance().setReloadBookList(false);
    }

    /**
     * Set the value indicating if the book list should be reloaded to true.
     */
    private void setReloadBookListToTrue() {
        VariableUtils.getInstance().setReloadBookList(true);
    }

    /**
     * Show the delete confirm dialog.
     *
     * @param book Book.
     */
    private void showDeleteConfirmDialog(final Book book) {
        String message = getString(R.string.message_confirm_delete_book);
        message = String.format(message, book.title);

        String cancelText = getString(R.string.message_confirm_delete_book_cancel);
        String confirmText = getString(R.string.message_confirm_delete_book_confirm);

        new AlertDialog.Builder(this.getActivity()) //
                .setTitle(R.string.title_dialog_delete_book) //
                .setMessage(message) //
                .setPositiveButton(confirmText, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AbstractBookListFragment.this.deleteBook(book);
                    }
                }).setNegativeButton(cancelText, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing.
            }
        }).show();
    }

    /**
     * Implementation of AsyncTask used to load the book list without blocking
     * the UI.
     */
    private class BookListLoadTask extends AsyncTask<Void, Void, List<ListItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);
        }

        @Override
        protected List<ListItem> doInBackground(Void... params) {
            return loadBookList();
        }

        @Override
        protected void onPostExecute(List<ListItem> result) {
            mBookListAdapter.clear();
            mBookListAdapter.addAll(result);
            mBookListAdapter.notifyDataSetChanged();


            mLoadingView.setVisibility(View.GONE);
            if (result.isEmpty()) {
                mEmptyView.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.GONE);
            } else {
                mEmptyView.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
            }

            mTextFooter.setText(getFooterText());
        }
    }
}
