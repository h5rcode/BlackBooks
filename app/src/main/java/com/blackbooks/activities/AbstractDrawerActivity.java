package com.blackbooks.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.adapters.DrawerAdapter;
import com.blackbooks.adapters.DrawerAdapter.DrawerItem;
import com.blackbooks.adapters.DrawerAdapter.DrawerItemType;
import com.blackbooks.database.Database;
import com.blackbooks.fragments.dialogs.ScannerInstallFragment;
import com.blackbooks.utils.FileUtils;
import com.blackbooks.utils.IsbnUtils;
import com.blackbooks.utils.Pic2ShopUtils;

/**
 * An abstract FragmentActivity that contains a navigation drawer.
 */
public abstract class AbstractDrawerActivity extends FragmentActivity {

	private static final int ITEM_BOOKS = 1;
	private static final int ITEM_SUMMARY = 2;
	private static final int ITEM_BOOK_LIST = 3;
	private static final int ITEM_ADD_BOOK = 4;
	private static final int ITEM_SCAN_ISBN = 5;
	private static final int ITEM_ENTER_ISBN = 6;
	private static final int ITEM_ADD_MANUALLY = 7;
	// private static final int ITEM_IMPORT_EXPORT_BOOKS = 8;
	private static final int ITEM_IMPORT_BOOKS = 9;
	private static final int ITEM_EXPORT_BOOKS = 10;
	private static final int ITEM_ADMINISTRATION = 11;
	private static final int ITEM_BACKUP_DB = 12;

	private static final String SCANNER_INSTALL_FRAGMENT = "SCANNER_INSTALL_FRAGMENT";

	private DrawerLayout mDrawerLayout;
	private ListView mListDrawer;
	private ActionBarDrawerToggle mDrawerToggle;

