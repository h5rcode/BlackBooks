package com.blackbooks.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.blackbooks.R;

import java.util.Arrays;
import java.util.List;

/**
 * Column separator picker dialog.
 */
public final class ColumnSeparatorPicker extends DialogFragment {

    private static final List<ColumnSeparator> COLUMN_SEPARATORS = Arrays.asList(//
            new ColumnSeparator(';', R.string.label_column_separator_semicolon), //
            new ColumnSeparator(',', R.string.label_column_separator_comma), //
            new ColumnSeparator('|', R.string.label_column_separator_pipe), //
            new ColumnSeparator(' ', R.string.label_column_separator_space), //
            new ColumnSeparator('\t', R.string.label_column_separator_tab));

    private ColumnSeparatorPickerListener mColumnSeparatorPickerListener;
    private int mSelectedSeparator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mColumnSeparatorPickerListener = (ColumnSeparatorPickerListener) this.getTargetFragment();

        if (mColumnSeparatorPickerListener == null) {
            throw new IllegalStateException("Set the target fragment of this instance by calling setTargetFragment().");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] separators = new String[COLUMN_SEPARATORS.size()];
        int i = 0;
        for (ColumnSeparator separator : COLUMN_SEPARATORS) {
            separators[i++] = getString(separator.getResourceId());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_dialog_column_separator_picker) //
                .setSingleChoiceItems(separators, mSelectedSeparator, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mSelectedSeparator = which;
                        dismiss();
                        mColumnSeparatorPickerListener.onColumnSeparatorPicked(getSelectedColumnSeparator());
                    }
                });
        return builder.create();
    }

    /**
     * This override prevents the dialog from disappearing when the screen
     * orientation changes.
     */
    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    public ColumnSeparator getSelectedColumnSeparator() {
        return COLUMN_SEPARATORS.get(mSelectedSeparator);
    }

    /**
     * Fragment hosting this dialog should implement this interface to be
     * notified when a column separator has been picked.
     */
    public interface ColumnSeparatorPickerListener {

        /**
         * Called when a column separator has been picked.
         *
         * @param columnSeparator ColumnSeparator.
         */
        void onColumnSeparatorPicked(ColumnSeparator columnSeparator);
    }
}
