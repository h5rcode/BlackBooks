package com.blackbooks.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.blackbooks.R;
import com.blackbooks.fragments.BookGroupListFragment;
import com.blackbooks.model.nonpersistent.BookGroup;

/**
 * The activity used to display the book groups of a certain type.
 */
public final class BookGroupListActivity extends FragmentActivity {

    public static final String EXTRA_GROUP_BOOK_TYPE = "EXTRA_GROUP_BOOK_TYPE";
    private static final String TAG_GROUP_LIST_FRAGMENT = "TAG_GROUP_LIST_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_group_list);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent i = getIntent();
        BookGroup.BookGroupType bookGroupType = (BookGroup.BookGroupType) i.getSerializableExtra(EXTRA_GROUP_BOOK_TYPE);
        setTitle(bookGroupType);

        FragmentManager fm = getSupportFragmentManager();
        BookGroupListFragment fragment = (BookGroupListFragment) fm.findFragmentByTag(TAG_GROUP_LIST_FRAGMENT);

        if (fragment == null) {
            fragment = BookGroupListFragment.newInstance(bookGroupType);

            fm.beginTransaction() //
                    .replace(R.id.activity_book_group_list_frameLayout, fragment, TAG_GROUP_LIST_FRAGMENT) //
                    .commit();
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
                break;
        }
        return result;
    }

    /**
     * Set the title of the activity.
     *
     * @param bookGroupType BookGroupType.
     */
    private void setTitle(BookGroup.BookGroupType bookGroupType) {
        int resId;
        switch (bookGroupType) {
            case AUTHOR:
                resId = R.string.title_activity_book_group_authors;
                break;

            case BOOK_LOCATION:
                resId = R.string.title_activity_book_group_book_locations;
                break;

            case CATEGORY:
                resId = R.string.title_activity_book_group_categories;
                break;

            case FIRST_LETTER:
                resId = R.string.title_activity_book_group_first_letter;
                break;

            case LANGUAGE:
                resId = R.string.title_activity_book_group_languages;
                break;

            case SERIES:
                resId = R.string.title_activity_book_group_series;
                break;

            default:
                throw new IllegalArgumentException(String.format("Invalid bookGroupType: %s.", bookGroupType));
        }

        setTitle(resId);
    }
}
