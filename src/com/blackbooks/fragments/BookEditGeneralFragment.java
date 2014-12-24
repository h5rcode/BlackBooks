package com.blackbooks.fragments;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.activities.BookAuthorsEditActivity;
import com.blackbooks.activities.BookCategoriesEditActivity;
import com.blackbooks.adapters.AutoCompleteAdapter;
import com.blackbooks.adapters.AutoCompleteAdapter.AutoCompleteSearcher;
import com.blackbooks.adapters.LanguagesAdapter;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.nonpersistent.Language;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.model.persistent.Publisher;
import com.blackbooks.model.persistent.Series;
import com.blackbooks.services.AuthorServices;
import com.blackbooks.services.CategoryServices;
import com.blackbooks.services.PublisherServices;
import com.blackbooks.services.SeriesServices;
import com.blackbooks.utils.BitmapUtils;
import com.blackbooks.utils.Commons;
import com.blackbooks.utils.FileUtils;
import com.blackbooks.utils.LogUtils;
import com.blackbooks.utils.StringUtils;

/**
 * Fragment to edit the general information of a book.
 */
public class BookEditGeneralFragment extends Fragment {

	private static final String ARG_BOOK = "ARG_BOOK";
	private static final String IMAGE_DISPLAY_FRAGMENT_TAG = "IMAGE_DISPLAY_FRAGMENT_TAG";

	private static final int REQUEST_EDIT_AUTHORS = 1;
	private static final int REQUEST_EDIT_CATEGORIES = 2;
	private static final int REQUEST_PICK_IMAGE = 3;
	private static final int REQUEST_TAKE_PICTURE = 4;

	private SQLiteHelper mDbHelper;

	private ImageView mImageThumbnail;
	private EditText mTextTitle;
	private EditText mTextSubtitle;
	private Spinner mSpinnerLanguage;
	private Button mButtonEditAuthors;
	private EditText mTextIsbn10;
	private EditText mTextIsbn13;
	private EditText mTextPageCount;
	private AutoCompleteTextView mTextPublisher;
	private AutoCompleteTextView mTextSeries;
	private EditText mTextNumber;
	private EditText mTextPublishedDate;
	private Button mButtonEditCategories;
	private EditText mTextDescription;

	private LanguagesAdapter mLanguagesAdapter;

	private BookInfo mBookInfo;

	private boolean mValidBookInfo;

	/**
	 * Create a new instance of BookEditGeneralFragment.
	 * 
	 * @param bookInfo
	 *            Book to edit.
	 * @return BookEditGeneralFragment.
	 */
	public static BookEditGeneralFragment newInstance(BookInfo bookInfo) {
		BookEditGeneralFragment instance = new BookEditGeneralFragment();
		Bundle args = new Bundle();
		args.putSerializable(ARG_BOOK, bookInfo);
		instance.setArguments(args);
		return instance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.thumbnail_edit, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		boolean result;
		Intent intent;

		switch (item.getItemId()) {
		case R.id.thumbnailEdit_actionRemove:
			mBookInfo.thumbnail = null;
			mBookInfo.smallThumbnail = null;
			setImageThumbnail();
			result = true;
			break;

		case R.id.thumbnailEdit_actionTakePicture:
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			Activity activity = getActivity();
			if (intent.resolveActivity(activity.getPackageManager()) != null) {
				startActivityForResult(intent, REQUEST_TAKE_PICTURE);
			}

			result = true;
			break;

		case R.id.thumbnailEdit_actionPickImage:
			intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			startActivityForResult(intent, REQUEST_PICK_IMAGE);
			result = true;
			break;

		default:
			result = super.onContextItemSelected(item);
			break;
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_EDIT_AUTHORS) {
				mBookInfo.authors = (ArrayList<Author>) data.getSerializableExtra(BookAuthorsEditActivity.EXTRA_AUTHOR_LIST);
				setButtonEditAuthorsText();
			} else if (requestCode == REQUEST_EDIT_CATEGORIES) {
				mBookInfo.categories = (ArrayList<Category>) data.getSerializableExtra(BookCategoriesEditActivity.EXTRA_CATEGORY_LIST);
				setButtonEditCategoriesText();
			} else if (requestCode == REQUEST_PICK_IMAGE) {

				try {
					InputStream stream = getActivity().getContentResolver().openInputStream(data.getData());
					byte[] image = FileUtils.readBytes(stream);
					mBookInfo.smallThumbnail = BitmapUtils.compress(getActivity(), image, 160);
					mBookInfo.thumbnail = BitmapUtils.compress(getActivity(), image, 500);
					setImageThumbnail();
				} catch (IOException e) {
					Log.e(LogUtils.TAG, "Could not read selected image.", e);
					Toast.makeText(getActivity(), getString(R.string.message_cant_read_selected_image), Toast.LENGTH_LONG).show();
				}
			} else if (requestCode == REQUEST_TAKE_PICTURE) {
				Bundle extras = data.getExtras();
				Bitmap bitmap = (Bitmap) extras.get(Commons.EXTRA_CAMERA_DATA);
				byte[] image = BitmapUtils.getBytes(bitmap);
				mBookInfo.smallThumbnail = BitmapUtils.compress(getActivity(), image, 160);
				mBookInfo.thumbnail = BitmapUtils.compress(getActivity(), image, 500);
				setImageThumbnail();
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_book_edit_general, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		findViews();

		mDbHelper = new SQLiteHelper(this.getActivity());

		registerForContextMenu(mImageThumbnail);

		android.view.View.OnClickListener listener = new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				byte[] image = mBookInfo.thumbnail;
				if (image != null && image.length > 0) {
					FragmentManager fm = BookEditGeneralFragment.this.getFragmentManager();
					ImageDisplayFragment fragment = ImageDisplayFragment.newInstance(image);
					fragment.show(fm, IMAGE_DISPLAY_FRAGMENT_TAG);
				}
			}
		};
		mImageThumbnail.setOnClickListener(listener);

