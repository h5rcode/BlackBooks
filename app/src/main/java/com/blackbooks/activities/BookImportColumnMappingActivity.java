package com.blackbooks.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.blackbooks.R;
import com.blackbooks.fragments.bookimport.BookImportColumnMappingFragment;
import com.blackbooks.fragments.dialogs.ColumnSeparator;
import com.blackbooks.fragments.dialogs.TextQualifier;

import java.io.File;

/**
 * The activity that allows the user to map each column of a CSV file to a property of a book.
 */
public final class BookImportColumnMappingActivity extends FragmentActivity {

    public static final String EXTRA_FILE = "EXTRA_FILE";
    public static final String EXTRA_COLUMN_SEPARATOR = "EXTRA_COLUMN_SEPARATOR";
    public static final String EXTRA_TEXT_QUALIFIER = "EXTRA_TEXT_QUALIFIER";
    public static final String EXTRA_FIRST_ROW_CONTAINS_HEADER = "EXTRA_FIRST_ROW_CONTAINS_HEADER";

    private static final String BOOK_IMPORT_COLUMN_MAPPING_FRAGMENT_TAG = "BOOK_IMPORT_COLUMN_MAPPING_FRAGMENT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fm = getSupportFragmentManager();
        BookImportColumnMappingFragment fragment = (BookImportColumnMappingFragment) fm.findFragmentByTag(BOOK_IMPORT_COLUMN_MAPPING_FRAGMENT_TAG);

        if (fragment == null) {

            Intent i = getIntent();
            File file = (File) i.getSerializableExtra(EXTRA_FILE);
            ColumnSeparator columnSeparator = (ColumnSeparator) i.getSerializableExtra(EXTRA_COLUMN_SEPARATOR);
            TextQualifier textQualifier = (TextQualifier) i.getSerializableExtra(EXTRA_TEXT_QUALIFIER);
            boolean firstRowContainsHeader = i.getBooleanExtra(EXTRA_FIRST_ROW_CONTAINS_HEADER, false);

            fragment = BookImportColumnMappingFragment.newInstance(file, columnSeparator, textQualifier, firstRowContainsHeader);

            fm.beginTransaction() //
                    .add(R.id.fragmentActivity_frameLayout, fragment, BOOK_IMPORT_COLUMN_MAPPING_FRAGMENT_TAG) //
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
