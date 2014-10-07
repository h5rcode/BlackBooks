package com.blackbooks.activities;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.adapters.AutoCompleteAdapter;
import com.blackbooks.adapters.AutoCompleteAdapter.AutoCompleteSearcher;
import com.blackbooks.adapters.EditableArrayAdapter;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.services.AuthorServices;

/**
 * Activity to edit the authors of a book.
 */
public class BookAuthorsEdit extends Activity {

	public static final String EXTRA_BOOK_TITLE = "EXTRA_BOOK_TITLE";
	public static final String EXTRA_AUTHOR_LIST = "EXTRA_AUTHOR_LIST";

	private static final String BOOK_TITLE = "BOOK_TITLE";
	private static final String AUTHOR_LIST = "AUTHOR_LIST";

	private String mBookTitle;
	private ArrayList<Author> mAuthorList;
	private LinkedHashMap<String, Author> mAuthorMap;
	private AutoCompleteAdapter<Author> mAutoCompleteAdapter;
	private EditableArrayAdapter<Author> mAuthorArrayAdapter;

	private TextView mTextInfo;
	private AutoCompleteTextView mTextAuthor;
	private ListView mListAuthors;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.book_authors_edit, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		boolean result;
		switch (item.getItemId()) {
		case R.id.bookAuthorsEdit_actionSave:
			addAuthorList();
			result = true;
			break;

		default:
			result = false;
			break;
		}
		return result;
	}

	/**
	 * Add the author to the list.
	 * 
	 * @param view
	 *            View.
	 */
	public void addAuthor(View view) {
		String authorName = mTextAuthor.getText().toString().trim();

		if (authorName.length() == 0) {
			mTextAuthor.setError(getString(R.string.message_author_missing));
		} else {
			if (mAuthorMap.containsKey(authorName)) {
				String message = getString(R.string.message_author_already_present);
				message = String.format(message, authorName);
				mTextAuthor.setError(message);
			} else {
				Author a = new Author();
				a.name = authorName;

				SQLiteHelper dbHelper = new SQLiteHelper(this);
				SQLiteDatabase db = dbHelper.getReadableDatabase();
				Author author = AuthorServices.getAuthorByCriteria(db, a);
				db.close();
				if (author != null) {
					a = author;
				}

				mAuthorMap.put(authorName, a);
				mAuthorArrayAdapter.add(a);

				String message = getString(R.string.message_author_added);
				message = String.format(message, authorName);
				Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

				mTextAuthor.setText(null);
				mTextAuthor.setError(null);
			}
		}
	}

	/**
	 * Remove an author from the list.
	 * 
	 * @param view
	 *            View.
	 */
	public void removeAuthor(View view) {
		Author author = (Author) view.getTag();
		mAuthorMap.remove(author.name);
		mAuthorArrayAdapter.remove(author);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_book_authors_edit);

		if (savedInstanceState != null) {
			mBookTitle = savedInstanceState.getString(BOOK_TITLE);
			mAuthorList = (ArrayList<Author>) savedInstanceState.getSerializable(AUTHOR_LIST);
		} else {
			Intent intent = getIntent();
			mBookTitle = intent.getStringExtra(EXTRA_BOOK_TITLE);
			mAuthorList = (ArrayList<Author>) intent.getSerializableExtra(EXTRA_AUTHOR_LIST);
		}
		if (mAuthorList == null) {
			mAuthorList = new ArrayList<Author>();
		}

		mAuthorMap = new LinkedHashMap<String, Author>();
		for (Author author : mAuthorList) {
			mAuthorMap.put(author.name, author);
		}

		mAutoCompleteAdapter = new AutoCompleteAdapter<Author>(this, android.R.layout.simple_list_item_1, new AutoCompleteSearcher<Author>() {

			@Override
			public ArrayList<Author> search(CharSequence constraint) {
				SQLiteHelper mDbHelper = new SQLiteHelper(BookAuthorsEdit.this);
				SQLiteDatabase db = mDbHelper.getReadableDatabase();
				ArrayList<Author> authorList = AuthorServices.getAuthorListByText(db, constraint.toString());
				db.close();
				return authorList;
			}

			@Override
			public String getDisplayLabel(Author item) {
				return item.name;
			}
		});

		mAuthorArrayAdapter = new EditableArrayAdapter<Author>(this, R.id.bookAuthorsEdit_authorList, R.layout.list_authors_item_author, R.id.item_author_name,
				R.id.item_author_button_remove, mAuthorList) {

			@Override
			protected String getDisplayLabel(Author object) {
				return object.name;
			}
		};

		mTextInfo = (TextView) findViewById(R.id.bookAuthorsEdit_textInfo);
		mTextAuthor = (AutoCompleteTextView) findViewById(R.id.bookAuthorsEdit_textAuthor);
		mListAuthors = (ListView) findViewById(R.id.bookAuthorsEdit_authorList);

		if (mBookTitle == null || mBookTitle.trim().equals("")) {
			mTextInfo.setText(getString(R.string.text_info_edit_authors_untitled_book));
		} else {
			String message = getString(R.string.text_info_edit_authors);
			message = String.format(message, mBookTitle.trim());
			mTextInfo.setText(message);
		}
		mTextAuthor.setAdapter(mAutoCompleteAdapter);
		mListAuthors.setAdapter(mAuthorArrayAdapter);

		TextView emptyText = (TextView) findViewById(android.R.id.empty);
		mListAuthors.setEmptyView(emptyText);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(BOOK_TITLE, mBookTitle);
		outState.putSerializable(AUTHOR_LIST, mAuthorList);
	}

	/**
	 * Finish the activity and add the list of authors to the result of the
	 * activity.
	 */
	private void addAuthorList() {
		Intent intent = new Intent();
		ArrayList<Author> authorList = new ArrayList<Author>(mAuthorMap.values());
		intent.putExtra(EXTRA_AUTHOR_LIST, authorList);
		setResult(RESULT_OK, intent);
		finish();
	}
}
