package com.blackbooks.fragments;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.activities.BookAuthorsEdit;
import com.blackbooks.activities.BookCategoriesEdit;
import com.blackbooks.adapters.AutoCompleteAdapter;
import com.blackbooks.adapters.AutoCompleteAdapter.AutoCompleteSearcher;
import com.blackbooks.adapters.Language;
import com.blackbooks.adapters.LanguagesAdapter;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.model.persistent.Identifier;
import com.blackbooks.model.persistent.Publisher;
import com.blackbooks.search.BookSearcher;
import com.blackbooks.services.AuthorServices;
import com.blackbooks.services.BookServices;
import com.blackbooks.services.CategoryServices;
import com.blackbooks.services.PublisherServices;
import com.blackbooks.utils.StringUtils;
import com.blackbooks.utils.VariableUtils;

/**
 * Fragment to add a new book to the library.
 */
public class BookAddFragment extends Fragment {

	public static final String ARG_ISBN = "ARG_ISBN";

	private static final String ARG_BOO_ID = "ARG_BOO_ID";
	private static final String ARG_MODE = "ARG_MODE";

	private static final int MODE_ADD = 1;
	private static final int MODE_EDIT = 2;

	private static final String BOOK_INFO = "BOOK_INFO";

	private static final int REQUEST_EDIT_AUTHORS = 1;
	private static final int REQUEST_EDIT_CATEGORIES = 2;

	private SQLiteHelper mDbHelper;

	private ProgressBar mProgressBar;
	private ScrollView mScrollView;
	private ImageView mImageThumbnail;
	private EditText mTextTitle;
	private EditText mTextSubtitle;
	private Spinner mSpinnerLanguage;
	private Button mButtonEditAuthors;
	private EditText mTextIsbn;
	private EditText mTextPageCount;
	private AutoCompleteTextView mTextPublisher;
	private EditText mTextPublishedDate;
	private Button mButtonEditCategories;
	private EditText mTextDescription;

	private LanguagesAdapter mLanguagesAdapter;
	private AutoCompleteAdapter<Publisher> mPublisherCompleteAdapter;

	private BookInfo mBookInfo;

	private boolean mIsSearching;
	private BookAddListener mBookAddListener;

	private boolean mValidBookInfo;

	/**
	 * Create a new instance of BookAddFragment. The parameter {@code isbn} can
	 * be used to initiate an internet search for some info.
	 * 
	 * @param isbn
	 *            ISBN number (can be null).
	 * @return BookAddFragment.
	 */
	public static BookAddFragment newInstanceAddMode(String isbn) {
		BookAddFragment instance = new BookAddFragment();

		Bundle args = new Bundle();
		args.putInt(ARG_MODE, MODE_ADD);
		if (isbn != null) {
			args.putString(ARG_ISBN, isbn);
		}

		instance.setArguments(args);
		return instance;
	}

