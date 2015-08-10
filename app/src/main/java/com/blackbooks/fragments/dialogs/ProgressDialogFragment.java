package com.blackbooks.fragments.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * Progress dialog fragment.
 */
public final class ProgressDialogFragment extends DialogFragment {

    private ProgressDialog mProgressDialog;

    private Integer mTitleId;
    private Integer mMessageId;
    private Integer mMax;

    private OnProgressDialogListener mOnProgressDialogListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mOnProgressDialogListener = (OnProgressDialogListener) getTargetFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        if (mTitleId != null) {
            mProgressDialog.setTitle(mTitleId);
        }
        if (mMessageId != null) {
            mProgressDialog.setMessage(getString(mMessageId));
        }
        if (mMax != null) {
            mProgressDialog.setMax(mMax);
        }
        return mProgressDialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mOnProgressDialogListener.onCancel();
    }

    @Override
    public void onDestroyView() {
        // Hack to prevent the dialog from hiding on screen orientation change.
        // See http://stackoverflow.com/questions/14176144/progressdialog-fragment-that-works-across-an-orientation-switch
        final Dialog dialog = getDialog();
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }

    /**
     * Set the progress of the dialog.
     *
     * @param progress Progress.
     */
    public void setProgress(int progress) {
        mProgressDialog.setProgress(progress);
    }

    /**
     * The the id of the string resource to use as the title of the dialog.
     *
     * @param titleId Id of the string to be used as the title.
     */
    public void setTitle(int titleId) {
        mTitleId = titleId;
    }

    /**
     * Set the max value of the progress dialog.
     *
     * @param max Max value.
     */
    public void setMax(int max) {
        mMax = max;
    }

    /**
     * Set the id of the string resource to use as the message of the dialog.
     *
     * @param messageId Id of the string to be used as the message.
     */
    public void setMessage(int messageId) {
        mMessageId = messageId;
    }

    /**
     * Fragments hosting a ProgressDialogFragment must implement this interface to be notified
     * when the dialog is cancelled.
     */
    public interface OnProgressDialogListener {

        /**
         * Called when the dialog is cancelled.
         */
        void onCancel();
    }
}