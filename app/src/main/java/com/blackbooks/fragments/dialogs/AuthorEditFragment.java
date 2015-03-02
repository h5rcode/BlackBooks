package com.blackbooks.fragments.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.blackbooks.R;
import com.blackbooks.model.nonpersistent.BookGroup;

/**
 * A dialog fragment to edit a category.
 */
public final class AuthorEditFragment extends DialogFragment {

    private static final String ARG_BOOK_GROUP = "ARG_BOOK_GROUP";

    private AuthorEditListener mAuthorEditListener;
    private BookGroup mBookGroup;

    /**
     * Return a new instance of CategoryEditFragment that is initialized to edit
     * a category.
     *
     * @param bookGroup BookGroup.
     * @return CategoryEditFragment.
     */
    public static AuthorEditFragment newInstance(BookGroup bookGroup) {
        AuthorEditFragment fragment = new AuthorEditFragment();
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
        mAuthorEditListener = (AuthorEditListener) getTargetFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_edit_author);
        dialog.setTitle(R.string.title_dialog_edit_author);

        final AutoCompleteTextView textAuthor = (AutoCompleteTextView) dialog.findViewById(R.id.editAuthor_textAuthor);
        textAuthor.setText(mBookGroup.name);

        Button saveButton = (Button) dialog.findViewById(R.id.editAuthor_confirm);
        Button cancelButton = (Button) dialog.findViewById(R.id.editAuthor_cancel);

        saveButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String newName = textAuthor.getText().toString();

                String errorMessage = null;
                if (newName == null || newName.trim().isEmpty()) {
                    errorMessage = "Enter a name";
                } else {
                    newName = newName.trim();
                }

                if (errorMessage == null) {
                    textAuthor.setText(null);
                    textAuthor.setError(null);
                    dismiss();
                    mAuthorEditListener.onAuthorEdit(mBookGroup, newName);
                } else {
                    textAuthor.setError(errorMessage);
                }
            }
        });
        cancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return dialog;
    }

    /**
     * Activities hosting a {@link com.blackbooks.fragments.dialogs.AuthorEditFragment} should implement this
     * interface to be notified when the category is edited.
     */
    public interface AuthorEditListener {

        /**
         * Called when the author is edited.
         *
         * @param bookGroup The edited author.
         * @param newName   The new name of the author.
         */
        void onAuthorEdit(BookGroup bookGroup, String newName);
    }
}
