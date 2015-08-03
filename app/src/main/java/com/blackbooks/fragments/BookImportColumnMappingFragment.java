package com.blackbooks.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.blackbooks.R;
import com.blackbooks.adapters.CsvColumnListAdapter;
import com.blackbooks.fragments.dialogs.ColumnSeparator;
import com.blackbooks.fragments.dialogs.TextQualifier;
import com.blackbooks.model.nonpersistent.CsvColumn;
import com.blackbooks.utils.CsvUtils;

import java.io.File;
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
    private List<CsvColumn> mColumns;

    /**
     * Constructor.
     *
     * @param file            The CSV file.
     * @param columnSeparator The column separator character.
     * @param textQualifier   The text qualifier character.
     */
    public static BookImportColumnMappingFragment newInstance(File file, ColumnSeparator columnSeparator, TextQualifier textQualifier, boolean firstRowContainsHeader) {
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

        Bundle arguments = getArguments();

        mFile = (File) arguments.getSerializable(ARG_FILE);
        mColumnSeparator = (ColumnSeparator) arguments.getSerializable(ARG_COLUMN_SEPARATOR);
        mTextQualifier = (TextQualifier) arguments.getSerializable(ARG_TEXT_QUALIFIER);
        mFirstRowContainsHeader = arguments.getBoolean(ARG_FIRST_ROW_CONTAINS_HEADER);
        mColumns = CsvUtils.getCsvFileColumns(mFile, mColumnSeparator.getCharacter(), mTextQualifier.getCharacter());

        int a = 3;
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
        adapter.addAll(mColumns);

        CheckBox checkBoxFirstRowContainsHeader = (CheckBox) view.findViewById(R.id.bookImportColumnMapping_checkBoxFirstRowContainsHeaders);
        checkBoxFirstRowContainsHeader.setChecked(mFirstRowContainsHeader);

        return view;
    }
}
