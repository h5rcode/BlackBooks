package com.blackbooks.activities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.adapters.AuthorItem;
import com.blackbooks.adapters.BookItem;
import com.blackbooks.adapters.BooksByAuthorAdapter;
import com.blackbooks.adapters.ListItem;
import com.blackbooks.adapters.ListItemType;
import com.blackbooks.database.Database;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.helpers.FileHelper;
import com.blackbooks.helpers.IsbnHelper;
import com.blackbooks.helpers.Pic2ShopHelper;
import com.blackbooks.model.nonpersistent.AuthorInfo;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.services.AuthorServices;

/**
 * Activity that lists all the books.
 */
public final class BookList extends ListActivity {

	private final static String TAG = BookList.class.getName();

	private final static String FIRST_VISIBLE_POSITION = "FIRST_VISIBLE_POSITION";

	private BooksByAuthorAdapter mAdapter;
	private SQLiteHelper mDbHelper;

	private ListView listView;
	private int firstVisiblePosition;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.book_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		boolean result;
		Intent i;
		switch (item.getItemId()) {
		case R.id.bookList_actionScanIsbn:
			startIsbnScan();
			result = true;
			break;

		case R.id.bookList_actionTypeIsbn:
			i = new Intent(BookList.this, IsbnEnter.class);
			BookList.this.startActivity(i);
			result = true;
			break;

		case R.id.bookList_actionAddManually:
			i = new Intent(BookList.this, BookAdd.class);
			BookList.this.startActivity(i);
			result = true;
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
				Intent i = new Intent(BookList.this, BookAdd.class);
				i.putExtra(BookAdd.EXTRA_ISBN, barCode);
				BookList.this.startActivity(i);
			} else {
				String message = getString(R.string.message_invalid_isbn);
				message = String.format(message, barCode);
				Toast.makeText(BookList.this, message, Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_list);

		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(FIRST_VISIBLE_POSITION)) {
				firstVisiblePosition = savedInstanceState.getInt(FIRST_VISIBLE_POSITION);
			}
		}
		
		listView = (ListView) findViewById(android.R.id.list);
		mDbHelper = new SQLiteHelper(this);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		ListItem item = mAdapter.getItem(position);
		ListItemType itemType = item.getListItemType();

		if (itemType == ListItemType.Entry) {
			BookItem bookItem = (BookItem) item;
			Intent i = new Intent(BookList.this, BookDisplay.class);
			i.putExtra(BookDisplay.EXTRA_BOOK_ID, bookItem.getId());
			BookList.this.startActivity(i);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		ArrayList<AuthorInfo> authorInfoList = AuthorServices.getAuthorInfoList(db);
		db.close();

		ArrayList<ListItem> listItems = new ArrayList<ListItem>();

		for (AuthorInfo authorInfo : authorInfoList) {
			if (authorInfo.id == null) {
				authorInfo.name = getString(R.string.label_unspecified_author);
			}
			AuthorItem authorItem = new AuthorItem(authorInfo.id, authorInfo.name, authorInfo.books.size());
			listItems.add(authorItem);

			for (Book book : authorInfo.books) {
				BookItem bookEntry = new BookItem(book.id, book.title, book.smallThumbnail);
				listItems.add(bookEntry);
			}
		}

		mAdapter = new BooksByAuthorAdapter(this, listItems);
		setListAdapter(mAdapter);

		int count = listView.getCount();
		if (firstVisiblePosition >= count) {
			firstVisiblePosition = count - 1;
		}
		listView.setSelectionFromTop(firstVisiblePosition, 0);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		firstVisiblePosition = listView.getFirstVisiblePosition();
		outState.putInt(FIRST_VISIBLE_POSITION, firstVisiblePosition);
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
