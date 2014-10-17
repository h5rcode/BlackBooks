package com.blackbooks.activities;

import java.io.File;
import java.io.IOException;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.adapters.BookItem;
import com.blackbooks.adapters.ListItem;
import com.blackbooks.adapters.ListItemType;
import com.blackbooks.database.Database;
import com.blackbooks.helpers.FileHelper;
import com.blackbooks.helpers.IsbnHelper;
import com.blackbooks.helpers.Pic2ShopHelper;

/**
 * Abstract class to list the books.
 */
public abstract class AbstractBookList extends ListActivity {

	private final static String TAG = AbstractBookList.class.getName();

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
			i = new Intent(this, BookAdd.class);
			this.startActivity(i);
			result = true;
			break;

		case R.id.bookList_actionSortByAuthor:
			result = true;
			if (!(this instanceof BookListByAuthor)) {
				sharedPref = getSharedPreferences(BlackBooksStart.PREFERENCES, MODE_PRIVATE);
				editor = sharedPref.edit();
				editor.putString(BlackBooksStart.PREF_DEFAULT_LIST, BookListByAuthor.class.getName());
				editor.commit();
				NavUtils.navigateUpFromSameTask(this);
			}
			break;

		case R.id.bookList_actionSortByFirstLetter:
			result = true;
			if (!(this instanceof BookListByFirstLetter)) {
				sharedPref = getSharedPreferences(BlackBooksStart.PREFERENCES, MODE_PRIVATE);
				editor = sharedPref.edit();
				editor.putString(BlackBooksStart.PREF_DEFAULT_LIST, BookListByFirstLetter.class.getName());
				editor.commit();
				NavUtils.navigateUpFromSameTask(this);
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
				Intent i = new Intent(this, BookAdd.class);
				i.putExtra(BookAdd.EXTRA_ISBN, barCode);
				this.startActivity(i);
			} else {
				String message = getString(R.string.message_invalid_isbn);
				message = String.format(message, barCode);
				Toast.makeText(this, message, Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_list);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		ListItem item = (ListItem) getListAdapter().getItem(position);
		ListItemType itemType = item.getListItemType();

		if (itemType == ListItemType.Entry) {
			BookItem bookItem = (BookItem) item;
			Intent i = new Intent(this, BookDisplay.class);
			i.putExtra(BookDisplay.EXTRA_BOOK_ID, bookItem.getId());
			this.startActivity(i);
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
}
