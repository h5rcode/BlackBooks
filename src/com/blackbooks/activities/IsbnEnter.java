package com.blackbooks.activities;

import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.blackbooks.R;
import com.blackbooks.helpers.IsbnHelper;

/**
 * Activity to type an ISBN number.
 */
public class IsbnEnter extends Activity {

	private final static int ALPHA_DISABLED = 75;

	private EditText textIsbn;
	private MenuItem menuItemLookup;
	private TextView textStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_isbn_enter);

		textIsbn = (EditText) findViewById(R.id.isbnEnter_textIsbn);
		textStatus = (TextView) findViewById(R.id.isbnEnter_textStatus);

		textIsbn.setRawInputType(Configuration.KEYBOARD_QWERTY);
		textIsbn.addTextChangedListener(new IsbnValidator());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.isbn_enter, menu);
		menuItemLookup = menu.findItem(R.id.isbnEnter_actionLookup);
		menuItemLookup.getIcon().setAlpha(ALPHA_DISABLED);
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
		String isbn = textIsbn.getText().toString();
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
		Intent i = new Intent(IsbnEnter.this, BookAdd.class);
		i.putExtra(BookAdd.EXTRA_ISBN, isbn);
		IsbnEnter.this.startActivity(i);
	}

	/**
	 * A text watcher that checks the entered ISBN.
	 */
	private final class IsbnValidator implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			String isbn = s.toString();

			boolean enableSearch = false;
			if (!Pattern.matches("[[0-9][xX]]*", isbn)) {
				textIsbn.setError(getString(R.string.message_isbn_search_info_format));
				textStatus.setText(getString(R.string.message_isbn_search_invalid_format));
			} else {
				textIsbn.setError(null);
				int length = isbn.length();

				if (length < 13) {
					if (length == 0) {
						textStatus.setText(null);
					} else if (length == 10) {
						if (IsbnHelper.isValidIsbn10(isbn)) {
							textStatus.setText(null);
							enableSearch = true;
						} else {
							textStatus.setText(getString(R.string.message_isbn_search_invalid_isbn10));
						}

					} else {
						textStatus.setText(getString(R.string.message_isbn_search_too_short));
					}
				} else if (length == 13) {
					if (IsbnHelper.isValidIsbn13(isbn)) {
						textStatus.setText(null);
						enableSearch = true;
					} else {
						textStatus.setText(getString(R.string.message_isbn_search_invalid_isbn13));
					}
				}
			}

			menuItemLookup.setEnabled(enableSearch);
			menuItemLookup.getIcon().setAlpha(enableSearch ? 255 : ALPHA_DISABLED);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
	}
}
