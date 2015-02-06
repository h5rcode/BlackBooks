package com.blackbooks.fragments;

import android.database.sqlite.SQLiteDatabase;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.fragments.dialogs.ColumnSeparator;
import com.blackbooks.fragments.dialogs.ColumnSeparatorPicker;
import com.blackbooks.fragments.dialogs.ColumnSeparatorPicker.ColumnSeparatorPickerListener;
import com.blackbooks.fragments.dialogs.TextQualifier;
import com.blackbooks.fragments.dialogs.TextQualifierPicker;
import com.blackbooks.fragments.dialogs.TextQualifierPicker.TextQualifierPickerListener;
import com.blackbooks.services.ExportServices;
import com.blackbooks.utils.FileUtils;
import com.blackbooks.utils.LogUtils;

import java.io.File;
import java.io.IOException;

/**
 * Fragment where the user can export the list of books as a CSV file.
 */
public class BookExportFragment extends Fragment implements TextQualifierPickerListener, ColumnSeparatorPickerListener {

    private static final String TAG_TEXT_QUALIFIER_PICKER = "TAG_TEXT_QUALIFIER_PICKER";
    private static final String TAG_COLUMN_SEPARATOR_PICKER = "TAG_COLUMN_SEPARATOR_PICKER";
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

    public BookExportFragment() {
        super();

        mTextQualifierPicker = new TextQualifierPicker();
        mTextQualifierPicker.setTargetFragment(this, 0);

        mColumnSeparatorPicker = new ColumnSeparatorPicker();
        mColumnSeparatorPicker.setTargetFragment(this, 0);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result;
        switch (item.getItemId()) {
            case R.id.bookExport_actionExport:
                exportBooks();
                result = true;
                break;

            default:
                result = super.onOptionsItemSelected(item);
                break;
        }

        return result;
    }

    /**
     * Export the books as a CSV file using the current parameters.
     */
    private void exportBooks() {
        try {
            SQLiteDatabase db = SQLiteHelper.getInstance().getReadableDatabase();
            File exportFile = FileUtils.createFileInAppDir("Export.csv");
            ExportServices.exportBookList(db, exportFile, mTextQualifier, mColumnSeparator, mFirstRowContainsHeader);

            MediaScannerConnection.scanFile(getActivity(), new String[]{exportFile.getAbsolutePath()}, null, null);
            String message = String.format(getString(R.string.message_file_saved), exportFile.getName(), exportFile
                    .getParentFile().getName());
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e(LogUtils.TAG, e.getMessage(), e);
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Render a preview of the book export with the current parameters.
     */
    private void renderPreview() {
        SQLiteDatabase db = SQLiteHelper.getInstance().getReadableDatabase();
        String preview = ExportServices.previewBookExport(db, mTextQualifier, mColumnSeparator, mFirstRowContainsHeader);

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
}
