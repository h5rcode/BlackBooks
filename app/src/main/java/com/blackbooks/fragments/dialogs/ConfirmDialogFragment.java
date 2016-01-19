package com.blackbooks.fragments.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.blackbooks.R;

/**
 * Confirm dialog fragment.
 */
public final class ConfirmDialogFragment extends DialogFragment {

    private static final String ARG_TITLE_RES_ID = "ARG_TITLE_RES_ID";
    private static final String ARG_MESSAGE_RES_ID = "ARG_MESSAGE_RES_ID";
    private static final String ARG_CHECKBOX_TEXT_RES_ID = "ARG_CHECKBOX_TEXT_RES_ID";
    private static final String ARG_BUTTON_CANCEL_TEXT_RES_ID = "ARG_BUTTON_CANCEL_TEXT_RES_ID";
    private static final String ARG_BUTTON_CONFIRM_TEXT_RES_ID = "ARG_BUTTON_CONFIRM_TEXT_RES_ID";

    private OnConfirmListener mOnConfirmListener;

    private Integer mTitleResId;
    private Integer mMessageResId;
    private Integer mCheckboxTextResId;
    private Integer mButtonCancelTextResId;
    private Integer mButtonConfirmTextResId;

    /**
     * Initialize a new instance of ConfirmDialogFragment.
     *
     * @param titleResId             Id of the resource for the title.
     * @param messageResId           Id of the resource for the message.
     * @param checkboxTextResId      Id of the resource for the checkbox.
     * @param buttonCancelTextResId  Id of the resource for the cancel button.
     * @param buttonConfirmTextResId Id of the resource for the confirm button.
     * @return ConfirmDialogFragment.
     */
    public static ConfirmDialogFragment newInstance(int titleResId, int messageResId, int checkboxTextResId, int buttonCancelTextResId, int buttonConfirmTextResId) {

        final Bundle args = new Bundle();
        args.putInt(ARG_TITLE_RES_ID, titleResId);
        args.putInt(ARG_MESSAGE_RES_ID, messageResId);
        args.putInt(ARG_CHECKBOX_TEXT_RES_ID, checkboxTextResId);
        args.putInt(ARG_BUTTON_CANCEL_TEXT_RES_ID, buttonCancelTextResId);
        args.putInt(ARG_BUTTON_CONFIRM_TEXT_RES_ID, buttonConfirmTextResId);

        final ConfirmDialogFragment dialog = new ConfirmDialogFragment();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mOnConfirmListener = (OnConfirmListener) getTargetFragment();

        final Bundle args = getArguments();
        mTitleResId = args.getInt(ARG_TITLE_RES_ID);
        mMessageResId = args.getInt(ARG_MESSAGE_RES_ID);
        mCheckboxTextResId = args.getInt(ARG_CHECKBOX_TEXT_RES_ID);
        mButtonCancelTextResId = args.getInt(ARG_BUTTON_CANCEL_TEXT_RES_ID);
        mButtonConfirmTextResId = args.getInt(ARG_BUTTON_CONFIRM_TEXT_RES_ID);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_confirm);

        final TextView textMessage = (TextView) dialog.findViewById(R.id.confirm_textMessage);
        final CheckBox checkbox = (CheckBox) dialog.findViewById(R.id.confirm_checkbox);
        final TextView textCheckboxMessage = (TextView) dialog.findViewById(R.id.confirm_checkbox_message);
        final Button buttonCancel = (Button) dialog.findViewById(R.id.confirm_buttonCancel);
        final Button buttonConfirm = (Button) dialog.findViewById(R.id.confirm_buttonConfirm);

        dialog.setTitle(mTitleResId);
        textMessage.setText(mMessageResId);
        textCheckboxMessage.setText(mCheckboxTextResId);
        buttonCancel.setText(mButtonCancelTextResId);
        buttonConfirm.setText(mButtonConfirmTextResId);

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonConfirm.setEnabled(isChecked);
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mOnConfirmListener.onConfirm();
            }
        });

        return dialog;
    }

    /**
     * Fragments hosting a ConfirmDialogFragment should implement this interface to be notified when the user confirms.
     */
    public interface OnConfirmListener {

        /**
         * Called when the user confirms.
         */
        void onConfirm();
    }
}
