package com.blackbooks.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.blackbooks.R;
import com.blackbooks.fragments.BookImportFragment;

/**
 * An activity to import books.
 */
public final class BookImportActivity extends FragmentActivity {

    private static final String BOOK_IMPORT_FRAGMENT_TAG = "BOOK_IMPORT_FRAGMENT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        FragmentManager fm = getSupportFragmentManager();
        BookImportFragment bookImportFragment = (BookImportFragment) fm.findFragmentByTag(BOOK_IMPORT_FRAGMENT_TAG);

        if (bookImportFragment == null) {

            bookImportFragment = new BookImportFragment();

            fm.beginTransaction() //
                    .add(R.id.fragmentActivity_frameLayout, bookImportFragment, BOOK_IMPORT_FRAGMENT_TAG) //
                    .commit();
        }
    }
}
