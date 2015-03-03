package com.blackbooks.fragments.dialogs;

import android.app.Dialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.blackbooks.R;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookGroup;
import com.blackbooks.model.persistent.BookLocation;
import com.blackbooks.services.BookLocationServices;

/**
 * A dialog fragment to edit a book location.
 */
public final class BookLocationEditFragment extends DialogFragment {

    private static final String ARG_BOOK_GROUP = "ARG_BOOK_GROUP";

    private BookLocationEditListener mBookLocationEditListener;
    private BookGroup mBookGroup;

    /**
     * Return a new instance of BookLocationEditFragment that is initialized to edit
     * a book location.
     *
     * @param bookGroup BookGroup.
     * @return BookLocationEditFragment.
     */
    public static BookLocationEditFragment newInstance(BookGroup bookGroup) {
        BookLocationEditFragment fragment = new BookLocationEditFragment();
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
        mBookLocationEditListener = (BookLocationEditListener) getTargetFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_edit_book_location);
        dialog.setTitle(R.string.title_dialog_edit_book_location);

        final EditText textAuthor = (EditText) dialog.findViewById(R.id.editBookLocation_textBookLocation);
        textAuthor.setText(mBookGroup.name);

        Button saveButton = (Button) dialog.findViewById(R.id.editBookLocation_confirm);
        Button cancelButton = (Button) dialog.findViewById(R.id.editBookLocation_cancel);

        saveButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String newName = textAuthor.getText().toString();

                String errorMessage = null;
                if (newName == null || newName.trim().isEmpty()) {
                    errorMessage = getString(R.string.message_book_location_missing);
                } else {
                    newName = newName.trim();

                    SQLiteDatabase db = SQLiteHelper.getInstance().getReadableDatabase();
                    BookLocation bookLocation = new BookLocation();
                    bookLocation.name = newName;
                    BookLocation bookLocationDb = BookLocationServices.getBookLocationByCriteria(db, bookLocation);

                    if (bookLocationDb != null) {
                        errorMessage = getString(R.string.message_book_location_already_present, newName);
                    }
                }

                if (errorMessage == null) {
                    textAuthor.setText(null);
                    textAuthor.setError(null);
                    dismiss();
                    mBookLocationEditListener.onBookLocationEdit(mBookGroup, newName);
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
     * Activities hosting a {@link com.blackbooks.fragments.dialogs.BookLocationEditFragment} should implement this
     * interface to be notified when the book location is edited.
     */
    public interface BookLocationEditListener {

        /**
         * Called when the book location is edited.
         *
         * @param bookGroup The edited book location.
         * @param newName   The new name of the author.
         */
        void onBookLocationEdit(BookGroup bookGroup, String newName);
    }
}
