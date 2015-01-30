package com.blackbooks.activities;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.blackbooks.R;
import com.blackbooks.fragments.AbstractBookListFragment;
import com.blackbooks.fragments.BookListByAuthorFragment;
import com.blackbooks.fragments.BookListByBookLocationFragment;
import com.blackbooks.fragments.BookListByCategoryFragment;
import com.blackbooks.fragments.BookListByFavouriteFragment;
import com.blackbooks.fragments.BookListByFirstLetterFragment;
import com.blackbooks.fragments.BookListByLanguageFragment;
import com.blackbooks.fragments.BookListByLoanedFragment;
import com.blackbooks.fragments.BookListBySeriesFragment;
import com.blackbooks.fragments.BookListByToReadReadFragment;

/**
 * The book list activity. It hosts an AbstractBookListFragment used to display
 * list in various orders.
 */
public class BookListActivity extends AbstractDrawerActivity {

    public static final String PREFERENCES = "PREFERENCES";
    public static final String PREF_DEFAULT_LIST = "PREF_DEFAULT_LIST";
    private static final String BOOK_LIST_FRAGMENT_TAG = "BOOK_LIST_FRAGMENT_TAG";

    private AbstractBookListFragment mCurrentFragment;

    @Override
    protected DrawerActivity getDrawerActivity() {
        return DrawerActivity.BOOK_LIST;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.menu_book_list);
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
                mCurrentFragment = new BookListByFirstLetterFragment();
            } else if (defaultList.equals(BookListByAuthorFragment.class.getName())) {
                mCurrentFragment = new BookListByAuthorFragment();
            } else if (defaultList.equals(BookListByFirstLetterFragment.class.getName())) {
                mCurrentFragment = new BookListByFirstLetterFragment();
            } else if (defaultList.equals(BookListByLanguageFragment.class.getName())) {
                mCurrentFragment = new BookListByLanguageFragment();
            } else if (defaultList.equals(BookListByCategoryFragment.class.getName())) {
                mCurrentFragment = new BookListByCategoryFragment();
            } else if (defaultList.equals(BookListByBookLocationFragment.class.getName())) {
                mCurrentFragment = new BookListByBookLocationFragment();
            } else if (defaultList.equals(BookListBySeriesFragment.class.getName())) {
                mCurrentFragment = new BookListBySeriesFragment();
            } else if (defaultList.equals(BookListByToReadReadFragment.class.getName())) {
                mCurrentFragment = new BookListByToReadReadFragment();
            } else if (defaultList.equals(BookListByLoanedFragment.class.getName())) {
                mCurrentFragment = new BookListByLoanedFragment();
            } else if (defaultList.equals(BookListByFavouriteFragment.class.getName())) {
                mCurrentFragment = new BookListByFavouriteFragment();
            } else {
                mCurrentFragment = new BookListByFirstLetterFragment();
            }

            fm.beginTransaction() //
                    .replace(R.id.abstractDrawerActivity_frameLayout, mCurrentFragment, BOOK_LIST_FRAGMENT_TAG) //
                    .commit();
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

            case R.id.bookList_actionSortBySeries:
                sortBySeries();
                break;

            case R.id.bookList_actionSortByBookLocation:
                sortByBookLocation();
                break;

            case R.id.bookList_actionSortByToReadRead:
                sortByReadNotRead();
                break;

            case R.id.bookList_actionSortByLoaned:
                sortByLoaned();
                break;

            case R.id.bookList_actionSortByFavourite:
                sortByFavourite();
                break;

            case R.id.bookList_actionSearch:
                break;

            default:
                result = super.onOptionsItemSelected(item);
                break;
        }
        return result;
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

    private void sortBySeries() {
        if (!(mCurrentFragment instanceof BookListBySeriesFragment)) {
            SharedPreferences sharedPref = getSharedPreferences(BookListActivity.PREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(BookListActivity.PREF_DEFAULT_LIST, BookListBySeriesFragment.class.getName());
            editor.commit();
            mCurrentFragment = new BookListBySeriesFragment();
            getSupportFragmentManager().beginTransaction() //
                    .replace(R.id.abstractDrawerActivity_frameLayout, mCurrentFragment, BOOK_LIST_FRAGMENT_TAG) //
                    .commit();
        }
    }

    private void sortByBookLocation() {
        if (!(mCurrentFragment instanceof BookListByBookLocationFragment)) {
            SharedPreferences sharedPref = getSharedPreferences(BookListActivity.PREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(BookListActivity.PREF_DEFAULT_LIST, BookListByBookLocationFragment.class.getName());
            editor.commit();
            mCurrentFragment = new BookListByBookLocationFragment();
            getSupportFragmentManager().beginTransaction() //
                    .replace(R.id.abstractDrawerActivity_frameLayout, mCurrentFragment, BOOK_LIST_FRAGMENT_TAG) //
                    .commit();
        }
    }

    private void sortByReadNotRead() {
        if (!(mCurrentFragment instanceof BookListByToReadReadFragment)) {
            SharedPreferences sharedPref = getSharedPreferences(BookListActivity.PREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(BookListActivity.PREF_DEFAULT_LIST, BookListByToReadReadFragment.class.getName());
            editor.commit();
            mCurrentFragment = new BookListByToReadReadFragment();
            getSupportFragmentManager().beginTransaction() //
                    .replace(R.id.abstractDrawerActivity_frameLayout, mCurrentFragment, BOOK_LIST_FRAGMENT_TAG) //
                    .commit();
        }
    }

    private void sortByLoaned() {
        if (!(mCurrentFragment instanceof BookListByLoanedFragment)) {
            SharedPreferences sharedPref = getSharedPreferences(BookListActivity.PREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(BookListActivity.PREF_DEFAULT_LIST, BookListByLoanedFragment.class.getName());
            editor.commit();
            mCurrentFragment = new BookListByLoanedFragment();
            getSupportFragmentManager().beginTransaction() //
                    .replace(R.id.abstractDrawerActivity_frameLayout, mCurrentFragment, BOOK_LIST_FRAGMENT_TAG) //
                    .commit();
        }
    }

    private void sortByFavourite() {
        if (!(mCurrentFragment instanceof BookListByFavouriteFragment)) {
            SharedPreferences sharedPref = getSharedPreferences(BookListActivity.PREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(BookListActivity.PREF_DEFAULT_LIST, BookListByFavouriteFragment.class.getName());
            editor.commit();
            mCurrentFragment = new BookListByFavouriteFragment();
            getSupportFragmentManager().beginTransaction() //
                    .replace(R.id.abstractDrawerActivity_frameLayout, mCurrentFragment, BOOK_LIST_FRAGMENT_TAG) //
                    .commit();
        }
    }
}
