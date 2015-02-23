package com.blackbooks.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.blackbooks.R;
import com.blackbooks.fragments.BookExportFragment;

/**
 * An activity to export books.
 */
public final class BookExportActivity extends FragmentActivity {

    private static final String BOOK_EXPORT_FRAGMENT_TAG = "BOOK_EXPORT_FRAGMENT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        FragmentManager fm = getSupportFragmentManager();

        BookExportFragment bookExportFragment = (BookExportFragment) fm.findFragmentByTag(BOOK_EXPORT_FRAGMENT_TAG);

        if (bookExportFragment == null) {
            bookExportFragment = new BookExportFragment();

            fm.beginTransaction() //
                    .add(R.id.fragmentActivity_frameLayout, bookExportFragment, BOOK_EXPORT_FRAGMENT_TAG) //
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
}
