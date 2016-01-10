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
 * A fragment dialog to delete a book location.
 */
public final class BookLocationDeleteFragment extends DialogFragment {

    private static final String ARG_BOOK_GROUP = "ARG_BOOK_GROUP";

    private BookGroup mBookGroup;
    private BookLocationDeleteListener mBookLocationDeleteListener;

    /**
     * Return a new instance of BookLocationDeleteFragment that is initialized to
     * remove a book location.
     *
     * @param bookGroup BookGroup.
     * @return BookLocationDeleteFragment.
     */
    public static BookLocationDeleteFragment newInstance(BookGroup bookGroup) {
        BookLocationDeleteFragment fragment = new BookLocationDeleteFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOK_GROUP, bookGroup);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBookGroup = (BookGroup) getArguments().getSerializable(ARG_BOOK_GROUP);
        mBookLocationDeleteListener = (BookLocationDeleteListener) getTargetFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String message = getString(R.string.message_confirm_delete_book_location);
        message = String.format(message, mBookGroup.name);

        builder.setTitle(R.string.title_dialog_delete_book_location) //
                .setMessage(message) //
                .setPositiveButton(R.string.message_confirm_delete_book_location_confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mBookLocationDeleteListener.onBookLocationDeleted(mBookGroup);
                    }
                }).setNegativeButton(R.string.message_confirm_delete_book_location_cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Do nothing.
            }
        });
        return builder.create();
    }

    /**
     * Activities hosting a {@link com.blackbooks.fragments.dialogs.BookLocationDeleteFragment} should implement this
     * interface to be notified when the book location is deleted.
     */
    public interface BookLocationDeleteListener {

        /**
         * Called when the book location is deleted.
         *
         * @param bookGroup Book location.
         */
        void onBookLocationDeleted(BookGroup bookGroup);
    }
}
