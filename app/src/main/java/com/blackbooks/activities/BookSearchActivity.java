package com.blackbooks.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.blackbooks.R;
import com.blackbooks.fragments.booksearch.BookSearchFragment;

/**
 * The activity that searches books in the library.
 */
public final class BookSearchActivity extends FragmentActivity {

    private static final String TAG_BOOK_SEARCH_FRAGMENT = "TAG_BOOK_SEARCH_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_book_search);
        Intent intent = getIntent();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            query = query.replace("*", "");

            String title = String.format(getString(R.string.title_activity_book_search), query);
            setTitle(title);

            FragmentManager fm = getSupportFragmentManager();

            BookSearchFragment fragment = (BookSearchFragment) fm.findFragmentByTag(TAG_BOOK_SEARCH_FRAGMENT);

            if (fragment == null) {
                fragment = BookSearchFragment.newInstance(query);
                fm.beginTransaction() //
                        .replace(R.id.bookSearch_frameLayout, fragment, TAG_BOOK_SEARCH_FRAGMENT) //
                        .commit();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result;
        switch (item.getItemId()) {
            case android.R.id.home:
                result = true;
                finish();
                break;

            default:
                result = super.onOptionsItemSelected(item);
        }
        return result;
    }
}
