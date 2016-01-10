package com.blackbooks.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.blackbooks.R;
import com.blackbooks.model.nonpersistent.BookGroup;

/**
 * A fragment dialog to delete a series.
 */
public final class SeriesDeleteFragment extends DialogFragment {

    private static final String ARG_BOOK_GROUP = "ARG_BOOK_GROUP";

    private BookGroup mBookGroup;
    private SeriesDeleteListener mSeriesDeleteListener;

    /**
     * Return a new instance of SeriesDeleteFragment that is initialized to
     * remove a series.
     *
     * @param bookGroup BookGroup.
     * @return SeriesDeleteFragment.
     */
    public static SeriesDeleteFragment newInstance(BookGroup bookGroup) {
        SeriesDeleteFragment fragment = new SeriesDeleteFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOK_GROUP, bookGroup);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBookGroup = (BookGroup) getArguments().getSerializable(ARG_BOOK_GROUP);
        mSeriesDeleteListener = (SeriesDeleteListener) getTargetFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String message = getString(R.string.message_confirm_delete_series);
        message = String.format(message, mBookGroup.name);

        builder.setTitle(R.string.title_dialog_delete_book_location) //
                .setMessage(message) //
                .setPositiveButton(R.string.message_confirm_delete_series_confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mSeriesDeleteListener.onSeriesDeleted(mBookGroup);
                    }
                }).setNegativeButton(R.string.message_confirm_delete_series_cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Do nothing.
            }
        });
        return builder.create();
    }

    /**
     * Activities hosting a {@link com.blackbooks.fragments.dialogs.SeriesDeleteFragment} should implement this
     * interface to be notified when the series is deleted.
     */
    public interface SeriesDeleteListener {

        /**
         * Called when the series is deleted.
         *
         * @param bookGroup Series.
         */
        void onSeriesDeleted(BookGroup bookGroup);
    }
}
