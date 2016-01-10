package com.blackbooks.fragments;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
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
import com.blackbooks.activities.SummaryActivity;
import com.blackbooks.adapters.CsvColumnListAdapter;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.fragments.dialogs.ColumnSeparator;
import com.blackbooks.fragments.dialogs.ProgressDialogFragment;
import com.blackbooks.fragments.dialogs.TextQualifier;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.nonpersistent.CsvColumn;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.services.BookServices;
import com.blackbooks.utils.CsvUtils;
import com.blackbooks.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The fragment that allows the user to map each column of a CSV file to a property of a book.
 */
public final class BookImportColumnMappingFragment extends Fragment implements ProgressDialogFragment.OnProgressDialogListener {

    private static final String ARG_FILE = "ARG_FILE";
    private static final String ARG_COLUMN_SEPARATOR = "ARG_COLUMN_SEPARATOR";
    private static final String ARG_TEXT_QUALIFIER = "ARG_TEXT_QUALIFIER";
    private static final String ARG_FIRST_ROW_CONTAINS_HEADER = "ARG_FIRST_ROW_CONTAINS_HEADER";
    private static final String TAG_PROGRESS_DIALOG_FRAGMENT = "TAG_PROGRESS_DIALOG_FRAGMENT";

    private File mFile;
    private ColumnSeparator mColumnSeparator;
    private TextQualifier mTextQualifier;
    private boolean mFirstRowContainsHeader;
    private List<CsvColumn> mCsvColumns;

    private CsvParsingTask mCsvParsingTask;

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
    public void onDestroy() {
        super.onDestroy();
        cancelAsyncTask();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.book_import_column_mapping, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result;
        switch (item.getItemId()) {
            case R.id.bookImportColumnMapping_actionImport:
                result = true;

                boolean ok = checkMappings();
                if (ok) {
                    mCsvParsingTask = new CsvParsingTask(
                            mFile, mColumnSeparator.getCharacter(), mTextQualifier.getCharacter(),
                            mFirstRowContainsHeader, mCsvColumns);
                    mCsvParsingTask.execute();
                }

                break;

            default:
                result = super.onOptionsItemSelected(item);
                break;
        }

        return result;
    }

    @Override
    public void onCancel() {
        cancelAsyncTask();
    }

    /**
     * Cancel the asynchronous task.
     */
    private void cancelAsyncTask() {
        if (mCsvParsingTask != null) {
            mCsvParsingTask.cancel(true);
        }
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

    /**
     * The asynchronous task that will parse the CSV file and save the list of books read from it.
     */
    private final class CsvParsingTask extends AsyncTask<Void, Integer, Void> {

        private final File mFile;
        private final char mColumnSeparator;
        private final char mTextQualifier;
        private final boolean mFirstRowContainsHeader;
        private final List<CsvColumn> mCsvColumns;

        private final ProgressDialogFragment mProgressDialogFragment;


        /**
         * Constructor.
         *
         * @param file                   The CSV file to parse.
         * @param columnSeparator        The column separator character.
         * @param textQualifier          The text qualifier character.
         * @param firstRowContainsHeader A boolean indicating whether the first row of the CSV file
         *                               contains headers or not.
         * @param csvColumns             The CSV columns mapping settings.
         */
        public CsvParsingTask(
                File file, char columnSeparator, char textQualifier, boolean firstRowContainsHeader,
                List<CsvColumn> csvColumns) {
            mFile = file;
            mColumnSeparator = columnSeparator;
            mTextQualifier = textQualifier;
            mFirstRowContainsHeader = firstRowContainsHeader;
            mCsvColumns = csvColumns;

            mProgressDialogFragment = new ProgressDialogFragment();
            mProgressDialogFragment.setTargetFragment(BookImportColumnMappingFragment.this, 0);
            mProgressDialogFragment.setTitle(R.string.title_dialog_save_parsed_books);
            mProgressDialogFragment.setMessage(R.string.message_save_parsed_books);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            List<BookInfo> bookInfoList;
            try {
                String message = String.format("Parsing file '%s' (column separator: '%c', text qualifier: '%c', first row contains headers: %b, column mappings: %s).", mFile.getAbsolutePath(), mColumnSeparator, mTextQualifier, mFirstRowContainsHeader, mCsvColumns);
                Log.d(LogUtils.TAG, message);

                bookInfoList = CsvUtils.parseCsvFile(mFile, mColumnSeparator, mTextQualifier, mFirstRowContainsHeader, mCsvColumns);

                Log.d(LogUtils.TAG, String.format("Parsing finished. %d books read.", bookInfoList.size()));
            } catch (InterruptedException e) {
                return null;
            }

            if (!bookInfoList.isEmpty()) {

                mProgressDialogFragment.setMax(bookInfoList.size());
                final FragmentManager fm = getActivity().getSupportFragmentManager();
                mProgressDialogFragment.show(fm, TAG_PROGRESS_DIALOG_FRAGMENT);

                final SQLiteDatabase db = SQLiteHelper.getInstance().getWritableDatabase();
                db.beginTransaction();
                try {
                    int i = 0;
                    boolean isCancelled = false;
                    for (final BookInfo bookInfo : bookInfoList) {
                        if (isCancelled = isCancelled()) {
                            Log.d(LogUtils.TAG, "Book import task cancelled, aborting.");
                            break;
                        }

                        processBookInfo(db, bookInfo);
                        i++;
                        publishProgress(i);
                    }

                    if (!isCancelled) {
                        Log.d(LogUtils.TAG, "Book import finished successfully.");
                        db.setTransactionSuccessful();
                    }
                } finally {
                    db.endTransaction();
                }
            }

            return null;
        }

        /**
         * Process a parsed book and save it in the database if everything is fine.
         *
         * @param db       SQLiteDatabase.
         * @param bookInfo The book.
         */
        private void processBookInfo(SQLiteDatabase db, BookInfo bookInfo) {
            boolean ok = true;
            if (bookInfo.id != null) {
                final Book book = BookServices.getBook(db, bookInfo.id);
                if (book == null) {
                    ok = false;
                    final String msg = String.format(
                            "Book %s cannot be updated because there is no row in table %s with the id %d.",
                            bookInfo.title, Book.NAME, bookInfo.id);
                    Log.w(LogUtils.TAG, msg);
                }
            }

            if (ok) {
                Log.d(LogUtils.TAG, String.format("Saving book '%s'.", bookInfo.title));
                BookServices.saveBookInfo(db, bookInfo);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int progress = values[0];
            mProgressDialogFragment.setProgress(progress);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgressDialogFragment.dismiss();

            Intent i = new Intent(getActivity(), SummaryActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }
}