	/**
	 * Return a value identifying the activity.
	 * 
	 * @return DrawerActivity.
	 */
	protected abstract DrawerActivity getDrawerActivity();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abstract_drawer_activity);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.abstractDrawerActivity_drawerLayout);
		mListDrawer = (ListView) findViewById(R.id.abstractDrawerActivity_leftDrawer);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.label_drawer_open,
				R.string.label_drawer_close);
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Books.
		DrawerItem groupBooks = new DrawerItem(ITEM_BOOKS, getString(R.string.menu_books), null, DrawerItemType.GROUP);
		DrawerItem itemSummary = new DrawerItem(ITEM_SUMMARY, getString(R.string.menu_book_list_summary),
				R.drawable.ic_action_summary, DrawerItemType.ITEM);
		DrawerItem itemBookList = new DrawerItem(ITEM_BOOK_LIST, getString(R.string.menu_book_list),
				R.drawable.ic_action_book_list, DrawerItemType.ITEM);

		// Add books.
		DrawerItem groupAddBook = new DrawerItem(ITEM_ADD_BOOK, getString(R.string.menu_add_books), null, DrawerItemType.GROUP);
		DrawerItem itemScanIsbn = new DrawerItem(ITEM_SCAN_ISBN, getString(R.string.action_scan_isbn),
				R.drawable.ic_action_camera, DrawerItemType.ITEM);
		DrawerItem itemEnterIsbn = new DrawerItem(ITEM_ENTER_ISBN, getString(R.string.action_enter_isbn),
				R.drawable.ic_action_dial_pad, DrawerItemType.ITEM);
		DrawerItem itemAddManually = new DrawerItem(ITEM_ADD_MANUALLY, getString(R.string.action_add_manually),
				R.drawable.ic_action_keyboard, DrawerItemType.ITEM);

		// Import/Export.
		// DrawerItem groupImportExportBooks = new
		// DrawerItem(ITEM_IMPORT_EXPORT_BOOKS,
		// getString(R.string.menu_import_export),
		// null, DrawerItemType.GROUP);
		// DrawerItem itemImportBooks = new DrawerItem(ITEM_IMPORT_BOOKS,
		// getString(R.string.menu_import),
		// R.drawable.ic_action_import, DrawerItemType.ITEM);
		// DrawerItem itemExportBooks = new DrawerItem(ITEM_EXPORT_BOOKS,
		// getString(R.string.menu_export),
		// R.drawable.ic_action_export, DrawerItemType.ITEM);

		// Administration.
		DrawerItem groupAdministration = new DrawerItem(ITEM_ADMINISTRATION, getString(R.string.menu_administration), null,
				DrawerItemType.GROUP);

		DrawerItem itemExportBooks = new DrawerItem(ITEM_EXPORT_BOOKS, getString(R.string.menu_export),
				R.drawable.ic_action_export, DrawerItemType.ITEM);

		DrawerItem itemBackupDb = new DrawerItem(ITEM_BACKUP_DB, getString(R.string.menu_backup_db), R.drawable.ic_action_save,
				DrawerItemType.ITEM);

		List<DrawerItem> list = new ArrayList<DrawerItem>();
		list.add(groupBooks);
		list.add(itemBookList);
		list.add(itemSummary);
		list.add(groupAddBook);
		list.add(itemScanIsbn);
		list.add(itemEnterIsbn);
		list.add(itemAddManually);
		// list.add(groupImportExportBooks);
		// list.add(itemImportBooks);
		// list.add(itemExportBooks);
		list.add(groupAdministration);
		list.add(itemExportBooks);
		list.add(itemBackupDb);

		mListDrawer.setAdapter(new DrawerAdapter(this, list));
		mListDrawer.setOnItemClickListener(new DrawerItemClickListener());
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Pic2ShopUtils.REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
			String barCode = data.getStringExtra(Pic2ShopUtils.BARCODE);

			if (IsbnUtils.isValidIsbn(barCode)) {
				Intent i = new Intent(this, BookEditActivity.class);
				i.putExtra(BookEditActivity.EXTRA_MODE, BookEditActivity.MODE_ADD);
				i.putExtra(BookEditActivity.EXTRA_ISBN, barCode);
				this.startActivity(i);
			} else {
				String message = getString(R.string.message_invalid_isbn);
				message = String.format(message, barCode);
				Toast.makeText(this, message, Toast.LENGTH_LONG).show();
			}
		}
	}

	/**
	 * Close the navigation drawer.
	 */
	private void closeDrawer() {
		mDrawerLayout.closeDrawer(mListDrawer);
	}

	/**
	 * An implementation of ListView.OnItemClickListener that starts the
	 * activity corresponding the navigation drawer item that has been clicked.
	 */
	private final class DrawerItemClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			DrawerItem drawerItem = (DrawerItem) mListDrawer.getAdapter().getItem(position);

			switch (drawerItem.getId()) {
			case ITEM_BOOK_LIST:
				startBookListActivity();
				break;

			case ITEM_SUMMARY:
				startSummaryActivity();
				break;

			case ITEM_SCAN_ISBN:
				mDrawerLayout.closeDrawer(mListDrawer);
				startIsbnScan();
				break;

			case ITEM_ENTER_ISBN:
				startIsbnLookupActivity();
				break;

			case ITEM_ADD_MANUALLY:
				startBookEditActivity();
				break;

			case ITEM_IMPORT_BOOKS:
				startBookImportActivity();
				break;

			case ITEM_EXPORT_BOOKS:
				startBookExportActivity();
				break;

			case ITEM_BACKUP_DB:
				saveDbOnDisk();
				break;
			}
		}

		/**
		 * Start {@link BookEditActivity}.
		 */
		private void startBookEditActivity() {
			closeDrawer();
			Intent i = new Intent(AbstractDrawerActivity.this, BookEditActivity.class);
			i.putExtra(BookEditActivity.EXTRA_MODE, BookEditActivity.MODE_ADD);
			startActivity(i);
		}

		/**
		 * Start {@link IsbnLookupActivity}.
		 */
		private void startIsbnLookupActivity() {
			closeDrawer();
			Intent i = new Intent(AbstractDrawerActivity.this, IsbnLookupActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(i);
		}

		/**
		 * Launches Pic2Shop to start scanning an ISBN code.
		 */
		private void startIsbnScan() {
			Intent intent = new Intent(Pic2ShopUtils.ACTION);

			PackageManager pm = getPackageManager();
			List<ResolveInfo> resolveInfo = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

			if (resolveInfo.isEmpty()) {
				FragmentManager fm = getSupportFragmentManager();
				ScannerInstallFragment fragment = new ScannerInstallFragment();
				fragment.show(fm, SCANNER_INSTALL_FRAGMENT);
			} else {
				intent = new Intent(AbstractDrawerActivity.this, IsbnLookupActivity.class);
				intent.putExtra(IsbnLookupActivity.EXTRA_SCAN, true);
				startActivity(intent);
			}
		}

		/**
		 * Start {@link BookListActivity}.
		 */
		private void startBookListActivity() {
			closeDrawer();
			if (getDrawerActivity() != DrawerActivity.BOOK_LIST) {
				Intent i = new Intent(AbstractDrawerActivity.this, BookListActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(i);
			}
		}

		/**
		 * Start {@link SummaryActivity}.
		 */
		private void startSummaryActivity() {
			closeDrawer();
			if (getDrawerActivity() != DrawerActivity.SUMMARY) {
				Intent i = new Intent(AbstractDrawerActivity.this, SummaryActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(i);
			}
		}

		/**
		 * Start {@link BookImportActivity}.
		 */
		private void startBookImportActivity() {
			closeDrawer();
			if (getDrawerActivity() != DrawerActivity.BOOK_IMPORT) {
				Intent i = new Intent(AbstractDrawerActivity.this, BookImportActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(i);
			}
		}

		/**
		 * Start {@link BookExportActivity}.
		 */
		private void startBookExportActivity() {
			closeDrawer();
			if (getDrawerActivity() != DrawerActivity.BOOK_EXPORT) {
				Intent i = new Intent(AbstractDrawerActivity.this, BookExportActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(i);
			}
		}

		/**
		 * Save a copy of the database file in the "Download" folder.
		 */
		private void saveDbOnDisk() {
			File backupDB = FileUtils.createFileInAppDir(Database.NAME + ".sqlite");

			boolean success = false;
			if (backupDB != null) {
				File currentDB = getDatabasePath(Database.NAME);
				success = FileUtils.copy(currentDB, backupDB);
			}

			if (success) {
				MediaScannerConnection.scanFile(AbstractDrawerActivity.this, new String[] { backupDB.getAbsolutePath() }, null,
						null);
				String message = String.format(getString(R.string.message_file_saved), backupDB.getName(), backupDB
						.getParentFile().getName());
				Toast.makeText(AbstractDrawerActivity.this, message, Toast.LENGTH_LONG).show();
				closeDrawer();
			} else {
				Toast.makeText(AbstractDrawerActivity.this, R.string.message_file_not_saved, Toast.LENGTH_LONG).show();
			}
		}
	}
}
