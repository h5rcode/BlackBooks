package com.blackbooks.fragments.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.blackbooks.R;
import com.blackbooks.utils.IsbnUtils;

/**
 * A dialog fragment used to add ISBNs.
 */
public final class IsbnAddFragment extends DialogFragment {

    private IsbnAddListener mIsbnAddListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsbnAddListener = (IsbnAddListener) getTargetFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_add_isbn);
        dialog.setTitle(R.string.title_dialog_add_isbn);

        final EditText textIsbn = (EditText) dialog.findViewById(R.id.addIsbn_textIsbn);
        Button continueButton = (Button) dialog.findViewById(R.id.addIsbn_confirm);
        Button cancelButton = (Button) dialog.findViewById(R.id.addIsbn_cancel);

        continueButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String isbn = textIsbn.getText().toString();

                if (IsbnUtils.isValidIsbn(isbn)) {

                    dismiss();
                    mIsbnAddListener.onAddIsbn(isbn);
                } else {
                    textIsbn.setError("Invalid ISBN.");
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return dialog;
    }

    /**
     * Activities hosting a {@link IsbnAddFragment} should implement this
     * interface to be notified when the user interacts with the dialog.
     */
    public interface IsbnAddListener {

        /**
         * Called when the user clicks the "Add" button.
         *
         * @param isbn ISBN.
         */
        void onAddIsbn(String isbn);
    }
}
