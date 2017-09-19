package com.blackbooks.fragments.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.blackbooks.R;
import com.blackbooks.activities.BookDisplayActivity;
import com.blackbooks.adapters.DuplicateBooksAdapter;
import com.blackbooks.cache.ThumbnailManager;
import com.blackbooks.model.persistent.Book;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

/**
 * A dialog used to display books that correspond to a given ISBN.
 */
public final class DuplicateBooksDialog extends DialogFragment {

    private static final String ARG_ISBN = "ARG_ISBN";
    private static final String ARG_BOOK_LIST = "ARG_BOOK_LIST";

    private DuplicateBooksListener mDuplicateBooksListener;
    private String mIsbn;
    private List<Book> mBookList;

    @Inject
    ThumbnailManager mThumbnailManager;

    /**
     * Create a new instance of {@link com.blackbooks.fragments.dialogs.DuplicateBooksDialog} ready
     * to display a book list.
     *
     * @param isbn     The ISBN all the books to be listed have in common.
     * @param bookList The list of books to display.
     * @return DuplicateBooksDialog.
     */
    public static DuplicateBooksDialog newInstance(String isbn, List<Book> bookList) {
        DuplicateBooksDialog fragment = new DuplicateBooksDialog();

        Bundle args = new Bundle();
        args.putString(ARG_ISBN, isbn);
        args.putSerializable(ARG_BOOK_LIST, (Serializable) bookList);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mIsbn = args.getString(ARG_ISBN);
        mBookList = (List<Book>) args.getSerializable(ARG_BOOK_LIST);
        mDuplicateBooksListener = (DuplicateBooksListener) getActivity();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_duplicate_books);
        dialog.setTitle(R.string.title_dialog_duplicate_books);

        final TextView textView = (TextView) dialog.findViewById(R.id.duplicate_books_message);
        final Resources res = getResources();
        final int bookNumber = mBookList.size();
        String message = res.getQuantityString(R.plurals.message_duplicate_books, bookNumber, bookNumber);
        textView.setText(message);

        final ListView listView = (ListView) dialog.findViewById(R.id.duplicate_books_list);
        listView.setAdapter(new DuplicateBooksAdapter(getActivity(), mBookList, mThumbnailManager));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book book = (Book) parent.getItemAtPosition(position);
                Intent i = new Intent(DuplicateBooksDialog.this.getActivity(), BookDisplayActivity.class);
                i.putExtra(BookDisplayActivity.EXTRA_BOOK_ID, book.id);
                DuplicateBooksDialog.this.startActivity(i);
                DuplicateBooksDialog.this.dismiss();
            }
        });

        Button continueButton = (Button) dialog.findViewById(R.id.duplicate_books_continue);
        Button cancelButton = (Button) dialog.findViewById(R.id.duplicate_books_cancel);

        continueButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                mDuplicateBooksListener.onContinue(mIsbn);
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                mDuplicateBooksListener.onCancel();
            }
        });

        return dialog;
    }

    /**
     * Activities hosting a {@link com.blackbooks.fragments.dialogs.DuplicateBooksDialog} should implement this
     * interface to be notified when the user interacts with the dialog.
     */
    public interface DuplicateBooksListener {

        /**
         * Called when the user clicks the "Continue" button.
         *
         * @param isbn ISBN.
         */
        void onContinue(String isbn);

        /**
         * Called when the user clicks the "Cancel" button.
         */
        void onCancel();
    }
}
