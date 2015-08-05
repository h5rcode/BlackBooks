package com.blackbooks.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.blackbooks.R;
import com.blackbooks.fragments.BookImportFileParsingFragment;
import com.blackbooks.fragments.dialogs.ColumnSeparator;
import com.blackbooks.fragments.dialogs.TextQualifier;
import com.blackbooks.model.nonpersistent.CsvColumn;

import java.io.File;
import java.util.List;

/**
 * The activity where a CSV file is parsed based on the settings entered by the user
 * at the BookImportColumnMappingActivity.
 */
public final class BookImportFileParsingActivity extends FragmentActivity {

    public static final String EXTRA_FILE = "EXTRA_FILE";
    public static final String EXTRA_COLUMN_SEPARATOR = "EXTRA_COLUMN_SEPARATOR";
    public static final String EXTRA_TEXT_QUALIFIER = "EXTRA_TEXT_QUALIFIER";
    public static final String EXTRA_FIRST_ROW_CONTAINS_HEADER = "EXTRA_FIRST_ROW_CONTAINS_HEADER";
    public static final String EXTRA_CSV_COLUMNS = "EXTRA_CSV_COLUMNS";

    private static final String BOOK_IMPORT_FILE_PARSING_FRAGMENT_TAG = "BOOK_IMPORT_FILE_PARSING_FRAGMENT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        FragmentManager fm = getSupportFragmentManager();
        BookImportFileParsingFragment fragment = (BookImportFileParsingFragment) fm.findFragmentByTag(BOOK_IMPORT_FILE_PARSING_FRAGMENT_TAG);

        if (fragment == null) {

            Intent i = getIntent();
            File file = (File) i.getSerializableExtra(EXTRA_FILE);
            ColumnSeparator columnSeparator = (ColumnSeparator) i.getSerializableExtra(EXTRA_COLUMN_SEPARATOR);
            TextQualifier textQualifier = (TextQualifier) i.getSerializableExtra(EXTRA_TEXT_QUALIFIER);
            boolean firstRowContainsHeader = i.getBooleanExtra(EXTRA_FIRST_ROW_CONTAINS_HEADER, false);
            List<CsvColumn> csvColumns = (List<CsvColumn>) i.getSerializableExtra(EXTRA_CSV_COLUMNS);

            fragment = BookImportFileParsingFragment.newInstance(
                    file, columnSeparator, textQualifier, firstRowContainsHeader, csvColumns);

            fm.beginTransaction() //
                    .add(R.id.fragmentActivity_frameLayout, fragment, BOOK_IMPORT_FILE_PARSING_FRAGMENT_TAG) //
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
