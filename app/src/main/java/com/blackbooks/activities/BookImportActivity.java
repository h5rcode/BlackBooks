package com.blackbooks.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.blackbooks.R;
import com.blackbooks.fragments.BookImportFragment;

/**
 * An activity to import books.
 */
public class BookImportActivity extends AbstractDrawerActivity {

    private static final String BOOK_IMPORT_FRAGMENT_TAG = "BOOK_IMPORT_FRAGMENT_TAG";

    @Override
    protected DrawerActivity getDrawerActivity() {
        return DrawerActivity.BOOK_IMPORT;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();
        BookImportFragment bookImportFragment = (BookImportFragment) fm.findFragmentByTag(BOOK_IMPORT_FRAGMENT_TAG);

        if (bookImportFragment == null) {

            bookImportFragment = new BookImportFragment();

            fm.beginTransaction() //
                    .add(R.id.abstractDrawerActivity_frameLayout, bookImportFragment, BOOK_IMPORT_FRAGMENT_TAG) //
                    .commit();
        }
    }
}
