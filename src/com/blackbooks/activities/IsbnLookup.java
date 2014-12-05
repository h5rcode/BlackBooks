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
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.helpers.IsbnHelper;
import com.blackbooks.helpers.Pic2ShopHelper;
import com.blackbooks.utils.Commons;

/**
 * Activity to start an ISBN lookup operation on the Internet. The activity can
 * get the ISBN to used to do the lookup either from the user interface or from
 * a bar code scan. Start the activity with an intent having the extra
 * {@link #EXTRA_SCAN} set to <code>true</code>.
 */
public class IsbnLookup extends Activity {

	/**
	 * A boolean extra used to initiate a bar code scan if set to
	 * <code>true</code>.
	 */
	public static final String EXTRA_SCAN = "EXTRA_SCAN";

	private EditText mTextIsbn;
	private TextView mTextStatus;
	private MenuItem mMenuItemLookup;
	private boolean mEnableLookup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
		setContentView(R.layout.activity_isbn_lookup);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		mTextIsbn = (EditText) findViewById(R.id.isbnEnter_textIsbn);
		mTextStatus = (TextView) findViewById(R.id.isbnEnter_textStatus);

		mTextIsbn.setRawInputType(Configuration.KEYBOARD_QWERTY);
		mTextIsbn.addTextChangedListener(new IsbnValidator());

		Intent intent = getIntent();
		// Do not start the scan if the activity has been recreated
		// (savedInstance != null).
		if (intent != null && savedInstanceState == null) {
			Bundle extras = intent.getExtras();
			if (extras != null && extras.getBoolean(EXTRA_SCAN)) {
				Intent scanIntent = new Intent(Pic2ShopHelper.ACTION);
				startActivityForResult(scanIntent, Pic2ShopHelper.REQUEST_CODE_SCAN);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Pic2ShopHelper.REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
			String barCode = data.getStringExtra(Pic2ShopHelper.BARCODE);

			if (IsbnHelper.isValidIsbn(barCode)) {
				Intent i = new Intent(this, BookEdit.class);
				i.putExtra(BookEdit.EXTRA_MODE, BookEdit.MODE_ADD);
				i.putExtra(BookEdit.EXTRA_ISBN, barCode);
				this.startActivity(i);
			} else {
				String message = getString(R.string.message_invalid_isbn);
				message = String.format(message, barCode);
				Toast.makeText(this, message, Toast.LENGTH_LONG).show();
			}
		}
		finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
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
		Intent i = new Intent(IsbnLookup.this, BookEdit.class);
		i.putExtra(BookEdit.EXTRA_MODE, BookEdit.MODE_ADD);
		i.putExtra(BookEdit.EXTRA_ISBN, isbn);
		IsbnLookup.this.startActivity(i);
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
