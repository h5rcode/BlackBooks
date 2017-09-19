package com.blackbooks.fragments.bookexport;

import android.content.Context;
import android.media.MediaScannerConnection;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.fragments.dialogs.ColumnSeparator;
import com.blackbooks.fragments.dialogs.ColumnSeparatorPicker;
import com.blackbooks.fragments.dialogs.ColumnSeparatorPicker.ColumnSeparatorPickerListener;
import com.blackbooks.fragments.dialogs.ProgressDialogFragment;
import com.blackbooks.fragments.dialogs.TextQualifier;
import com.blackbooks.fragments.dialogs.TextQualifierPicker;
import com.blackbooks.fragments.dialogs.TextQualifierPicker.TextQualifierPickerListener;
import com.blackbooks.model.nonpersistent.BookExport;
import com.blackbooks.services.ExportService;
import com.blackbooks.utils.FileUtils;
import com.blackbooks.utils.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

/**
 * Fragment where the user can export the list of books as a CSV file.
 */
public final class BookExportFragment extends Fragment implements TextQualifierPickerListener, ColumnSeparatorPickerListener, ProgressDialogFragment.OnProgressDialogListener {

    private static final String TAG_TEXT_QUALIFIER_PICKER = "TAG_TEXT_QUALIFIER_PICKER";
    private static final String TAG_COLUMN_SEPARATOR_PICKER = "TAG_COLUMN_SEPARATOR_PICKER";
    private static final String TAG_PROGRESS_DIALOG_FRAGMENT = "TAG_PROGRESS_DIALOG_FRAGMENT";

    private final TextQualifierPicker mTextQualifierPicker;
    private final ColumnSeparatorPicker mColumnSeparatorPicker;
    private LinearLayout mLayoutQualifier;
    private LinearLayout mLayoutSeparator;
    private TextView mTextViewQualifier;
    private TextView mTextViewSeparator;
    private CheckBox mCheckBoxFirstRowContainsHeaders;
    private TextView mTextPreview;
    private char mTextQualifier;
    private char mColumnSeparator;
    private boolean mFirstRowContainsHeader = true;

    private CsvExportTask mCsvExportTask;

    @Inject
    ExportService exportService;

    public BookExportFragment() {
        super();

        mTextQualifierPicker = new TextQualifierPicker();
        mTextQualifierPicker.setTargetFragment(this, 0);

        mColumnSeparatorPicker = new ColumnSeparatorPicker();
        mColumnSeparatorPicker.setTargetFragment(this, 0);
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_export, container, false);

        mLayoutQualifier = (LinearLayout) view.findViewById(R.id.bookExport_layoutQualifier);
        mLayoutSeparator = (LinearLayout) view.findViewById(R.id.bookExport_layoutSeparator);

        mTextViewQualifier = (TextView) view.findViewById(R.id.bookExport_textQualifier);
        mTextViewQualifier.setText(mTextQualifierPicker.getSelectedTextQualifier().getResourceId());
        mTextQualifier = mTextQualifierPicker.getSelectedTextQualifier().getCharacter();

        mTextViewSeparator = (TextView) view.findViewById(R.id.bookExport_textSeparator);
        mTextViewSeparator.setText(mColumnSeparatorPicker.getSelectedColumnSeparator().getResourceId());
        mColumnSeparator = mColumnSeparatorPicker.getSelectedColumnSeparator().getCharacter();

