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

    private static final String ARG_STYLE = "ARG_STYLE";
    private static final String ARG_TITLE_ID = "ARG_TITLE_ID";
    private static final String ARG_MESSAGE_ID = "ARG_MESSAGE_ID";
    private static final String ARG_MAX_ID = "ARG_MAX_ID";
    private ProgressDialog mProgressDialog;

    private Integer mStyle;
    private Integer mTitleId;
    private Integer mMessageId;
    private Integer mMax;

    private OnProgressDialogListener mOnProgressDialogListener;

    /**
     * Initialize a new instance of ProgressDialogFragment that will render a horizontal progress bar.
     *
     * @param titleId   Id of the title.
     * @param messageId Id of the message.
     * @param max       Max value of the progress bar.
     * @return ProgressDialogFragment.
     */
    public static ProgressDialogFragment newInstanceHorizontal(int titleId, int messageId, int max) {
        final Bundle args = new Bundle();
        args.putInt(ARG_STYLE, ProgressDialog.STYLE_HORIZONTAL);
        args.putInt(ARG_TITLE_ID, titleId);
        args.putInt(ARG_MESSAGE_ID, messageId);
        args.putInt(ARG_MAX_ID, max);

        final ProgressDialogFragment fragment = new ProgressDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Initialize a new instance of ProgressDialogFragment that will render a spinner.
     *
     * @param titleId   Id of the title.
     * @param messageId Id of the message.
     * @return ProgressDialogFragment.
     */
    public static ProgressDialogFragment newInstanceSpinner(int titleId, int messageId) {
        final Bundle args = new Bundle();
        args.putInt(ARG_STYLE, ProgressDialog.STYLE_SPINNER);
        args.putInt(ARG_TITLE_ID, titleId);
        args.putInt(ARG_MESSAGE_ID, messageId);

        final ProgressDialogFragment fragment = new ProgressDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        final Bundle args = getArguments();
        mStyle = args.getInt(ARG_STYLE);
        mTitleId = args.getInt(ARG_TITLE_ID);
        mMessageId = args.getInt(ARG_MESSAGE_ID);

        if (mStyle == ProgressDialog.STYLE_HORIZONTAL) {
            mMax = args.getInt(ARG_MAX_ID);
        }

        mOnProgressDialogListener = (OnProgressDialogListener) getTargetFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(mStyle);
        mProgressDialog.setTitle(mTitleId);
        mProgressDialog.setMessage(getString(mMessageId));
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