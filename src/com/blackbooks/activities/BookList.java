package com.blackbooks.activities;

import java.io.File;
import java.io.IOException;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.database.Database;
import com.blackbooks.fragments.AbstractBookListFragment;
import com.blackbooks.fragments.AbstractBookListFragment.BookListListener;
import com.blackbooks.fragments.BookListByAuthorFragment;
import com.blackbooks.fragments.BookListByFirstLetterFragment;
import com.blackbooks.helpers.FileHelper;
import com.blackbooks.helpers.IsbnHelper;
import com.blackbooks.helpers.Pic2ShopHelper;

/**
 * The book list activity. It hosts an AbstractBookListFragment used to display
 * list in various orders.
 */
public class BookList extends FragmentActivity implements BookListListener {

	private static final String PREFERENCES = "PREFERENCES";
	private static final String PREF_DEFAULT_LIST = "PREF_DEFAULT_LIST";
	private static final String BOOK_LIST_FRAGMENT_TAG = "BOOK_LIST_FRAGMENT_TAG";
	private static final String TAG = BookList.class.getName();

	private AbstractBookListFragment mCurrentFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_list);

		SharedPreferences preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);

		String defaultList = preferences.getString(PREF_DEFAULT_LIST, null);

		FragmentManager fm = getSupportFragmentManager();
		mCurrentFragment = (AbstractBookListFragment) fm.findFragmentByTag(BOOK_LIST_FRAGMENT_TAG);

		if (mCurrentFragment == null) {
			if (defaultList == null) {
				mCurrentFragment = new BookListByAuthorFragment();
			} else if (defaultList.equals(BookListByAuthorFragment.class.getName())) {
				mCurrentFragment = new BookListByAuthorFragment();
			} else if (defaultList.equals(BookListByFirstLetterFragment.class.getName())) {
				mCurrentFragment = new BookListByFirstLetterFragment();
			} else {
				mCurrentFragment = new BookListByAuthorFragment();
			}

			fm.beginTransaction() //
					.add(R.id.bookList_frameLayout, mCurrentFragment, BOOK_LIST_FRAGMENT_TAG) //
					.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.book_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		boolean result;
		Intent i;
		SharedPreferences sharedPref;
		SharedPreferences.Editor editor;

		switch (item.getItemId()) {
		case R.id.bookList_actionScanIsbn:
			startIsbnScan();
			result = true;
			break;

		case R.id.bookList_actionTypeIsbn:
			i = new Intent(this, IsbnEnter.class);
			this.startActivity(i);
			result = true;
			break;

		case R.id.bookList_actionAddManually:
			i = new Intent(this, BookEdit.class);
			i.putExtra(BookEdit.EXTRA_MODE, BookEdit.MODE_ADD);
			this.startActivity(i);
			result = true;
			break;

		case R.id.bookList_actionSortByAuthor:
			result = true;
			if (!(mCurrentFragment instanceof BookListByAuthorFragment)) {
				sharedPref = getSharedPreferences(BookList.PREFERENCES, MODE_PRIVATE);
				editor = sharedPref.edit();
				editor.putString(BookList.PREF_DEFAULT_LIST, BookListByAuthorFragment.class.getName());
				editor.commit();
				mCurrentFragment = new BookListByAuthorFragment();
				getSupportFragmentManager().beginTransaction() //
						.replace(R.id.bookList_frameLayout, mCurrentFragment, BOOK_LIST_FRAGMENT_TAG) //
						.commit();
			}

			break;

		case R.id.bookList_actionSortByFirstLetter:
			result = true;

			if (!(mCurrentFragment instanceof BookListByFirstLetterFragment)) {
				sharedPref = getSharedPreferences(BookList.PREFERENCES, MODE_PRIVATE);
				editor = sharedPref.edit();
				editor.putString(BookList.PREF_DEFAULT_LIST, BookListByFirstLetterFragment.class.getName());
				editor.commit();
				mCurrentFragment = new BookListByFirstLetterFragment();
				getSupportFragmentManager().beginTransaction() //
						.replace(R.id.bookList_frameLayout, mCurrentFragment, BOOK_LIST_FRAGMENT_TAG) //
						.commit();
			}

			break;

		case R.id.bookList_backupDb:
			saveDbOnDisk();
			result = true;
			break;

		default:
			result = super.onOptionsItemSelected(item);
			break;
		}
		return result;
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
	}

	/**
	 * Save a copy of the database file in the "Download" folder.
	 */
	private void saveDbOnDisk() {

		try {
			File dwnldFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

			if (FileHelper.isExternalStorageWritable() && dwnldFolder.canWrite()) {
				File currentDB = this.getDatabasePath(Database.NAME);
				File backupDB = new File(dwnldFolder, Database.NAME + ".sqlite");

				FileHelper.copy(currentDB, backupDB);

				MediaScannerConnection.scanFile(this, new String[] { backupDB.getAbsolutePath() }, null, null);

				Toast.makeText(this, "File " + backupDB.getName() + " saved in " + dwnldFolder.getName() + ".", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, "Cannot write", Toast.LENGTH_LONG).show();
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Launches Pic2Shop to start scanning an ISBN code.
	 */
	private void startIsbnScan() {
		Intent intent = new Intent(Pic2ShopHelper.ACTION);
		startActivityForResult(intent, Pic2ShopHelper.REQUEST_CODE_SCAN);
	}

	@Override
	public void onBookListLoaded() {
	}
}
