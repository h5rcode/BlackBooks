package com.blackbooks.activities;

import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.adapters.AutoCompleteAdapter;
import com.blackbooks.adapters.AutoCompleteAdapter.AutoCompleteSearcher;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.model.persistent.Identifier;
import com.blackbooks.model.persistent.Publisher;
import com.blackbooks.search.GoogleBook;
import com.blackbooks.search.GoogleBooksSearcher;
import com.blackbooks.services.AuthorServices;
import com.blackbooks.services.BookServices;
import com.blackbooks.services.CategoryServices;
import com.blackbooks.services.PublisherServices;
import com.blackbooks.utils.GoogleBookUtils;
import com.blackbooks.utils.StringUtils;

/**
 * Activity used to add a book to the library.
 */
public final class BookAdd extends Activity {

	public final static String EXTRA_ISBN = "ISBN";
	private final static String BOOK_INFO = "BOOK_INFO";
	private final static int REQUEST_EDIT_AUTHORS = 0;
	private final static int REQUEST_EDIT_CATEGORIES = 1;

	private ImageButton mImageThumbnail;
	private EditText mTextTitle;
	private EditText mTextSubtitle;
	private Button mButtonEditAuthors;
	private EditText mTextIsbn;
	private EditText mTextPageCount;
	private AutoCompleteTextView mTextPublisher;
	private EditText mTextPublishedDate;
	private Button mButtonEditCategories;
	private EditText mTextDescription;
	private EditText mTextLanguage;

	private AutoCompleteAdapter<Publisher> mPublisherCompleteAdapter;

	private BookInfo mBookInfo;

	// TODO: Use ProgressBar insted of ProgressDialog (see
	// http://developer.android.com/design/building-blocks/progress.html).
	private ProgressDialog mProgressDialog;

	private boolean mValidBookInfo;

	/**
	 * Return to the book list activity.
	 * 
	 * @param view
	 *            View.
	 */
	public void cancel(View view) {
		finish();
	}

	/**
	 * Start the activity to edit the list of authors.
	 * 
	 * @param view
	 *            View.
	 */
	public void editAuthors(View view) {
		Intent intent = new Intent(this, BookAuthorsEdit.class);
		intent.putExtra(BookAuthorsEdit.EXTRA_BOOK_TITLE, mTextTitle.getText().toString());
		intent.putExtra(BookAuthorsEdit.EXTRA_AUTHOR_LIST, mBookInfo.authors);
		startActivityForResult(intent, REQUEST_EDIT_AUTHORS);
	}

	/**
	 * Start the activity to edit the list of categories.
	 * 
	 * @param view
	 *            View.
	 */
	public void editCategories(View view) {
		Intent intent = new Intent(this, BookCategoriesEdit.class);
		intent.putExtra(BookCategoriesEdit.EXTRA_BOOK_TITLE, mTextTitle.getText().toString());
		intent.putExtra(BookCategoriesEdit.EXTRA_CATEGORY_LIST, mBookInfo.categories);
		startActivityForResult(intent, REQUEST_EDIT_CATEGORIES);
	}

