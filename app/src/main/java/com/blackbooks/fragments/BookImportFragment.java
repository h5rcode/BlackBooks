package com.blackbooks.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blackbooks.R;
import com.blackbooks.activities.FileChooserActivity;
import com.blackbooks.fragments.dialogs.ColumnSeparator;
import com.blackbooks.fragments.dialogs.ColumnSeparatorPicker;
import com.blackbooks.fragments.dialogs.TextQualifier;
import com.blackbooks.fragments.dialogs.TextQualifierPicker;

/**
 * Fragment where the user can import a list of books from a CSV file.
 */
public final class BookImportFragment extends Fragment implements TextQualifierPicker.TextQualifierPickerListener, ColumnSeparatorPicker.ColumnSeparatorPickerListener {

    private static final int REQUEST_CHOOSE_FILE = 0;
    private static final String TAG_TEXT_QUALIFIER_PICKER = "TAG_TEXT_QUALIFIER_PICKER";
    private static final String TAG_COLUMN_SEPARATOR_PICKER = "TAG_COLUMN_SEPARATOR_PICKER";
    private final TextQualifierPicker mTextQualifierPicker;
    private final ColumnSeparatorPicker mColumnSeparatorPicker;

    private LinearLayout mLayoutFile;
    private LinearLayout mLayoutQualifier;
    private LinearLayout mLayoutSeparator;
    private TextView mTextViewQualifier;
    private TextView mTextViewSeparator;
    private CheckBox mCheckBoxFirstRowContainsHeaders;
    private char mTextQualifier;
    private char mColumnSeparator;
    private boolean mFirstRowContainsHeader = true;

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

        mLayoutFile = (LinearLayout) view.findViewById(R.id.bookImport_layoutFile);
        mLayoutQualifier = (LinearLayout) view.findViewById(R.id.bookImport_layoutQualifier);
        mLayoutSeparator = (LinearLayout) view.findViewById(R.id.bookImport_layoutSeparator);

        mTextViewQualifier = (TextView) view.findViewById(R.id.bookImport_textQualifier);
        mTextViewQualifier.setText(mTextQualifierPicker.getSelectedTextQualifier().getResourceId());
        mTextQualifier = mTextQualifierPicker.getSelectedTextQualifier().getCharacter();

        mTextViewSeparator = (TextView) view.findViewById(R.id.bookImport_textSeparator);
        mTextViewSeparator.setText(mColumnSeparatorPicker.getSelectedColumnSeparator().getResourceId());
        mColumnSeparator = mColumnSeparatorPicker.getSelectedColumnSeparator().getCharacter();

        mLayoutFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), FileChooserActivity.class);
                startActivityForResult(i, REQUEST_CHOOSE_FILE);
            }
        });

        mLayoutQualifier.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mTextQualifierPicker.show(getFragmentManager(), TAG_TEXT_QUALIFIER_PICKER);
            }
        });

        mLayoutSeparator.setOnClickListener(new View.OnClickListener() {

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
    public void onTextQualifierPicked(TextQualifier textQualifier) {
        mTextQualifier = textQualifier.getCharacter();
        mTextViewQualifier.setText(textQualifier.getResourceId());
    }

    @Override
    public void onColumnSeparatorPicked(ColumnSeparator columnSeparator) {
        mColumnSeparator = columnSeparator.getCharacter();
        mTextViewSeparator.setText(columnSeparator.getResourceId());
    }
}
