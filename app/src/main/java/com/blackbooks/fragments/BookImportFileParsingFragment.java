package com.blackbooks.fragments;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.blackbooks.R;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.fragments.dialogs.ColumnSeparator;
import com.blackbooks.fragments.dialogs.ProgressDialogFragment;
import com.blackbooks.fragments.dialogs.TextQualifier;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.nonpersistent.CsvColumn;
import com.blackbooks.services.BookServices;
import com.blackbooks.utils.CsvUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
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

    private static final String TAG_PROGRESS_DIALOG_FRAGMENT = "TAG_PROGRESS_DIALOG_FRAGMENT";

    private List<BookInfo> mBookInfos;

    private CsvParsingTask mCsvParsingTask;
    private BookSavingTask mBookSavingTask;


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

        final BookImportFileParsingFragment fragment = new BookImportFileParsingFragment();
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

        final Bundle arguments = getArguments();

        final File file = (File) arguments.getSerializable(ARG_FILE);
        final ColumnSeparator columnSeparator = (ColumnSeparator) arguments.getSerializable(ARG_COLUMN_SEPARATOR);
        final TextQualifier textQualifier = (TextQualifier) arguments.getSerializable(ARG_TEXT_QUALIFIER);
        final boolean firstRowContainsHeader = arguments.getBoolean(ARG_FIRST_ROW_CONTAINS_HEADER);
        final List<CsvColumn> csvColumns = (List<CsvColumn>) arguments.getSerializable(ARG_CSV_COLUMNS);

        mCsvParsingTask = new CsvParsingTask(
                file, columnSeparator.getCharacter(), textQualifier.getCharacter(),
                firstRowContainsHeader, csvColumns);
        mCsvParsingTask.execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelAsyncTasks();
    }

    /**
     * Cancel the asynchronous tasks.
     */
    private void cancelAsyncTasks() {
        if (mCsvParsingTask != null) {
            mCsvParsingTask.cancel(true);
        }
        if (mBookSavingTask != null) {
            mBookSavingTask.cancel(true);
        }
    }

    /**
     * The asynchronous task responsible for saving a list of books to the database.
     */
    private final class BookSavingTask extends AsyncTask<Void, Integer, Void> {

        private final List<BookInfo> mBookInfos;
        private final ProgressDialogFragment mProgressDialogFragment;

        /**
         * Constructor.
         *
         * @param bookInfos The books to save.
         */
        public BookSavingTask(List<BookInfo> bookInfos) {
            mBookInfos = bookInfos;
            mProgressDialogFragment = new ProgressDialogFragment();
            mProgressDialogFragment.setTitle(R.string.title_dialog_save_parsed_books);
            mProgressDialogFragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    cancelAsyncTasks();
                }
            });
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialogFragment.setMax(mBookInfos.size());
            FragmentManager fm = getActivity().getSupportFragmentManager();
            mProgressDialogFragment.show(fm, TAG_PROGRESS_DIALOG_FRAGMENT);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (!mBookInfos.isEmpty()) {
                final SQLiteDatabase db = SQLiteHelper.getInstance().getWritableDatabase();
                int i = 0;
                for (final BookInfo bookInfo : mBookInfos) {

                    if (isCancelled()) {
                        break;
                    }

                    BookServices.saveBookInfo(db, bookInfo);

                    i++;
                    publishProgress(i);
                }
            }
            return null;
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
        }
    }

    /**
     * The asynchronous task that will parse the CSV file and return the list of books read from it.
     */
    private final class CsvParsingTask extends AsyncTask<Void, Void, List<BookInfo>> {

        private final File mFile;
        private final char mColumnSeparator;
        private final char mTextQualifier;
        private final boolean mFirstRowContainsHeader;
        private final List<CsvColumn> mCsvColumns;

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
        }

        @Override
        protected List<BookInfo> doInBackground(Void... voids) {
            List<BookInfo> bookInfos = null;
            try {
                bookInfos = CsvUtils.parseCsvFile(mFile, mColumnSeparator, mTextQualifier, mFirstRowContainsHeader, mCsvColumns);
            } catch (InterruptedException e) {
                // Do nothing. The method will terminate and return an empty list of books.
            } finally {
                if (bookInfos == null) {
                    bookInfos = new ArrayList<BookInfo>();
                }
            }
            return bookInfos;
        }

        @Override
        protected void onPostExecute(List<BookInfo> result) {
            super.onPostExecute(result);
            mBookInfos = result;

            // TODO Call BookSavingTask outside of this class.
            mBookSavingTask = new BookSavingTask(mBookInfos);
            mBookSavingTask.execute();
        }
    }
}
