package com.blackbooks.fragments;

import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.activities.BookEdit;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.BookServices;
import com.blackbooks.utils.StringUtils;
import com.blackbooks.utils.VariableUtils;

/**
 * Fragment to display the information of a book.
 */
public class BookDisplayFragment extends Fragment {

	private final static String ARG_BOOK_ID = "ARG_BOOK_ID";

	private final static int REQUEST_CODE_EDIT_BOOK = 1;

	private SQLiteHelper mDbHelper;

	private BookInfo mBookInfo;

	private ImageView mImageCover;
	private TextView mTextTitle;
	private TextView mTextSubtitle;
	private TextView mTextAuthor;
	private TextView mTextIsbn;
	private TextView mTextPageCount;
	private TextView mTextPublisher;
	private TextView mTextPublishedDate;
	private TextView mTextCategory;
	private TextView mTextLanguage;
	private TextView mTextDescription;

	private LinearLayout mGroupInfo;
	private LinearLayout mGroupInfoUserFriendlyPageCount;
	private LinearLayout mGroupInfoUserFriendlyCategories;
	private LinearLayout mGroupInfoUserFriendlyLanguage;
	private LinearLayout mGroupInfoTechnicalIsbn;
	private LinearLayout mGroupInfoTechnicalPublisher;
	private LinearLayout mGroupInfoTechnicalPublishedDate;
	private LinearLayout mGroupDescription;

	private BookDisplayListener mBookDisplayListener;

	/**
	 * Create a new instance of BookDisplayFragment, initialized to display a
	 * book.
	 * 
	 * @param bookId
	 *            Id of the book.
	 * @return BookDisplayFragment.
	 */
	public static BookDisplayFragment newInstance(long bookId) {
		BookDisplayFragment instance = new BookDisplayFragment();
		Bundle args = new Bundle();
		args.putLong(ARG_BOOK_ID, bookId);
		instance.setArguments(args);
		return instance;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof BookDisplayListener) {
			mBookDisplayListener = (BookDisplayListener) activity;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_book_display, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Bundle args = getArguments();
		if (args != null) {
			long bookId = args.getLong(ARG_BOOK_ID);
			mDbHelper = new SQLiteHelper(this.getActivity());
			findViews();
			displayBook(bookId);
		}
	}