		mButtonEditAuthors.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				BookEditGeneralFragment.this.editAuthors(v);
			}
		});
		mButtonEditCategories.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				BookEditGeneralFragment.this.editCategories(v);

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

		AutoCompleteAdapter<Publisher> publisherAutoCompleteAdapter = new AutoCompleteAdapter<Publisher>(this.getActivity(),
				android.R.layout.simple_list_item_1, new AutoCompleteSearcher<Publisher>() {

					@Override
					public List<Publisher> search(CharSequence constraint) {
						SQLiteDatabase db = mDbHelper.getReadableDatabase();
						List<Publisher> publisherList = PublisherServices.getPublisherListByText(db, constraint.toString());
						db.close();
						return publisherList;
					}

					@Override
					public String getDisplayLabel(Publisher item) {
						return item.name;
					}
				});
		mTextPublisher.setAdapter(publisherAutoCompleteAdapter);

		AutoCompleteAdapter<Series> seriesAutoCompleteAdapter = new AutoCompleteAdapter<Series>(this.getActivity(),
				android.R.layout.simple_list_item_1, new AutoCompleteSearcher<Series>() {

					@Override
					public List<Series> search(CharSequence constraint) {
						SQLiteDatabase db = mDbHelper.getReadableDatabase();
						List<Series> seriesList = SeriesServices.getSeriesListByText(db, constraint.toString());
						db.close();
						return seriesList;
					}

					@Override
					public String getDisplayLabel(Series item) {
						return item.name;
					}
				});
		mTextSeries.setAdapter(seriesAutoCompleteAdapter);

		handleArguments();
		renderBookInfo();
	}

	/**
	 * Validate the user input and read the book info from the view.
	 * 
	 * @param bookInfo
	 *            BookInfo.
	 * @return True if the book information is valid, false otherwise.
	 */
	public boolean readBookInfo(BookInfo bookInfo) {
		mValidBookInfo = true;

		String title = getEditTextValue(mTextTitle, true);
		String subtitle = getEditTextValue(mTextSubtitle, false);
		String isbn10 = getEditTextValue(mTextIsbn10, false);
		String isbn13 = getEditTextValue(mTextIsbn13, false);
		String pageCountString = getEditTextValue(mTextPageCount, false);
		String publisherName = getEditTextValue(mTextPublisher, false);
		String publishedDate = getEditTextValue(mTextPublishedDate, false);
		String description = getEditTextValue(mTextDescription, false);
		String seriesName = getEditTextValue(mTextSeries, false);
		String numberString = getEditTextValue(mTextNumber, false);

		bookInfo.title = title;
		bookInfo.subtitle = subtitle;
		bookInfo.isbn10 = isbn10;
		bookInfo.isbn13 = isbn13;
		bookInfo.languageCode = ((Language) mSpinnerLanguage.getSelectedItem()).getCode();
		if (pageCountString != null && StringUtils.isInteger(pageCountString)) {
			bookInfo.pageCount = Long.valueOf(pageCountString);
		} else {
			bookInfo.pageCount = null;
		}
		bookInfo.publishedDate = publishedDate;
		if (numberString != null && StringUtils.isInteger(numberString)) {
			bookInfo.number = Long.valueOf(numberString);
		} else {
			bookInfo.number = null;
		}
		bookInfo.description = description;

		SQLiteDatabase db = mDbHelper.getReadableDatabase();

		List<Author> authors = new ArrayList<Author>();
		for (Author author : bookInfo.authors) {
			Author authorDb = AuthorServices.getAuthorByCriteria(db, author);
			if (authorDb != null) {
				authors.add(authorDb);
			} else {
				authors.add(author);
			}
		}
		bookInfo.authors = authors;

		Publisher publisher = new Publisher();
		if (publisherName != null) {
			publisher.name = publisherName;

			Publisher publisherDb = PublisherServices.getPublisherByCriteria(db, publisher);
			if (publisherDb != null) {
				publisher = publisherDb;
			}
		}
		bookInfo.publisher = publisher;

		Series series = new Series();
		if (seriesName != null) {
			series.name = seriesName;

			Series seriesDb = SeriesServices.getSeriesByCriteria(db, series);
			if (seriesDb != null) {
				series = seriesDb;
			}
		}
		bookInfo.series = series;

		List<Category> categories = new ArrayList<Category>();
		for (Category category : bookInfo.categories) {
			Category categoryDb = CategoryServices.getCategoryByCriteria(db, category);
			if (categoryDb != null) {
				categories.add(categoryDb);
			} else {
				categories.add(category);
			}
		}
		bookInfo.categories = categories;

		db.close();

		return mValidBookInfo;
	}

	/**
	 * Start the activity to edit the list of authors.
	 * 
	 * @param view
	 *            View.
	 */
	private void editAuthors(View view) {
		Intent intent = new Intent(this.getActivity(), BookAuthorsEditActivity.class);
		intent.putExtra(BookAuthorsEditActivity.EXTRA_BOOK_TITLE, mTextTitle.getText().toString());
		intent.putExtra(BookAuthorsEditActivity.EXTRA_AUTHOR_LIST, (ArrayList<Author>) mBookInfo.authors);
		startActivityForResult(intent, REQUEST_EDIT_AUTHORS);
	}

	/**
	 * Start the activity to edit the list of categories.
	 * 
	 * @param view
	 *            View.
	 */
	private void editCategories(View view) {
		Intent intent = new Intent(this.getActivity(), BookCategoriesEditActivity.class);
		intent.putExtra(BookCategoriesEditActivity.EXTRA_BOOK_TITLE, mTextTitle.getText().toString());
		intent.putExtra(BookCategoriesEditActivity.EXTRA_CATEGORY_LIST, (ArrayList<Category>) mBookInfo.categories);
		startActivityForResult(intent, REQUEST_EDIT_CATEGORIES);
	}

	/**
	 * Find the views of the activity that will contain the book information.
	 */
	private void findViews() {
		View view = getView();
		mImageThumbnail = (ImageView) view.findViewById(R.id.bookEditGeneral_buttonThumbnail);
		mTextTitle = (EditText) view.findViewById(R.id.bookEditGeneral_textTitle);
		mTextSubtitle = (EditText) view.findViewById(R.id.bookEditGeneral_textSubtitle);
		mSpinnerLanguage = (Spinner) view.findViewById(R.id.bookEditGeneral_spinnerLanguage);
		mButtonEditAuthors = (Button) view.findViewById(R.id.bookEditGeneral_buttonEditAuthors);
		mTextIsbn10 = (EditText) view.findViewById(R.id.bookEditGeneral_textIsbn10);
		mTextIsbn13 = (EditText) view.findViewById(R.id.bookEditGeneral_textIsbn13);
		mTextPageCount = (EditText) view.findViewById(R.id.bookEditGeneral_textPageCount);
		mTextPublisher = (AutoCompleteTextView) view.findViewById(R.id.bookEditGeneral_textPublisher);
		mTextPublishedDate = (EditText) view.findViewById(R.id.bookEditGeneral_textPublishedDate);
		mTextSeries = (AutoCompleteTextView) view.findViewById(R.id.bookEditGeneral_textSeries);
		mTextNumber = (EditText) view.findViewById(R.id.bookEditGeneral_textNumber);
		mButtonEditCategories = (Button) view.findViewById(R.id.bookEditGeneral_buttonEditCategories);
		mTextDescription = (EditText) view.findViewById(R.id.bookEditGeneral_textDescription);
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
	 * Handle the arguments of the fragment.
	 */
	private void handleArguments() {
		Bundle args = getArguments();
		mBookInfo = (BookInfo) args.getSerializable(ARG_BOOK);
	}

	/**
	 * Update the views of the activity using the book information.
	 */
	private void renderBookInfo() {
		setImageThumbnail();
		mTextTitle.setText(mBookInfo.title);
		mTextSubtitle.setText(mBookInfo.subtitle);
		mTextIsbn10.setText(mBookInfo.isbn10);
		mTextIsbn13.setText(mBookInfo.isbn13);
		int languageToSelect = mLanguagesAdapter.getPosition(mBookInfo.languageCode);
		if (languageToSelect < 0) {
			languageToSelect = 0;
		}
		mSpinnerLanguage.setSelection(languageToSelect);

		setButtonEditAuthorsText();
		if (mBookInfo.pageCount != null) {
			mTextPageCount.setText(mBookInfo.pageCount.toString());
		}
		mTextPublisher.setText(mBookInfo.publisher.name);
		mTextPublishedDate.setText(mBookInfo.publishedDate);
		mTextSeries.setText(mBookInfo.series.name);
		if (mBookInfo.number != null) {
			mTextNumber.setText(mBookInfo.number.toString());
		}
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
		byte[] thumbnail = mBookInfo.thumbnail;
		if (thumbnail != null && thumbnail.length > 0) {
			bitmap = BitmapFactory.decodeByteArray(thumbnail, 0, thumbnail.length);
			bitmap = BitmapUtils.resizeThumbnailBitmap(getActivity(), bitmap);
		} else {
			bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_undefined_thumbnail);
		}
		mImageThumbnail.setImageBitmap(bitmap);
	}
}