	/**
	 * Create a new instance of BookAddFragment in edit mode. The parameter
	 * {@code booId} represents the id of the edited book.
	 * 
	 * @param booId
	 *            Id of the book to edit.
	 * @return BookAddFragment.
	 */
	public static BookAddFragment newInstanceEditMode(long booId) {
		BookAddFragment instance = new BookAddFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_MODE, MODE_EDIT);
		args.putLong(ARG_BOO_ID, booId);
		instance.setArguments(args);
		return instance;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof BookAddListener) {
			mBookAddListener = (BookAddListener) activity;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
	}

	/**
	 * Start the activity to edit the list of authors.
	 * 
	 * @param view
	 *            View.
	 */
	public void editAuthors(View view) {
		Intent intent = new Intent(this.getActivity(), BookAuthorsEdit.class);
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
		Intent intent = new Intent(this.getActivity(), BookCategoriesEdit.class);
		intent.putExtra(BookCategoriesEdit.EXTRA_BOOK_TITLE, mTextTitle.getText().toString());
		intent.putExtra(BookCategoriesEdit.EXTRA_CATEGORY_LIST, mBookInfo.categories);
		startActivityForResult(intent, REQUEST_EDIT_CATEGORIES);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.thumbnail_edit, menu);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.book_add, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		boolean result;

		switch (item.getItemId()) {
		case R.id.thumbnailEdit_actionRemove:
			mBookInfo.thumbnail = null;
			mBookInfo.smallThumbnail = null;
			setImageThumbnail();
			result = true;
			break;

		default:
			result = super.onContextItemSelected(item);
			break;
		}

		return result;
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

	/**
	 * Save the new book and finish the activity.
	 */
	public void save() {
		mValidBookInfo = true;
		readBookInfo();

		if (mValidBookInfo) {
			SQLiteHelper dbHelper = new SQLiteHelper(this.getActivity());
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			BookServices.saveBookInfo(db, mBookInfo);
			db.close();

			VariableUtils.getInstance().setReloadBookList(true);

			String title = mBookInfo.title;
			String message = String.format(getString(R.string.message_book_added), title);
			Toast.makeText(this.getActivity(), message, Toast.LENGTH_LONG).show();

			if (mBookAddListener != null) {
				mBookAddListener.onSaved();
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_book_add, container, false);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		findViews();

		mDbHelper = new SQLiteHelper(this.getActivity());

		if (mIsSearching) {
			mProgressBar.setVisibility(View.VISIBLE);
			mScrollView.setVisibility(View.GONE);
		} else {
			mProgressBar.setVisibility(View.GONE);
			mScrollView.setVisibility(View.VISIBLE);
		}

		registerForContextMenu(mImageThumbnail);
		mButtonEditAuthors.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				BookAddFragment.this.editAuthors(v);
			}
		});
		mButtonEditCategories.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				BookAddFragment.this.editCategories(v);

			}
		});
		mLanguagesAdapter = new LanguagesAdapter(this.getActivity());
		mSpinnerLanguage.setAdapter(mLanguagesAdapter);
		mSpinnerLanguage.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				mBookInfo.languageCode = (String) arg1.getTag();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		mPublisherCompleteAdapter = new AutoCompleteAdapter<Publisher>(this.getActivity(), android.R.layout.simple_list_item_1,
				new AutoCompleteSearcher<Publisher>() {

					@Override
					public ArrayList<Publisher> search(CharSequence constraint) {
						SQLiteDatabase db = mDbHelper.getReadableDatabase();
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

		if (savedInstanceState != null && savedInstanceState.containsKey(BOOK_INFO)) {
			mBookInfo = (BookInfo) savedInstanceState.getSerializable(BOOK_INFO);
		} else if (mBookInfo == null) {
			mBookInfo = new BookInfo();
		}

		handleArguments();

		renderBookInfo();
	}

	/**
	 * Handle the arguments of the fragment.
	 */
	private void handleArguments() {
		Bundle args = getArguments();
		int mode = args.getInt(ARG_MODE);

		switch (mode) {
		case MODE_ADD:
			String isbn = null;
			if (args != null) {
				isbn = args.getString(ARG_ISBN);
				args.remove(ARG_ISBN);
			}
			if (isbn != null) {
				new BookSearch().execute(isbn);
			}
			break;

		case MODE_EDIT:
			long bookId = args.getLong(ARG_BOO_ID);
			SQLiteDatabase db = mDbHelper.getReadableDatabase();
			mBookInfo = BookServices.getBookInfo(db, bookId);
			db.close();

			String title = getString(R.string.title_activity_book_edit);
			this.getActivity().setTitle(String.format(title, mBookInfo.title));
			break;

		default:
			throw new IllegalStateException("Argument " + ARG_MODE + " not set.");
		}

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(BOOK_INFO, mBookInfo);
	}

	/**
	 * Find the views of the activity that will contain the book information.
	 */
	private void findViews() {
		View view = getView();
		mProgressBar = (ProgressBar) view.findViewById(R.id.bookAdd_progressBar);
		mScrollView = (ScrollView) view.findViewById(R.id.bookAdd_scrollView);
		mImageThumbnail = (ImageView) view.findViewById(R.id.bookAdd_buttonThumbnail);
		mTextTitle = (EditText) view.findViewById(R.id.bookAdd_textTitle);
		mTextSubtitle = (EditText) view.findViewById(R.id.bookAdd_textSubtitle);
		mSpinnerLanguage = (Spinner) view.findViewById(R.id.bookAdd_spinnerLanguage);
		mButtonEditAuthors = (Button) view.findViewById(R.id.bookAdd_buttonEditAuthors);
		mTextIsbn = (EditText) view.findViewById(R.id.bookAdd_textIsbn);
		mTextPageCount = (EditText) view.findViewById(R.id.bookAdd_textPageCount);
		mTextPublisher = (AutoCompleteTextView) view.findViewById(R.id.bookAdd_textPublisher);
		mTextPublishedDate = (EditText) view.findViewById(R.id.bookAdd_textPublishedDate);
		mButtonEditCategories = (Button) view.findViewById(R.id.bookAdd_buttonEditCategories);
		mTextDescription = (EditText) view.findViewById(R.id.bookAdd_textDescription);
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
		mBookInfo.languageCode = ((Language) mSpinnerLanguage.getSelectedItem()).getCode();
		if (pageCountString != null && StringUtils.isInteger(pageCountString)) {
			mBookInfo.pageCount = Long.valueOf(pageCountString);
		}
		mBookInfo.publishedDate = publishedDate;
		mBookInfo.description = description;

		SQLiteDatabase db = mDbHelper.getReadableDatabase();

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
		setImageThumbnail();
		mTextTitle.setText(mBookInfo.title);
		mTextSubtitle.setText(mBookInfo.subtitle);
		int languageToSelect = mLanguagesAdapter.getPosition(mBookInfo.languageCode);
		if (languageToSelect < 0) {
			languageToSelect = 0;
		}
		mSpinnerLanguage.setSelection(languageToSelect);

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
	 * Displays the thumbnail if there is one.
	 */
	private void setImageThumbnail() {
		Bitmap bitmap;
		if (mBookInfo.thumbnail != null && mBookInfo.thumbnail.length > 0) {
			bitmap = BitmapFactory.decodeByteArray(mBookInfo.thumbnail, 0, mBookInfo.thumbnail.length);
		} else {
			bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_undefined_thumbnail);
		}
		mImageThumbnail.setImageBitmap(bitmap);
	}

	/**
	 * An activity hosting a {@link BookAddFragment} should implement this
	 * interface to be notified when the user saves.
	 */
	public interface BookAddListener {

		/**
		 * Called when the user saves the book.
		 */
		void onSaved();
	}

	/**
	 * Task performing the search for a book.
	 */
	private final class BookSearch extends AsyncTask<String, Void, BookInfo> {

		private String errorMessage = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mIsSearching = true;
			mProgressBar.setVisibility(View.VISIBLE);
			mScrollView.setVisibility(View.GONE);
		}

		@Override
		protected BookInfo doInBackground(String... params) {
			String barCode = params[0];
			BookInfo book = null;
			try {
				book = BookSearcher.search(barCode);
			} catch (ClientProtocolException e) {
				errorMessage = getString(R.string.error_connection_problem);
			} catch (JSONException e) {
				errorMessage = getString(R.string.error_json_exception);
			} catch (URISyntaxException e) {
				errorMessage = getString(R.string.error_uri_syntax);
			} catch (UnknownHostException e) {
				errorMessage = getString(R.string.error_connection_problem);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return book;
		}

		@Override
		protected void onPostExecute(BookInfo result) {
			super.onPostExecute(result);
			if (result != null) {
				mBookInfo = result;
				renderBookInfo();
			} else {
				if (errorMessage != null) {
					Toast.makeText(BookAddFragment.this.getActivity(), errorMessage, Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(BookAddFragment.this.getActivity(), getString(R.string.message_no_result), Toast.LENGTH_LONG).show();
				}
			}

			mIsSearching = false;
			mProgressBar.setVisibility(View.GONE);
			mScrollView.setVisibility(View.VISIBLE);
		}
	}
}
