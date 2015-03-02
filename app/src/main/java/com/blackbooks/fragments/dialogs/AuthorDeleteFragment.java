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
 * A fragment dialog to delete an author.
 */
public final class AuthorDeleteFragment extends DialogFragment {

    private static final String ARG_BOOK_GROUP = "ARG_BOOK_GROUP";

    private BookGroup mBookGroup;
    private AuthorDeleteListener mAuthorDeleteListener;

    /**
     * Return a new instance of AuthorDeleteFragment that is initialized to
     * remove an author.
     *
     * @param bookGroup BookGroup.
     * @return AuthorDeleteFragment.
     */
    public static AuthorDeleteFragment newInstance(BookGroup bookGroup) {
        AuthorDeleteFragment fragment = new AuthorDeleteFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOK_GROUP, bookGroup);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mBookGroup = (BookGroup) args.getSerializable(ARG_BOOK_GROUP);
        mAuthorDeleteListener = (AuthorDeleteListener) getTargetFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String message = getString(R.string.message_confirm_delete_author);
        message = String.format(message, mBookGroup.name);

        builder.setTitle(R.string.title_dialog_delete_author) //
                .setMessage(message) //
                .setPositiveButton(R.string.message_confirm_delete_author_confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mAuthorDeleteListener.onAuthorDeleted(mBookGroup);
                    }
                }).setNegativeButton(R.string.message_confirm_delete_author_cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Do nothing.
            }
        });
        return builder.create();
    }

    /**
     * Activities hosting a {@link com.blackbooks.fragments.dialogs.AuthorDeleteFragment} should implement this
     * interface to be notified when the author is deleted.
     */
    public interface AuthorDeleteListener {

        /**
         * Called when the author is deleted.
         *
         * @param bookGroup BookGroup.
         */
        public void onAuthorDeleted(BookGroup bookGroup);
    }
}
