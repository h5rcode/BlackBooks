package com.blackbooks.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

import com.blackbooks.R;
import com.blackbooks.fragments.AbstractBookListFragment2;
import com.blackbooks.fragments.BookListByAuthorFragment2;
import com.blackbooks.fragments.BookListByBookLocationFragment2;
import com.blackbooks.fragments.BookListByCategoryFragment2;
import com.blackbooks.fragments.BookListByFirstLetterFragment2;
import com.blackbooks.fragments.BookListByLanguageFragment2;
import com.blackbooks.fragments.BookListByLoanedFragment2;
import com.blackbooks.fragments.BookListBySeriesFragment2;
import com.blackbooks.fragments.BookListFavouriteFragment;
import com.blackbooks.model.nonpersistent.BookGroup;

import java.io.Serializable;

/**
 * The activity used to display the books of a particular group.
 */
public final class BookListActivity2 extends FragmentActivity {

    public static final String EXTRA_BOOK_GROUP_TYPE = "EXTRA_BOOK_GROUP_TYPE";
    public static final String EXTRA_BOOK_GROUP_ID = "EXTRA_BOOK_GROUP_ID";
    private static final String TAG_BOOK_LIST_FRAGMENT = "TAG_BOOK_LIST_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_group_list);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        FragmentManager fm = getSupportFragmentManager();
        AbstractBookListFragment2 fragment = (AbstractBookListFragment2) fm.findFragmentByTag(TAG_BOOK_LIST_FRAGMENT);

        if (fragment == null) {
            Intent i = getIntent();
            BookGroup.BookGroupType bookGroupType = (BookGroup.BookGroupType) i.getSerializableExtra(EXTRA_BOOK_GROUP_TYPE);
            Serializable bookGroupId = i.getSerializableExtra(EXTRA_BOOK_GROUP_ID);

            switch (bookGroupType) {
                case AUTHOR:
                    fragment = BookListByAuthorFragment2.newInstance((Long) bookGroupId);
                    break;
                case BOOK_LOCATION:
                    fragment = BookListByBookLocationFragment2.newInstance((Long) bookGroupId);
                    break;
                case CATEGORY:
                    fragment = BookListByCategoryFragment2.newInstance((Long) bookGroupId);
                    break;
                case FAVOURITE:
                    fragment = new BookListFavouriteFragment();
                    break;
                case FIRST_LETTER:
                    fragment = BookListByFirstLetterFragment2.newInstance((String) bookGroupId);
                    break;
                case LANGUAGE:
                    fragment = BookListByLanguageFragment2.newInstance((String) bookGroupId);
                    break;
                case LOANED:
                    fragment = BookListByLoanedFragment2.newInstance((String) bookGroupId);
                    break;
                case SERIES:
                    fragment = BookListBySeriesFragment2.newInstance((Long) bookGroupId);
                    break;
            }

            fm.beginTransaction() //
                    .replace(R.id.activity_book_group_list_frameLayout, fragment, TAG_BOOK_LIST_FRAGMENT) //
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
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
                break;
        }
        return result;
    }
}
