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
    private Integer mMax;
    private DialogInterface.OnCancelListener mOnCancelListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
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
        if (mOnCancelListener != null) {
            mProgressDialog.setOnCancelListener(mOnCancelListener);
        }
        if (mMax != null) {
            mProgressDialog.setMax(mMax);
        }
        return mProgressDialog;
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
     * Set the cancel listener.
     *
     * @param onCancelListener Cancel listener.
     */
    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        mOnCancelListener = onCancelListener;
    }

    /**
     * Set the max value of the progress dialog.
     *
     * @param max Max value.
     */
    public void setMax(int max) {
        mMax = max;
    }
}