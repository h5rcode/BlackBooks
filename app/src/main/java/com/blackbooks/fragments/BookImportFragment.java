package com.blackbooks.fragments;

import android.app.Activity;
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
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blackbooks.R;
import com.blackbooks.activities.BookImportColumnMappingActivity;
import com.blackbooks.activities.FileChooserActivity;
import com.blackbooks.fragments.dialogs.ColumnSeparator;
import com.blackbooks.fragments.dialogs.ColumnSeparatorPicker;
import com.blackbooks.fragments.dialogs.TextQualifier;
import com.blackbooks.fragments.dialogs.TextQualifierPicker;
import com.blackbooks.utils.Commons;

import java.io.File;

/**
 * Fragment where the user can import a list of books from a CSV file.
 */
public final class BookImportFragment extends Fragment implements TextQualifierPicker.TextQualifierPickerListener, ColumnSeparatorPicker.ColumnSeparatorPickerListener {

    private static final int REQUEST_CHOOSE_FILE = 0;
    private static final String TAG_TEXT_QUALIFIER_PICKER = "TAG_TEXT_QUALIFIER_PICKER";
    private static final String TAG_COLUMN_SEPARATOR_PICKER = "TAG_COLUMN_SEPARATOR_PICKER";
    private final TextQualifierPicker mTextQualifierPicker;
    private final ColumnSeparatorPicker mColumnSeparatorPicker;

    private MenuItem mMenuItemNextStep;
    private TextView mTextViewFile;
    private TextView mTextViewQualifier;
    private TextView mTextViewSeparator;
    private CheckBox mCheckBoxFirstRowContainsHeaders;
    private File mFile;

    private TextQualifier mTextQualifier;
    private ColumnSeparator mColumnSeparator;
    private boolean mFirstRowContainsHeader = true;

    /**
     * Constructor.
     */
    public BookImportFragment() {
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
        View view = inflater.inflate(R.layout.fragment_book_import, container, false);

        final LinearLayout layoutFile = (LinearLayout) view.findViewById(R.id.bookImport_layoutFile);
        final LinearLayout layoutQualifier = (LinearLayout) view.findViewById(R.id.bookImport_layoutQualifier);
        final LinearLayout layoutSeparator = (LinearLayout) view.findViewById(R.id.bookImport_layoutSeparator);

        mTextViewFile = (TextView) view.findViewById(R.id.bookImport_textFile);
        if (mFile != null) {
            mTextViewFile.setText(mFile.getName());
        }

        mTextViewQualifier = (TextView) view.findViewById(R.id.bookImport_textQualifier);
        mTextViewQualifier.setText(mTextQualifierPicker.getSelectedTextQualifier().getResourceId());
        mTextQualifier = mTextQualifierPicker.getSelectedTextQualifier();

        mTextViewSeparator = (TextView) view.findViewById(R.id.bookImport_textSeparator);
        mTextViewSeparator.setText(mColumnSeparatorPicker.getSelectedColumnSeparator().getResourceId());
        mColumnSeparator = mColumnSeparatorPicker.getSelectedColumnSeparator();

        layoutFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), FileChooserActivity.class);
                startActivityForResult(i, REQUEST_CHOOSE_FILE);
            }
        });

        layoutQualifier.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mTextQualifierPicker.show(getFragmentManager(), TAG_TEXT_QUALIFIER_PICKER);
            }
        });

        layoutSeparator.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mColumnSeparatorPicker.show(getFragmentManager(), TAG_COLUMN_SEPARATOR_PICKER);
            }
        });

        mCheckBoxFirstRowContainsHeaders = (CheckBox) view.findViewById(R.id.bookImport_checkBoxFirstRowContainsHeaders);
        mCheckBoxFirstRowContainsHeaders.setChecked(true);
        mCheckBoxFirstRowContainsHeaders.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mFirstRowContainsHeader = isChecked;
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.book_import, menu);
        mMenuItemNextStep = menu.findItem(R.id.bookImport_actionNextStep);
        toggleMenuItemNextStep();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result;
        switch (item.getItemId()) {
            case R.id.bookImport_actionNextStep:
                Intent i = new Intent(getActivity(), BookImportColumnMappingActivity.class);
                i.putExtra(BookImportColumnMappingActivity.EXTRA_FILE, mFile);
                i.putExtra(BookImportColumnMappingActivity.EXTRA_COLUMN_SEPARATOR, mColumnSeparator);
                i.putExtra(BookImportColumnMappingActivity.EXTRA_TEXT_QUALIFIER, mTextQualifier);
                i.putExtra(BookImportColumnMappingActivity.EXTRA_FIRST_ROW_CONTAINS_HEADER, mFirstRowContainsHeader);
                startActivity(i);
                result = true;
                break;

            default:
                result = super.onOptionsItemSelected(item);
                break;
        }

        return result;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CHOOSE_FILE) {
            File file = (File) data.getSerializableExtra(FileChooserActivity.EXTRA_CHOSEN_FILE);
            mFile = file;
            mTextViewFile.setText(file.getName());
            toggleMenuItemNextStep();
        }
    }

    @Override
    public void onTextQualifierPicked(TextQualifier textQualifier) {
        mTextQualifier = textQualifier;
        mTextViewQualifier.setText(textQualifier.getResourceId());
    }

    @Override
    public void onColumnSeparatorPicked(ColumnSeparator columnSeparator) {
        mColumnSeparator = columnSeparator;
        mTextViewSeparator.setText(columnSeparator.getResourceId());
    }

    /**
     * Enable or disable the "Next" item of the options menu.
     */
    private void toggleMenuItemNextStep() {
        if (mMenuItemNextStep != null) {
            boolean itemEnabled = mFile != null;
            mMenuItemNextStep.setEnabled(itemEnabled);
            mMenuItemNextStep.getIcon().setAlpha(itemEnabled ? Commons.ALPHA_ENABLED : Commons.ALPHA_DISABLED);
        }
    }
}