	/**
	 * Load the book info from the database and render it.
	 * 
	 * @param bookId
	 *            Id of the book.
	 */
	private void displayBook(long bookId) {
		loadBook(bookId);
		renderBookInfo();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_CODE_EDIT_BOOK) {
				displayBook(mBookInfo.id);
			}
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.book_display, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result;
		switch (item.getItemId()) {
		case R.id.bookDisplay_actionEdit:
			Intent i = new Intent(this.getActivity(), BookEdit.class);
			i.putExtra(BookEdit.EXTRA_MODE, BookEdit.MODE_EDIT);
			i.putExtra(BookEdit.EXTRA_BOOK_ID, mBookInfo.id);
			startActivityForResult(i, REQUEST_CODE_EDIT_BOOK);
			result = true;
			break;

		case R.id.bookDisplay_actionDelete:
			showDeleteConfirmDialog();
			result = true;
			break;
		default:
			result = super.onOptionsItemSelected(item);
			break;
		}
		return result;
	}

	/**
	 * Find the views of the activity that will contain the book information.
	 */
	private void findViews() {
		View rootView = getView();

		mImageCover = (ImageView) rootView.findViewById(R.id.bookDisplay_imageCover);
		mTextTitle = (TextView) rootView.findViewById(R.id.bookDisplay_textTitle);
		mTextSubtitle = (TextView) rootView.findViewById(R.id.bookDisplay_textSubtitle);
		mTextAuthor = (TextView) rootView.findViewById(R.id.bookDisplay_textAuthor);
		mTextIsbn = (TextView) rootView.findViewById(R.id.bookDisplay_textIsbn);
		mTextPageCount = (TextView) rootView.findViewById(R.id.bookDisplay_textPageCount);
		mTextPublisher = (TextView) rootView.findViewById(R.id.bookDisplay_textPublisher);
		mTextPublishedDate = (TextView) rootView.findViewById(R.id.bookDisplay_textPublishedDate);
		mTextCategory = (TextView) rootView.findViewById(R.id.bookDisplay_textCategory);
		mTextLanguage = (TextView) rootView.findViewById(R.id.bookDisplay_textLanguage);
		mTextLanguage = (TextView) rootView.findViewById(R.id.bookDisplay_textLanguage);
		mTextDescription = (TextView) rootView.findViewById(R.id.bookDisplay_textDescription);

		mGroupInfo = (LinearLayout) rootView.findViewById(R.id.bookDisplay_groupInfo);
		mGroupInfoUserFriendlyPageCount = (LinearLayout) rootView.findViewById(R.id.bookDisplay_groupInfoUserFriendly_pageCount);
		mGroupInfoUserFriendlyCategories = (LinearLayout) rootView.findViewById(R.id.bookDisplay_groupInfoUserFriendly_categories);
		mGroupInfoUserFriendlyLanguage = (LinearLayout) rootView.findViewById(R.id.bookDisplay_groupInfoUserFriendly_language);
		mGroupInfoTechnicalIsbn = (LinearLayout) rootView.findViewById(R.id.bookDisplay_groupInfoTechnical_isbn);
		mGroupInfoTechnicalPublisher = (LinearLayout) rootView.findViewById(R.id.bookDisplay_groupInfoTechnical_publisher);
		mGroupInfoTechnicalPublishedDate = (LinearLayout) rootView.findViewById(R.id.bookDisplay_groupInfoTechnical_publishedDate);
		mGroupDescription = (LinearLayout) rootView.findViewById(R.id.bookDisplay_groupDescription);
	}

	/**
	 * Load the book from the database.
	 * 
	 * @param bookId
	 *            Id of a book.
	 */
	private void loadBook(long bookId) {
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		mBookInfo = BookServices.getBookInfo(db, bookId);
		db.close();
		
		if (mBookDisplayListener != null) {
			mBookDisplayListener.onBookLoaded(mBookInfo);
		}
	}

	/**
	 * Update the views of the activity using the book information.
	 */
	private void renderBookInfo() {
		boolean hasPageCount = mBookInfo.pageCount != null;
		boolean hasCategories = mBookInfo.categories.size() > 0;
		boolean hasLanguage = mBookInfo.languageCode != null;
		boolean hasIdentifiers = mBookInfo.identifiers.size() > 0;
		boolean hasPublisher = mBookInfo.publisher.name != null;
		boolean hasPublishedDate = mBookInfo.publishedDate != null;

		boolean showInfoUserFriendly = hasPageCount || hasCategories || hasLanguage;
		boolean showInfoTechnical = hasIdentifiers || hasPublisher || hasPublishedDate;
		boolean showInfo = showInfoUserFriendly || showInfoTechnical;

		mTextTitle.setText(mBookInfo.title);
		if (mBookInfo.thumbnail != null && mBookInfo.thumbnail.length > 0) {
			Bitmap bitmap = BitmapFactory.decodeByteArray(mBookInfo.thumbnail, 0, mBookInfo.thumbnail.length);
			mImageCover.setImageBitmap(bitmap);
			mImageCover.setVisibility(View.VISIBLE);
		} else {
			mImageCover.setVisibility(View.GONE);
		}
		if (mBookInfo.subtitle != null) {
			mTextSubtitle.setText(mBookInfo.subtitle);
			mTextSubtitle.setVisibility(View.VISIBLE);
		} else {
			mTextSubtitle.setVisibility(View.GONE);
		}
		if (mBookInfo.authors.size() > 0) {
			String authorsFormat = getString(R.string.label_authors_format);
			String authors = String.format(authorsFormat, StringUtils.joinAuthorNameList(mBookInfo.authors, ", "));
			mTextAuthor.setText(authors);
		} else {
			mTextAuthor.setText(getString(R.string.label_unspecified_author));
		}

		if (showInfo) {
			if (hasPageCount) {
				mTextPageCount.setText(mBookInfo.pageCount.toString());
				mGroupInfoUserFriendlyPageCount.setVisibility(View.VISIBLE);
			} else {
				mGroupInfoUserFriendlyPageCount.setVisibility(View.GONE);
			}
			if (hasCategories) {
				mTextCategory.setText(StringUtils.joinCategoryNameList(mBookInfo.categories, ", "));
				mGroupInfoUserFriendlyCategories.setVisibility(View.VISIBLE);
			} else {
				mGroupInfoUserFriendlyCategories.setVisibility(View.GONE);
			}
			if (hasLanguage) {
				Locale locale = new Locale(mBookInfo.languageCode);
				String language = locale.getDisplayLanguage();
				mTextLanguage.setText(StringUtils.capitalize(language));
				mGroupInfoUserFriendlyLanguage.setVisibility(View.VISIBLE);
			} else {
				mGroupInfoUserFriendlyLanguage.setVisibility(View.GONE);
			}
			if (hasIdentifiers) {
				mTextIsbn.setText(mBookInfo.identifiers.get(0).identifier);
				mGroupInfoTechnicalIsbn.setVisibility(View.VISIBLE);
			} else {
				mGroupInfoTechnicalIsbn.setVisibility(View.GONE);
			}
			if (hasPublisher) {
				mTextPublisher.setText(mBookInfo.publisher.name);
				mGroupInfoTechnicalPublisher.setVisibility(View.VISIBLE);
			} else {
				mGroupInfoTechnicalPublisher.setVisibility(View.GONE);
			}
			if (hasPublishedDate) {
				mTextPublishedDate.setText(mBookInfo.publishedDate);
				mGroupInfoTechnicalPublishedDate.setVisibility(View.VISIBLE);
			} else {
				mGroupInfoTechnicalPublishedDate.setVisibility(View.GONE);
			}
			mGroupInfo.setVisibility(View.VISIBLE);
		} else {
			mGroupInfo.setVisibility(View.GONE);
		}
		if (mBookInfo.description != null) {
			mTextDescription.setText(mBookInfo.description);
			mGroupDescription.setVisibility(View.VISIBLE);

		} else {
			mGroupDescription.setVisibility(View.GONE);
		}
	}

	/**
	 * Delete the current book.
	 */
	public void delete() {
		String title = mBookInfo.title;
		String message = String.format(getString(R.string.message_book_deleted), title);

		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		BookServices.deleteBook(db, mBookInfo.id);
		db.close();
		VariableUtils.getInstance().setReloadBookList(true);
		Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT).show();
		
		if (mBookDisplayListener != null) {
			mBookDisplayListener.onBookDeleted();
		}
	}

	/**
	 * Show the delete confirm dialog.
	 */
	private void showDeleteConfirmDialog() {
		String message = getString(R.string.message_confirm_delete_book);
		message = String.format(message, mBookInfo.title);

		String cancelText = getString(R.string.message_confirm_delete_book_cancel);
		String confirmText = getString(R.string.message_confirm_delete_book_confirm);

		new AlertDialog.Builder(this.getActivity()).setMessage(message).setPositiveButton(confirmText, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				BookDisplayFragment.this.delete();
			}
		}).setNegativeButton(cancelText, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Do nothing.
			}
		}).show();
	}

	/**
	 * An activity hosting a {@link BookDisplayFragment} should implement this
	 * interface to be notified when the book is loaded, deleted.
	 */
	public interface BookDisplayListener {
		
		/**
		 * Called when the book is loaded.
		 * @param bookInfo BookInfo.
		 */
		void onBookLoaded(BookInfo bookInfo);

		/**
		 * Called when the book is deleted.
		 */
		void onBookDeleted();
	}
}
