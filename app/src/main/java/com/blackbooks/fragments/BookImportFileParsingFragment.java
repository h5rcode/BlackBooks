package com.blackbooks.fragments;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.fragments.dialogs.ColumnSeparator;
import com.blackbooks.fragments.dialogs.TextQualifier;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.nonpersistent.CsvColumn;
import com.blackbooks.services.BookServices;
import com.blackbooks.utils.CsvUtils;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * The fragment that will parse a CSV file according to the CSV column mapping settings
 * entered by the user.
 */
public final class BookImportFileParsingFragment extends Fragment {

    private static final String ARG_FILE = "ARG_FILE";
    private static final String ARG_COLUMN_SEPARATOR = "ARG_COLUMN_SEPARATOR";
    private static final String ARG_TEXT_QUALIFIER = "ARG_TEXT_QUALIFIER";
    private static final String ARG_FIRST_ROW_CONTAINS_HEADER = "ARG_FIRST_ROW_CONTAINS_HEADER";
    private static final String ARG_CSV_COLUMNS = "ARG_CSV_COLUMNS";

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
     * @param csvColumns             The CSV column mapping settings.
     */
    public static BookImportFileParsingFragment newInstance(
            File file, ColumnSeparator columnSeparator,
            TextQualifier textQualifier, boolean firstRowContainsHeader, List<CsvColumn> csvColumns) {

        BookImportFileParsingFragment fragment = new BookImportFileParsingFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FILE, file);
        args.putSerializable(ARG_COLUMN_SEPARATOR, columnSeparator);
        args.putSerializable(ARG_TEXT_QUALIFIER, textQualifier);
        args.putBoolean(ARG_FIRST_ROW_CONTAINS_HEADER, firstRowContainsHeader);
        args.putSerializable(ARG_CSV_COLUMNS, (Serializable) csvColumns);
        fragment.setArguments(args);

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
        mCsvColumns = (List<CsvColumn>) arguments.getSerializable(ARG_CSV_COLUMNS);


        List<BookInfo> bookInfos = CsvUtils.parseCsvFile(mFile, mColumnSeparator.getCharacter(),
                mTextQualifier.getCharacter(), mFirstRowContainsHeader, mCsvColumns);


        if (!bookInfos.isEmpty()) {
            SQLiteDatabase db = SQLiteHelper.getInstance().getWritableDatabase();

            for (BookInfo bookInfo : bookInfos) {
                BookServices.saveBookInfo(db, bookInfo);
            }
        }
    }
}
