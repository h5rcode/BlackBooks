package com.blackbooks.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.activities.BookImportFileParsingActivity;
import com.blackbooks.adapters.CsvColumnListAdapter;
import com.blackbooks.fragments.dialogs.ColumnSeparator;
import com.blackbooks.fragments.dialogs.TextQualifier;
import com.blackbooks.model.nonpersistent.CsvColumn;
import com.blackbooks.utils.CsvUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The fragment that allows the user to map each column of a CSV file to a property of a book.
 */
public final class BookImportColumnMappingFragment extends Fragment {

    private static final String ARG_FILE = "ARG_FILE";
    private static final String ARG_COLUMN_SEPARATOR = "ARG_COLUMN_SEPARATOR";
    private static final String ARG_TEXT_QUALIFIER = "ARG_TEXT_QUALIFIER";
    private static final String ARG_FIRST_ROW_CONTAINS_HEADER = "ARG_FIRST_ROW_CONTAINS_HEADER";

    private File mFile;
    private ColumnSeparator mColumnSeparator;
    private TextQualifier mTextQualifier;
    private boolean mFirstRowContainsHeader;
    private List<CsvColumn> mCsvColumns;

    /**
     * Constructor.
     *
     * @param file                   The CSV file.
     * @param columnSeparator        The column separator character.
     * @param textQualifier          The text qualifier character.
     * @param firstRowContainsHeader Boolean indicating if the first row of the file contains column headers.
     */
    public static BookImportColumnMappingFragment newInstance(
            File file, ColumnSeparator columnSeparator, TextQualifier textQualifier, boolean firstRowContainsHeader) {
        BookImportColumnMappingFragment fragment = new BookImportColumnMappingFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(ARG_FILE, file);
        arguments.putSerializable(ARG_COLUMN_SEPARATOR, columnSeparator);
        arguments.putSerializable(ARG_TEXT_QUALIFIER, textQualifier);
        arguments.putBoolean(ARG_FIRST_ROW_CONTAINS_HEADER, firstRowContainsHeader);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        Bundle arguments = getArguments();

        mFile = (File) arguments.getSerializable(ARG_FILE);
        mColumnSeparator = (ColumnSeparator) arguments.getSerializable(ARG_COLUMN_SEPARATOR);
        mTextQualifier = (TextQualifier) arguments.getSerializable(ARG_TEXT_QUALIFIER);
        mFirstRowContainsHeader = arguments.getBoolean(ARG_FIRST_ROW_CONTAINS_HEADER);
        mCsvColumns = CsvUtils.getCsvFileColumns(mFile, mColumnSeparator.getCharacter(), mTextQualifier.getCharacter());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_import_column_mapping, container, false);

        TextView textViewFile = (TextView) view.findViewById(R.id.bookImportColumnMapping_textFile);
        textViewFile.setText(mFile.getName());

        TextView textViewQualifier = (TextView) view.findViewById(R.id.bookImportColumnMapping_textQualifier);
        textViewQualifier.setText(mTextQualifier.getResourceId());

        TextView textViewSeparator = (TextView) view.findViewById(R.id.bookImportColumnMapping_textSeparator);
        textViewSeparator.setText(mColumnSeparator.getResourceId());

        ListView listViewColumns = (ListView) view.findViewById(R.id.bookImportColumnMapping_listColumns);
        CsvColumnListAdapter adapter = new CsvColumnListAdapter(getActivity());
        listViewColumns.setAdapter(adapter);
        adapter.addAll(mCsvColumns);

        CheckBox checkBoxFirstRowContainsHeader = (CheckBox) view.findViewById(R.id.bookImportColumnMapping_checkBoxFirstRowContainsHeaders);
        checkBoxFirstRowContainsHeader.setChecked(mFirstRowContainsHeader);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.book_import, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result;
        switch (item.getItemId()) {
            case R.id.bookImport_actionNextStep:
                result = true;

                boolean ok = checkMappings();
                if (ok) {
                    Intent i = new Intent(getActivity(), BookImportFileParsingActivity.class);
                    i.putExtra(BookImportFileParsingActivity.EXTRA_COLUMN_SEPARATOR, mColumnSeparator);
                    i.putExtra(BookImportFileParsingActivity.EXTRA_CSV_COLUMNS, (Serializable) mCsvColumns);
                    i.putExtra(BookImportFileParsingActivity.EXTRA_FILE, mFile);
                    i.putExtra(BookImportFileParsingActivity.EXTRA_FIRST_ROW_CONTAINS_HEADER, mFirstRowContainsHeader);
                    i.putExtra(BookImportFileParsingActivity.EXTRA_TEXT_QUALIFIER, mTextQualifier);
                    startActivity(i);
                }

                break;

            default:
                result = super.onOptionsItemSelected(item);
                break;
        }

        return result;
    }

    /**
     * Check if the CSV column mappings settings are correct.
     *
     * @return True if the column mappings settings are correct, false otherwise.
     */
    private boolean checkMappings() {
        boolean titleMapped = false;
        CsvColumn.BookProperty propertyMappedMultipleTimes = null;

        List<CsvColumn.BookProperty> mappedProperties = new ArrayList<CsvColumn.BookProperty>();
        for (CsvColumn csvColumn : mCsvColumns) {
            CsvColumn.BookProperty bookProperty = csvColumn.getBookProperty();
            if (bookProperty == CsvColumn.BookProperty.NONE) {
                continue;
            }
            if (bookProperty == CsvColumn.BookProperty.TITLE) {
                titleMapped = true;
            }
            if (mappedProperties.contains(bookProperty)) {
                propertyMappedMultipleTimes = bookProperty;
                break;
            } else {
                mappedProperties.add(bookProperty);
            }
        }

        boolean ok = true;
        if (!titleMapped) {
            ok = false;
            String bookPropertyDisplayName = getString(CsvUtils.getBookPropertyResourceId(CsvColumn.BookProperty.TITLE));
            String message = getString(R.string.message_book_property_not_mapped, bookPropertyDisplayName);
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }
        if (propertyMappedMultipleTimes != null) {
            ok = false;
            String bookPropertyDisplayName = getString(CsvUtils.getBookPropertyResourceId(propertyMappedMultipleTimes));
            String message = getString(R.string.message_book_property_mapped_multiple_times, bookPropertyDisplayName);
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }

        return ok;
    }
}
