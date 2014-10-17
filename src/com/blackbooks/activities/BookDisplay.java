package com.blackbooks.activities;

import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.BookServices;
import com.blackbooks.utils.StringUtils;
import com.blackbooks.utils.VariableUtils;

/**
 * Activity to display the info of a book saved in the database.
 * 
 */
public final class BookDisplay extends Activity {

	public final static String EXTRA_BOOK_ID = "BOOK_ID";

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		Intent intent = getIntent();

		if (intent != null && intent.hasExtra(EXTRA_BOOK_ID)) {
			long bookId = intent.getLongExtra(EXTRA_BOOK_ID, Long.MIN_VALUE);

			mDbHelper = new SQLiteHelper(this);
			SQLiteDatabase db = mDbHelper.getReadableDatabase();
			mBookInfo = BookServices.getBookInfo(db, bookId);
			db.close();

			setContentView(R.layout.activity_book_display);

			setTitle(mBookInfo.title);
			findViews();
			renderBookInfo();

		} else {
			finish();
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
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.book_display, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result;
		switch (item.getItemId()) {
		case R.id.bookDisplay_actionEdit:
			Toast.makeText(this, "Coming soon...", Toast.LENGTH_SHORT).show();
			result = true;
			break;

		case R.id.bookDisplay_actionDelete:
			showDeleteConfirmDialog();
			result = true;
			break;
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
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
		mImageCover = (ImageView) findViewById(R.id.bookDisplay_imageCover);
		mTextTitle = (TextView) findViewById(R.id.bookDisplay_textTitle);
		mTextSubtitle = (TextView) findViewById(R.id.bookDisplay_textSubtitle);
		mTextAuthor = (TextView) findViewById(R.id.bookDisplay_textAuthor);
		mTextIsbn = (TextView) findViewById(R.id.bookDisplay_textIsbn);
		mTextPageCount = (TextView) findViewById(R.id.bookDisplay_textPageCount);
		mTextPublisher = (TextView) findViewById(R.id.bookDisplay_textPublisher);
		mTextPublishedDate = (TextView) findViewById(R.id.bookDisplay_textPublishedDate);
		mTextCategory = (TextView) findViewById(R.id.bookDisplay_textCategory);
		mTextLanguage = (TextView) findViewById(R.id.bookDisplay_textLanguage);
		mTextLanguage = (TextView) findViewById(R.id.bookDisplay_textLanguage);
		mTextDescription = (TextView) findViewById(R.id.bookDisplay_textDescription);

		mGroupInfo = (LinearLayout) findViewById(R.id.bookDisplay_groupInfo);
		mGroupInfoUserFriendlyPageCount = (LinearLayout) findViewById(R.id.bookDisplay_groupInfoUserFriendly_pageCount);
		mGroupInfoUserFriendlyCategories = (LinearLayout) findViewById(R.id.bookDisplay_groupInfoUserFriendly_categories);
		mGroupInfoUserFriendlyLanguage = (LinearLayout) findViewById(R.id.bookDisplay_groupInfoUserFriendly_language);
		mGroupInfoTechnicalIsbn = (LinearLayout) findViewById(R.id.bookDisplay_groupInfoTechnical_isbn);
		mGroupInfoTechnicalPublisher = (LinearLayout) findViewById(R.id.bookDisplay_groupInfoTechnical_publisher);
		mGroupInfoTechnicalPublishedDate = (LinearLayout) findViewById(R.id.bookDisplay_groupInfoTechnical_publishedDate);
		mGroupDescription = (LinearLayout) findViewById(R.id.bookDisplay_groupDescription);
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
		} else {
			mImageCover.setVisibility(View.GONE);
		}
		if (mBookInfo.subtitle != null) {
			mTextSubtitle.setText(mBookInfo.subtitle);
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
			} else {
				mGroupInfoUserFriendlyPageCount.setVisibility(View.GONE);
			}
			if (hasCategories) {
				mTextCategory.setText(StringUtils.joinCategoryNameList(mBookInfo.categories, ", "));
			} else {
				mGroupInfoUserFriendlyCategories.setVisibility(View.GONE);
			}
			if (hasLanguage) {
				Locale locale = new Locale(mBookInfo.languageCode);
				String language = locale.getDisplayLanguage();
				mTextLanguage.setText(StringUtils.capitalize(language));
			} else {
				mGroupInfoUserFriendlyLanguage.setVisibility(View.GONE);
			}
			if (hasIdentifiers) {
				mTextIsbn.setText(mBookInfo.identifiers.get(0).identifier);
			} else {
				mGroupInfoTechnicalIsbn.setVisibility(View.GONE);
			}
			if (hasPublisher) {
				mTextPublisher.setText(mBookInfo.publisher.name);
			} else {
				mGroupInfoTechnicalPublisher.setVisibility(View.GONE);
			}
			if (hasPublishedDate) {
				mTextPublishedDate.setText(mBookInfo.publishedDate);
			} else {
				mGroupInfoTechnicalPublishedDate.setVisibility(View.GONE);
			}
		} else {
			mGroupInfo.setVisibility(View.GONE);
		}
		if (mBookInfo.description != null) {
			mTextDescription.setText(mBookInfo.description);

		} else {
			mGroupDescription.setVisibility(View.GONE);
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

		new AlertDialog.Builder(this).setMessage(message).setPositiveButton(confirmText, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				BookDisplay.this.delete();
			}
		}).setNegativeButton(cancelText, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Do nothing.
			}
		}).show();
	}
}
