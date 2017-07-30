package com.blackbooks.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.fragments.dialogs.DuplicateBooksDialog;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.services.BookService;
import com.blackbooks.utils.Commons;
import com.blackbooks.utils.IsbnUtils;
import com.blackbooks.utils.Pic2ShopUtils;

import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

/**
 * Activity to start an ISBN lookup operation on the Internet. The activity can
 * get the ISBN to used to do the lookup either from the user interface or from
 * a bar code scan. Start the activity with an intent having the extra
 * {@link #EXTRA_SCAN} set to <code>true</code>.
 */
public final class IsbnLookupActivity extends FragmentActivity implements DuplicateBooksDialog.DuplicateBooksListener {

    /**
     * A boolean extra used to initiate a bar code scan if set to
     * <code>true</code>.
     */
    public static final String EXTRA_SCAN = "EXTRA_SCAN";

    private static final String STATE_ISBN_SCANNED = "STATE_ISBN_SCANNED";
    private static final String TAG_DUPLICATE_BOOKS_DIALOG = "TAG_DUPLICATE_BOOKS_DIALOG";

    private EditText mTextIsbn;
    private TextView mTextStatus;
    private MenuItem mMenuItemLookup;
    private boolean mEnableLookup;
    private boolean mIsbnScanned;

    @Inject
    BookService bookService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_isbn_lookup);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mTextIsbn = (EditText) findViewById(R.id.isbnEnter_textIsbn);
        mTextStatus = (TextView) findViewById(R.id.isbnEnter_textStatus);

        mTextIsbn.addTextChangedListener(new IsbnValidator());

        Intent intent = getIntent();
        // Do not start the scan if the activity has been recreated
        // (savedInstance != null).
        if (intent != null && savedInstanceState == null) {
            Bundle extras = intent.getExtras();
            if (extras != null && extras.getBoolean(EXTRA_SCAN)) {
                Intent scanIntent = new Intent(Pic2ShopUtils.ACTION);
                startActivityForResult(scanIntent, Pic2ShopUtils.REQUEST_CODE_SCAN);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Pic2ShopUtils.REQUEST_CODE_SCAN) {
            if (resultCode == RESULT_OK) {
                String barCode = data.getStringExtra(Pic2ShopUtils.BARCODE);
                mIsbnScanned = true;

                if (IsbnUtils.isValidIsbn(barCode)) {
                    mTextIsbn.setText(barCode);
                    checkIsbnAndStartSearch(barCode);
                } else {
                    String message = getString(R.string.message_invalid_isbn);
                    message = String.format(message, barCode);
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                    finishIfIsbnScanned();
                }
            } else {
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.isbn_enter, menu);
        mMenuItemLookup = menu.findItem(R.id.isbnEnter_actionLookup);
        toggleMenuItemLookup();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        boolean result;
        switch (item.getItemId()) {
            case R.id.isbnEnter_actionLookup:
                String isbn = mTextIsbn.getText().toString();
                checkIsbnAndStartSearch(isbn);
                result = true;
                break;

            case android.R.id.home:
                result = true;
                finish();
                break;

            default:
                result = super.onOptionsItemSelected(item);
                break;
        }

        return result;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_ISBN_SCANNED, mIsbnScanned);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mIsbnScanned = savedInstanceState.getBoolean(STATE_ISBN_SCANNED);
    }

    /**
     * Check if there are books with the same ISBN in the library. If there are, show a
     * {@link com.blackbooks.fragments.dialogs.DuplicateBooksDialog}. Otherwise, start the
     * search immediately.
     *
     * @param isbn ISBN.
     */
    private void checkIsbnAndStartSearch(final String isbn) {
        List<Book> bookList = bookService.getBookListByIsbn(isbn);

        if (bookList.isEmpty()) {
            startIsbnSearch(isbn);
        } else {
            FragmentManager fm = IsbnLookupActivity.this.getSupportFragmentManager();
            DuplicateBooksDialog fragment = DuplicateBooksDialog.newInstance(isbn, bookList);
            fragment.show(fm, TAG_DUPLICATE_BOOKS_DIALOG);
        }
    }

    /**
     * Launches the BookAdd activity to perform the search using the entered
     * ISBN.
     *
     * @param isbn ISBN.
     */
    private void startIsbnSearch(String isbn) {
        Intent i = new Intent(IsbnLookupActivity.this, BookEditActivity.class);
        i.putExtra(BookEditActivity.EXTRA_MODE, BookEditActivity.MODE_ADD);
        i.putExtra(BookEditActivity.EXTRA_ISBN, isbn);
        IsbnLookupActivity.this.startActivity(i);
        finishIfIsbnScanned();
    }

    /**
     * Finish the activity if the ISBN was scanned.
     */
    private void finishIfIsbnScanned() {
        if (mIsbnScanned) {
            finish();
        }
    }

    /**
     * Enable or disable the lookup item of the options menu.
     */
    private void toggleMenuItemLookup() {
        if (mMenuItemLookup != null) {
            mMenuItemLookup.setEnabled(mEnableLookup);
            mMenuItemLookup.getIcon().setAlpha(mEnableLookup ? Commons.ALPHA_ENABLED : Commons.ALPHA_DISABLED);
        }
    }

    @Override
    public void onContinue(String isbn) {
        startIsbnSearch(isbn);
    }

    @Override
    public void onCancel() {
        finishIfIsbnScanned();
    }

    /**
     * A text watcher that checks the entered ISBN.
     */
    private final class IsbnValidator implements TextWatcher {

        @Override
        public void afterTextChanged(Editable s) {
            String isbn = s.toString();

            mEnableLookup = false;
            if (!Pattern.matches("[[0-9][xX]]*", isbn)) {
                mTextIsbn.setError(getString(R.string.message_isbn_search_info_format));
                mTextStatus.setText(getString(R.string.message_isbn_search_invalid_format));
            } else {
                mTextIsbn.setError(null);
                int length = isbn.length();

                if (length < 13) {
                    if (length == 0) {
                        mTextStatus.setText(null);
                    } else if (length == 10) {
                        if (IsbnUtils.isValidIsbn10(isbn)) {
                            mTextStatus.setText(null);
                            mEnableLookup = true;
                        } else {
                            mTextStatus.setText(getString(R.string.message_isbn_search_invalid_isbn10));
                        }

                    } else {
                        mTextStatus.setText(getString(R.string.message_isbn_search_too_short));
                    }
                } else if (length == 13) {
                    if (IsbnUtils.isValidIsbn13(isbn)) {
                        mTextStatus.setText(null);
                        mEnableLookup = true;
                    } else {
                        mTextStatus.setText(getString(R.string.message_isbn_search_invalid_isbn13));
                    }
                }
            }

            toggleMenuItemLookup();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Do nothing.
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Do nothing.
        }
    }
}
