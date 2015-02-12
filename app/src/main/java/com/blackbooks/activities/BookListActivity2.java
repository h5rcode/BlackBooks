package com.blackbooks.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

import com.blackbooks.R;
import com.blackbooks.fragments.BookListFragment;
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
        BookListFragment fragment = (BookListFragment) fm.findFragmentByTag(TAG_BOOK_LIST_FRAGMENT);

        if (fragment == null) {
            Intent i = getIntent();
            BookGroup.BookGroupType bookGroupType = (BookGroup.BookGroupType) i.getSerializableExtra(EXTRA_BOOK_GROUP_TYPE);
            Serializable bookGroupId = i.getSerializableExtra(EXTRA_BOOK_GROUP_ID);

            fragment = BookListFragment.newInstance(bookGroupType, bookGroupId);

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
