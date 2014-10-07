package com.blackbooks.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.BookServices;
import com.blackbooks.utils.StringUtils;

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
	private TextView mTextDescription;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();

		if (intent != null && intent.hasExtra(EXTRA_BOOK_ID)) {
			long bookId = intent.getLongExtra(EXTRA_BOOK_ID, Long.MIN_VALUE);

			mDbHelper = new SQLiteHelper(this);
			SQLiteDatabase db = mDbHelper.getReadableDatabase();
			mBookInfo = BookServices.getBookInfo(db, bookId);
			db.close();

			setContentView(R.layout.activity_book_display);

			mImageCover = (ImageView) findViewById(R.id.bookDisplay_imageCover);
			mTextTitle = (TextView) findViewById(R.id.bookDisplay_textTitle);
			mTextSubtitle = (TextView)findViewById(R.id.bookDisplay_textSubtitle);
			mTextAuthor = (TextView) findViewById(R.id.bookDisplay_textAuthor);
			mTextIsbn = (TextView) findViewById(R.id.bookDisplay_textIsbn);
			mTextPageCount = (TextView) findViewById(R.id.bookDisplay_textPageCount);
			mTextPublisher = (TextView) findViewById(R.id.bookDisplay_textPublisher);
			mTextPublishedDate = (TextView) findViewById(R.id.bookDisplay_textPublishedDate);
			mTextCategory = (TextView) findViewById(R.id.bookDisplay_textCategory);
			mTextDescription = (TextView) findViewById(R.id.bookDisplay_textDescription);

			setTitle(mBookInfo.title);

			if (mBookInfo.thumbnail != null && mBookInfo.thumbnail.length > 0) {
				Bitmap bitmap = BitmapFactory.decodeByteArray(mBookInfo.thumbnail, 0, mBookInfo.thumbnail.length);
				mImageCover.setImageBitmap(bitmap);
			}
			mTextTitle.setText(mBookInfo.title);
			mTextSubtitle.setText(mBookInfo.subtitle);
			mTextAuthor.setText(StringUtils.joinAuthorNameList(mBookInfo.authors, ",\n"));
			if (mBookInfo.identifiers.size() > 0) {
				mTextIsbn.setText(mBookInfo.identifiers.get(0).identifier);
			}
			if (mBookInfo.pageCount != null) {
				mTextPageCount.setText(mBookInfo.pageCount.toString());
			}
			mTextPublisher.setText(mBookInfo.publisher.name);
			mTextPublishedDate.setText(mBookInfo.publishedDate);
			mTextCategory.setText(StringUtils.joinCategoryNameList(mBookInfo.categories, ",\n"));
			mTextDescription.setText(mBookInfo.description);

		} else {
			finish();
		}
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

	/**
	 * Delete the current book.
	 */
	public void delete() {
		String title = mBookInfo.title;
		String message = String.format(getString(R.string.message_book_deleted), title);

		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		BookServices.deleteBook(db, mBookInfo.id);
		db.close();

		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		finish();
	}
}
