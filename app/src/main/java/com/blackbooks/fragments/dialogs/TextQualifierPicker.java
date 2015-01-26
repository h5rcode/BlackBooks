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
 * Text qualifier picker dialog.
 */
public final class TextQualifierPicker extends DialogFragment {

    private static final List<TextQualifier> TEXT_QUALIFIERS = Arrays.asList( //
            new TextQualifier('"', R.string.label_text_qualifier_double_quote), //
            new TextQualifier('\'', R.string.label_text_qualifier_single_quote));

    private TextQualifierPickerListener mTextQualifierPickerListener;
    private int mSelectedQualifier;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTextQualifierPickerListener = (TextQualifierPickerListener) this.getTargetFragment();

        if (mTextQualifierPickerListener == null) {
            throw new IllegalStateException("Set the target fragment of this instance by calling setTargetFragment().");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] qualifiers = new String[TEXT_QUALIFIERS.size()];

        int i = 0;
        for (TextQualifier textQualifier : TEXT_QUALIFIERS) {
            qualifiers[i++] = getString(textQualifier.getResourceId());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_dialog_text_qualifier_picker) //
                .setSingleChoiceItems(qualifiers, mSelectedQualifier, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mSelectedQualifier = which;
                        dismiss();
                        mTextQualifierPickerListener.onTextQualifierPicked(getSelectedTextQualifier());
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

    /**
     * Return the selected TextQualifier.
     *
     * @return TextQualifier.
     */
    public TextQualifier getSelectedTextQualifier() {
        return TEXT_QUALIFIERS.get(mSelectedQualifier);
    }

    /**
     * Fragment hosting this dialog should implement this interface to be
     * notified when a text qualifier has been picked.
     */
    public interface TextQualifierPickerListener {

        /**
         * Called when a text qualifier has been picked.
         *
         * @param textQualifier TextQualifier.
         */
        void onTextQualifierPicked(TextQualifier textQualifier);
    }
}