        mLayoutQualifier.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mTextQualifierPicker.show(getFragmentManager(), TAG_TEXT_QUALIFIER_PICKER);
            }
        });

        mLayoutSeparator.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mColumnSeparatorPicker.show(getFragmentManager(), TAG_COLUMN_SEPARATOR_PICKER);
            }
        });

        mCheckBoxFirstRowContainsHeaders = (CheckBox) view.findViewById(R.id.bookExport_checkBoxFirstRowContainsHeaders);
        mTextPreview = (TextView) view.findViewById(R.id.bookExport_preview);

        mCheckBoxFirstRowContainsHeaders.setChecked(true);

        mCheckBoxFirstRowContainsHeaders.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mFirstRowContainsHeader = isChecked;
                renderPreview();
            }
        });

        renderPreview();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.book_export, menu);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelAsyncTask();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result;
        switch (item.getItemId()) {
            case R.id.bookExport_actionExport:
                mCsvExportTask = new CsvExportTask();
                mCsvExportTask.execute();
                result = true;
                break;

            default:
                result = super.onOptionsItemSelected(item);
                break;
        }

        return result;
    }

    /**
     * Cancel the asynchronous task.
     */
    private void cancelAsyncTask() {
        if (mCsvExportTask != null) {
            mCsvExportTask.cancel(true);
        }
    }

    /**
     * Render a preview of the book export with the current parameters.
     */
    private void renderPreview() {
        String preview = exportService.previewBookExport(mTextQualifier, mColumnSeparator, mFirstRowContainsHeader);

        mTextPreview.setText(preview);
    }

    @Override
    public void onTextQualifierPicked(TextQualifier textQualifier) {
        mTextQualifier = textQualifier.getCharacter();
        mTextViewQualifier.setText(textQualifier.getResourceId());
        renderPreview();
    }

    @Override
    public void onColumnSeparatorPicked(ColumnSeparator columnSeparator) {
        mColumnSeparator = columnSeparator.getCharacter();
        mTextViewSeparator.setText(columnSeparator.getResourceId());
        renderPreview();
    }

    @Override
    public void onCancel() {
        cancelAsyncTask();
    }

    /**
     * The asynchronous task that will export the book list to a CSV file.
     */
    private final class CsvExportTask extends AsyncTask<Void, Integer, String> {

        private final File mExportFile;
        private ProgressDialogFragment mProgressDialogFragment;

        /**
         * Constructor.
         */
        public CsvExportTask() {
            mExportFile = FileUtils.createFileInAppDir("Export.csv");
        }

        @Override
        protected String doInBackground(Void... params) {
            String errorMessage = null;

            OutputStreamWriter writer = null;
            try {
                Log.i(LogUtils.TAG, "Exporting books to CSV.");

                List<BookExport> bookExportList = exportService.getBookExportList(null);

                Log.i(LogUtils.TAG, String.format("%d books to export.", bookExportList.size()));

                mProgressDialogFragment = ProgressDialogFragment.newInstanceHorizontal(
                        R.string.title_dialog_export_books,
                        R.string.message_export_books,
                        bookExportList.size()
                );
                mProgressDialogFragment.setTargetFragment(BookExportFragment.this, 0);

                final FragmentManager fm = getActivity().getSupportFragmentManager();
                mProgressDialogFragment.show(fm, TAG_PROGRESS_DIALOG_FRAGMENT);

                writer = new OutputStreamWriter(new FileOutputStream(mExportFile), "UTF-8");

                writer.append(FileUtils.UTF8_BOM);
                if (mFirstRowContainsHeader) {
                    writer.append(BookExport.getCsvHeader(mTextQualifier, mColumnSeparator));
                    writer.append('\n');
                }

                int i = 0;
                for (BookExport bookExport : bookExportList) {
                    if (isCancelled()) {
                        Log.i(LogUtils.TAG, "CSV export task cancelled, aborting.");
                        break;
                    }

                    writer.append(bookExport.toCsv(mTextQualifier, mColumnSeparator));
                    writer.append('\n');

                    i++;
                    publishProgress(i);
                }

                Log.i(LogUtils.TAG, "Export finished successfully.");
            } catch (IOException e) {
                Log.e(LogUtils.TAG, e.getMessage(), e);
                errorMessage = e.getMessage();
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        Log.e(LogUtils.TAG, "Error closing the file.", e);
                    }
                }

                MediaScannerConnection.scanFile(getActivity(), new String[]{mExportFile.getAbsolutePath()}, null, null);
            }
            return errorMessage;
        }

        @Override
        protected void onPostExecute(String errorMessage) {
            super.onPostExecute(errorMessage);
            mProgressDialogFragment.dismiss();

            if (errorMessage == null) {
                String message = String.format(getString(R.string.message_file_saved), mExportFile.getName(), mExportFile
                        .getParentFile().getName());
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int progress = values[0];
            mProgressDialogFragment.setProgress(progress);
        }
    }
}
