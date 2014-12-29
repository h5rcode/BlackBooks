package com.blackbooks.activities;

import android.app.ActionBar;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.blackbooks.R;
import com.blackbooks.fragments.AbstractBookListFragment;
import com.blackbooks.fragments.AbstractBookListFragment.BookListListener;
import com.blackbooks.fragments.BookListByAuthorFragment;
import com.blackbooks.fragments.BookListByBookShelfFragment;
import com.blackbooks.fragments.BookListByCategoryFragment;
import com.blackbooks.fragments.BookListByFirstLetterFragment;
import com.blackbooks.fragments.BookListByLanguageFragment;

/**
 * The book list activity. It hosts an AbstractBookListFragment used to display
 * list in various orders.
 */
public class BookListActivity extends AbstractDrawerActivity implements BookListListener {

	public static final String PREFERENCES = "PREFERENCES";
	public static final String PREF_DEFAULT_LIST = "PREF_DEFAULT_LIST";
	private static final String BOOK_LIST_FRAGMENT_TAG = "BOOK_LIST_FRAGMENT_TAG";

	private AbstractBookListFragment mCurrentFragment;

	@Override
	protected DrawerActivity getDrawerActivity() {
		return DrawerActivity.BOOK_LIST;
	}

	@Override
	protected void onResume() {
		super.onResume();

		SharedPreferences preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
		String defaultList = preferences.getString(PREF_DEFAULT_LIST, null);

		FragmentManager fm = getSupportFragmentManager();
		mCurrentFragment = (AbstractBookListFragment) fm.findFragmentByTag(BOOK_LIST_FRAGMENT_TAG);

		if (mCurrentFragment == null || !mCurrentFragment.getClass().getName().equals(defaultList)) {
			if (defaultList == null) {
				mCurrentFragment = new BookListByAuthorFragment();
			} else if (defaultList.equals(BookListByAuthorFragment.class.getName())) {
				mCurrentFragment = new BookListByAuthorFragment();
			} else if (defaultList.equals(BookListByFirstLetterFragment.class.getName())) {
				mCurrentFragment = new BookListByFirstLetterFragment();
			} else if (defaultList.equals(BookListByLanguageFragment.class.getName())) {
				mCurrentFragment = new BookListByLanguageFragment();
			} else if (defaultList.equals(BookListByCategoryFragment.class.getName())) {
				mCurrentFragment = new BookListByCategoryFragment();
			} else if (defaultList.equals(BookListByBookShelfFragment.class.getName())) {
				mCurrentFragment = new BookListByBookShelfFragment();
			} else {
				mCurrentFragment = new BookListByAuthorFragment();
			}

			fm.beginTransaction() //
					.replace(R.id.abstractDrawerActivity_frameLayout, mCurrentFragment, BOOK_LIST_FRAGMENT_TAG) //
					.commit();
		} else {
			setActionBarSubtitle();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.book_list, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.bookList_actionSearch).getActionView();
		ComponentName componentName = getComponentName();
		SearchableInfo searchableInfo = searchManager.getSearchableInfo(componentName);
		searchView.setSearchableInfo(searchableInfo);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		boolean result = true;

		switch (item.getItemId()) {
		case R.id.bookList_actionSortByAuthor:
			sortByAuthor();
			break;

		case R.id.bookList_actionSortByFirstLetter:
			sortByFirstLetter();
			break;

		case R.id.bookList_actionSortByCategory:
			sortByFirstCategory();
			break;

		case R.id.bookList_actionSortByLanguage:
			sortByLanguage();
			break;

		case R.id.bookList_actionSortByBookshelf:
			sortByBookshelf();
			break;

		case R.id.bookList_actionSearch:
			break;

		default:
			result = super.onOptionsItemSelected(item);
			break;
		}
		return result;
	}

	/**
	 * Set the action bar subtitle.
	 */
	private void setActionBarSubtitle() {
		ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(mCurrentFragment.getActionBarSubtitle());
	}

	private void sortByAuthor() {
		if (!(mCurrentFragment instanceof BookListByAuthorFragment)) {
			SharedPreferences sharedPref = getSharedPreferences(BookListActivity.PREFERENCES, MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString(BookListActivity.PREF_DEFAULT_LIST, BookListByAuthorFragment.class.getName());
			editor.commit();
			mCurrentFragment = new BookListByAuthorFragment();
			getSupportFragmentManager().beginTransaction() //
					.replace(R.id.abstractDrawerActivity_frameLayout, mCurrentFragment, BOOK_LIST_FRAGMENT_TAG) //
					.commit();
		}
	}

	private void sortByFirstLetter() {
		if (!(mCurrentFragment instanceof BookListByFirstLetterFragment)) {
			SharedPreferences sharedPref = getSharedPreferences(BookListActivity.PREFERENCES, MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString(BookListActivity.PREF_DEFAULT_LIST, BookListByFirstLetterFragment.class.getName());
			editor.commit();
			mCurrentFragment = new BookListByFirstLetterFragment();
			getSupportFragmentManager().beginTransaction() //
					.replace(R.id.abstractDrawerActivity_frameLayout, mCurrentFragment, BOOK_LIST_FRAGMENT_TAG) //
					.commit();
		}
	}

	private void sortByFirstCategory() {
		if (!(mCurrentFragment instanceof BookListByCategoryFragment)) {
			SharedPreferences sharedPref = getSharedPreferences(BookListActivity.PREFERENCES, MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString(BookListActivity.PREF_DEFAULT_LIST, BookListByCategoryFragment.class.getName());
			editor.commit();
			mCurrentFragment = new BookListByCategoryFragment();
			getSupportFragmentManager().beginTransaction() //
					.replace(R.id.abstractDrawerActivity_frameLayout, mCurrentFragment, BOOK_LIST_FRAGMENT_TAG) //
					.commit();
		}
	}

	private void sortByLanguage() {
		if (!(mCurrentFragment instanceof BookListByLanguageFragment)) {
			SharedPreferences sharedPref = getSharedPreferences(BookListActivity.PREFERENCES, MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString(BookListActivity.PREF_DEFAULT_LIST, BookListByLanguageFragment.class.getName());
			editor.commit();
			mCurrentFragment = new BookListByLanguageFragment();
			getSupportFragmentManager().beginTransaction() //
					.replace(R.id.abstractDrawerActivity_frameLayout, mCurrentFragment, BOOK_LIST_FRAGMENT_TAG) //
					.commit();
		}
	}

	private void sortByBookshelf() {
		if (!(mCurrentFragment instanceof BookListByBookShelfFragment)) {
			SharedPreferences sharedPref = getSharedPreferences(BookListActivity.PREFERENCES, MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString(BookListActivity.PREF_DEFAULT_LIST, BookListByBookShelfFragment.class.getName());
			editor.commit();
			mCurrentFragment = new BookListByBookShelfFragment();
			getSupportFragmentManager().beginTransaction() //
					.replace(R.id.abstractDrawerActivity_frameLayout, mCurrentFragment, BOOK_LIST_FRAGMENT_TAG) //
					.commit();
		}
	}

	@Override
	public void onBookListLoaded() {
		setActionBarSubtitle();
	}
}
