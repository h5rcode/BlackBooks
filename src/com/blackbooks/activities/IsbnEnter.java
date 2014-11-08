package com.blackbooks.activities;

import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.blackbooks.R;
import com.blackbooks.helpers.IsbnHelper;

/**
 * Activity to enter an ISBN number and start a search for information on the
 * internet.
 */
public class IsbnEnter extends Activity {

	private final static int ALPHA_ENABLED = 255;
	private final static int ALPHA_DISABLED = 75;

	private EditText mTextIsbn;
	private TextView mTextStatus;
	private MenuItem mMenuItemLookup;
	private boolean mEnableLookup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_isbn_enter);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		mTextIsbn = (EditText) findViewById(R.id.isbnEnter_textIsbn);
		mTextStatus = (TextView) findViewById(R.id.isbnEnter_textStatus);

		mTextIsbn.setRawInputType(Configuration.KEYBOARD_QWERTY);
		mTextIsbn.addTextChangedListener(new IsbnValidator());
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
			search();
			result = true;
			break;

		case android.R.id.home:
			result = true;
			NavUtils.navigateUpFromSameTask(this);
			break;

		default:
			result = super.onOptionsItemSelected(item);
			break;
		}

		return result;
	}

	/**
	 * Get the entered ISBN number and start the search.
	 */
	public void search() {
		String isbn = mTextIsbn.getText().toString();
		startIsbnSearch(isbn);
	}

	/**
	 * Launches the BookAdd activity to perform the search using the entered
	 * ISBN.
	 * 
	 * @param isbn
	 *            ISBN.
	 */
	private void startIsbnSearch(String isbn) {
		Intent i = new Intent(IsbnEnter.this, BookEdit.class);
		i.putExtra(BookEdit.EXTRA_MODE, BookEdit.MODE_ADD);
		i.putExtra(BookEdit.EXTRA_ISBN, isbn);
		IsbnEnter.this.startActivity(i);
	}

	/**
	 * Enable or disable the lookup item of the options menu.
	 */
	private void toggleMenuItemLookup() {
		if (mMenuItemLookup != null) {
			mMenuItemLookup.setEnabled(mEnableLookup);
			mMenuItemLookup.getIcon().setAlpha(mEnableLookup ? ALPHA_ENABLED : ALPHA_DISABLED);
		}
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
						if (IsbnHelper.isValidIsbn10(isbn)) {
							mTextStatus.setText(null);
							mEnableLookup = true;
						} else {
							mTextStatus.setText(getString(R.string.message_isbn_search_invalid_isbn10));
						}

					} else {
						mTextStatus.setText(getString(R.string.message_isbn_search_too_short));
					}
				} else if (length == 13) {
					if (IsbnHelper.isValidIsbn13(isbn)) {
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
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
	}
}