	/**
	 * Save the new book and finish the activity.
	 */
	public void save() {
		mValidBookInfo = true;
		readBookInfo();

		if (mValidBookInfo) {
			SQLiteHelper dbHelper = new SQLiteHelper(this);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			BookServices.saveBookInfo(db, mBookInfo);
			db.close();

			String title = mBookInfo.title;
			String message = String.format(getString(R.string.message_book_added), title);
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();

			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.book_add, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result;
		switch (item.getItemId()) {
		case R.id.bookAdd_actionSave:
			result = true;
			save();
			break;

		default:
			result = super.onOptionsItemSelected(item);
			break;
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_EDIT_AUTHORS) {
				mBookInfo.authors = (ArrayList<Author>) data.getSerializableExtra(BookAuthorsEdit.EXTRA_AUTHOR_LIST);
				setButtonEditAuthorsText();
			} else if (requestCode == REQUEST_EDIT_CATEGORIES) {
				mBookInfo.categories = (ArrayList<Category>) data.getSerializableExtra(BookCategoriesEdit.EXTRA_CATEGORY_LIST);
				setButtonEditCategoriesText();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_add);

		findViews();

		mPublisherCompleteAdapter = new AutoCompleteAdapter<Publisher>(this, android.R.layout.simple_list_item_1, new AutoCompleteSearcher<Publisher>() {

			@Override
			public ArrayList<Publisher> search(CharSequence constraint) {
				SQLiteHelper dbHelper = new SQLiteHelper(BookAdd.this);
				SQLiteDatabase db = dbHelper.getReadableDatabase();
				ArrayList<Publisher> publisherList = PublisherServices.getPublisherListByText(db, constraint.toString());
				db.close();
				return publisherList;
			}

			@Override
			public String getDisplayLabel(Publisher item) {
				return item.name;
			}
		});
		mTextPublisher.setAdapter(mPublisherCompleteAdapter);

		mProgressDialog = new ProgressDialog(BookAdd.this);
		mProgressDialog.setMessage(getString(R.string.message_searching));

		if (savedInstanceState != null && savedInstanceState.containsKey(BOOK_INFO)) {
			mBookInfo = (BookInfo) savedInstanceState.getSerializable(BOOK_INFO);
		} else {
			mBookInfo = new BookInfo();
		}

		renderBookInfo();

		handleIntent();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(BOOK_INFO, mBookInfo);
	}

	/**
	 * Find the views of the activity that will contain the book information.
	 */
	private void findViews() {
		mImageThumbnail = (ImageButton) findViewById(R.id.bookAdd_buttonThumbnail);
		mTextTitle = (EditText) findViewById(R.id.bookAdd_textTitle);
		mTextSubtitle = (EditText) findViewById(R.id.bookAdd_textSubtitle);
		mButtonEditAuthors = (Button) findViewById(R.id.bookAdd_buttonEditAuthors);
		mTextIsbn = (EditText) findViewById(R.id.bookAdd_textIsbn);
		mTextPageCount = (EditText) findViewById(R.id.bookAdd_textPageCount);
		mTextPublisher = (AutoCompleteTextView) findViewById(R.id.bookAdd_textPublisher);
		mTextPublishedDate = (EditText) findViewById(R.id.bookAdd_textPublishedDate);
		mButtonEditCategories = (Button) findViewById(R.id.bookAdd_buttonEditCategories);
		mTextDescription = (EditText) findViewById(R.id.bookAdd_textDescription);
		mTextLanguage = (EditText) findViewById(R.id.bookAdd_textLanguage);
	}

	/**
	 * Get the value from an EditText.
	 * 
	 * @param editText
	 *            EditText.
	 * @param mandatory
	 *            True if the the field is mandatory.
	 * @return The text value from the field.
	 */
	private String getEditTextValue(EditText editText, boolean mandatory) {
		String text = editText.getText().toString().trim();

		if (text.length() == 0) {
			text = null;
			if (mandatory) {
				editText.setError(getString(R.string.field_mandatory));
				if (mValidBookInfo) {
					mValidBookInfo = false;
				}
			}
		} else if (editText.getError() != null) {
			editText.setError(null);
		}
		return text;
	}

	/**
	 * Handle the intent that started the activity.
	 */
	private void handleIntent() {
		Intent intent = getIntent();
		if (intent != null) {
			if (intent.hasExtra(EXTRA_ISBN)) {
				String isbn = intent.getStringExtra(EXTRA_ISBN);
				intent.removeExtra(EXTRA_ISBN);
				new BookSearch().execute(isbn);
			}
		}
	}

	/**
	 * Validate the user input and read the book info from the view.
	 */
	private void readBookInfo() {

		String title = getEditTextValue(mTextTitle, true);
		String subtitle = getEditTextValue(mTextSubtitle, false);
		String isbn = getEditTextValue(mTextIsbn, false);
		String pageCountString = getEditTextValue(mTextPageCount, false);
		String publisherName = getEditTextValue(mTextPublisher, false);
		String publishedDate = getEditTextValue(mTextPublishedDate, false);
		String description = getEditTextValue(mTextDescription, false);

		mBookInfo.title = title;
		mBookInfo.subtitle = subtitle;
		if (pageCountString != null && StringUtils.isInteger(pageCountString)) {
			mBookInfo.pageCount = Long.valueOf(pageCountString);
		}
		mBookInfo.publishedDate = publishedDate;
		mBookInfo.description = description;

		SQLiteHelper dbHelper = new SQLiteHelper(BookAdd.this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		ArrayList<Author> authors = new ArrayList<Author>();
		for (Author author : mBookInfo.authors) {
			Author authorDb = AuthorServices.getAuthorByCriteria(db, author);
			if (authorDb != null) {
				authors.add(authorDb);
			} else {
				authors.add(author);
			}
		}
		mBookInfo.authors = authors;

		if (publisherName != null) {
			Publisher publisher = new Publisher();
			publisher.name = publisherName;

			Publisher publisherDb = PublisherServices.getPublisherByCriteria(db, publisher);
			if (publisherDb != null) {
				mBookInfo.publisher = publisherDb;
			} else {
				mBookInfo.publisher = publisher;
			}
		}

		ArrayList<Identifier> identifiers = new ArrayList<Identifier>();
		if (isbn != null) {
			Identifier identifier = new Identifier();
			identifier.identifier = isbn;
			identifiers.add(identifier);
		}
		mBookInfo.identifiers = identifiers;

		ArrayList<Category> categories = new ArrayList<Category>();
		for (Category category : mBookInfo.categories) {
			Category categoryDb = CategoryServices.getCategoryByCriteria(db, category);
			if (categoryDb != null) {
				categories.add(categoryDb);
			} else {
				categories.add(category);
			}
		}
		mBookInfo.categories = categories;

		db.close();
	}

	/**
	 * Update the views of the activity using the book information.
	 */
	private void renderBookInfo() {
		if (mBookInfo.thumbnail != null && mBookInfo.thumbnail.length > 0) {
			Bitmap bitmap = BitmapFactory.decodeByteArray(mBookInfo.thumbnail, 0, mBookInfo.thumbnail.length);
			mImageThumbnail.setImageBitmap(bitmap);
		}
		mTextTitle.setText(mBookInfo.title);
		setButtonEditAuthorsText();
		if (mBookInfo.identifiers.size() > 0) {
			mTextIsbn.setText(mBookInfo.identifiers.get(0).identifier);
		}
		if (mBookInfo.pageCount != null) {
			mTextPageCount.setText(mBookInfo.pageCount.toString());
		}
		mTextPublisher.setText(mBookInfo.publisher.name);
		mTextPublishedDate.setText(mBookInfo.publishedDate);
		setButtonEditCategoriesText();
		mTextDescription.setText(mBookInfo.description);
		mTextLanguage.setText(null); // TODO: Handle the language.
	}

	/**
	 * Set the text of the "Edit author(s)" button.
	 */
	private void setButtonEditAuthorsText() {
		if (mBookInfo.authors == null || mBookInfo.authors.size() == 0) {
			mButtonEditAuthors.setText(getString(R.string.button_edit_authors));
		} else {
			String authors = StringUtils.joinAuthorNameList(mBookInfo.authors, ", ");
			mButtonEditAuthors.setText(authors);
		}
	}

	/**
	 * Set the text of the "Edit categories" button.
	 */
	private void setButtonEditCategoriesText() {
		if (mBookInfo.categories == null || mBookInfo.categories.size() == 0) {
			mButtonEditCategories.setText(getString(R.string.button_edit_categories));
		} else {
			String categories = StringUtils.joinCategoryNameList(mBookInfo.categories, ", ");
			mButtonEditCategories.setText(categories);
		}
	}

	/**
	 * Task performing the search for a book.
	 */
	private final class BookSearch extends AsyncTask<String, Void, GoogleBook> {

		private String errorMessage = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.show();
		}

		@Override
		protected GoogleBook doInBackground(String... params) {
			String barCode = params[0];
			GoogleBook book = null;
			try {
				book = GoogleBooksSearcher.search(barCode);
			} catch (ClientProtocolException e) {
				errorMessage = getString(R.string.error_connection_problem);
			} catch (JSONException e) {
				errorMessage = getString(R.string.error_json_exception);
			} catch (URISyntaxException e) {
				errorMessage = getString(R.string.error_uri_syntax);
			} catch (UnknownHostException e) {
				errorMessage = getString(R.string.error_connection_problem);
			}
			return book;
		}

		@Override
		protected void onPostExecute(GoogleBook result) {
			super.onPostExecute(result);
			if (result != null) {
				mBookInfo = GoogleBookUtils.toBookInfo(result);
				renderBookInfo();
			} else {
				if (errorMessage != null) {
					Toast.makeText(BookAdd.this, errorMessage, Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(BookAdd.this, getString(R.string.message_no_result), Toast.LENGTH_LONG).show();
				}
			}

			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
		}
	}
}
