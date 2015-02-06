package com.blackbooks.fragments;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;

import com.blackbooks.R;
import com.blackbooks.adapters.AutoCompleteAdapter;
import com.blackbooks.adapters.AutoCompleteAdapter.AutoCompleteSearcher;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.BookLocation;
import com.blackbooks.services.BookLocationServices;

import java.util.List;

/**
 * Fragment to edit the information concerning the owner of the book.
 */
public class BookEditPersonalFragment extends Fragment {

    private static final String ARG_BOOK = "ARG_BOOK";

    private CheckBox mCheckBoxRead;
    private CheckBox mCheckBoxFavourite;
    private EditText mTextComment;
    private AutoCompleteTextView mTextBookLocation;

    private BookInfo mBookInfo;

    private AutoCompleteAdapter<BookLocation> mBookLocationAutoCompleteAdapter;

    /**
     * Create a new instance of BookEditPersonalFragment.
     *
     * @param bookInfo Book to edit.
     * @return BookEditPersonalFragment.
     */
    public static BookEditPersonalFragment newInstance(BookInfo bookInfo) {
        BookEditPersonalFragment instance = new BookEditPersonalFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOK, bookInfo);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Bundle args = getArguments();
        mBookInfo = (BookInfo) args.getSerializable(ARG_BOOK);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_book_edit_personal, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        findViews();
        renderBookInfo();

        mBookLocationAutoCompleteAdapter = new AutoCompleteAdapter<BookLocation>(this.getActivity(),
                android.R.layout.simple_list_item_1, new AutoCompleteSearcher<BookLocation>() {

            @Override
            public List<BookLocation> search(CharSequence constraint) {
                SQLiteDatabase db = SQLiteHelper.getInstance().getReadableDatabase();
                List<BookLocation> bookLocationList = BookLocationServices.getBookLocationListByText(db, constraint.toString());
                return bookLocationList;
            }

            @Override
            public String getDisplayLabel(BookLocation item) {
                return item.name;
            }
        });
        mTextBookLocation.setAdapter(mBookLocationAutoCompleteAdapter);
    }

    /**
     * Validate the user input and read the book info from the view.
     *
     * @param bookInfo BookInfo.
     * @return True if the book information is valid, false otherwise.
     */
    public boolean readBookInfo(BookInfo bookInfo) {
        bookInfo.isRead = mCheckBoxRead.isChecked() ? 1L : 0L;
        bookInfo.isFavourite = mCheckBoxFavourite.isChecked() ? 1L : 0L;
        String bookLocationName = mTextBookLocation.getText().toString();
        String comment = mTextComment.getText().toString();
        if (comment == null || comment.isEmpty()) {
            comment = null;
        }
        bookInfo.comment = comment;

        BookLocation bookLocation = new BookLocation();
        SQLiteDatabase db = SQLiteHelper.getInstance().getReadableDatabase();

        if (bookLocationName != null && !bookLocationName.isEmpty()) {
            bookLocation.name = bookLocationName;

            BookLocation bookLocationDb = BookLocationServices.getBookLocationByCriteria(db, bookLocation);
            if (bookLocationDb != null) {
                bookLocation = bookLocationDb;
            }
        }

        bookInfo.bookLocation = bookLocation;
        return true;
    }

    /**
     * Find the views of the activity that will contain the book information.
     */
    private void findViews() {
        View view = getView();
        mCheckBoxRead = (CheckBox) view.findViewById(R.id.bookEditPersonal_checkRead);
        mCheckBoxFavourite = (CheckBox) view.findViewById(R.id.bookEditPersonal_checkFavourite);
        mTextComment = (EditText) view.findViewById(R.id.bookEditPersonal_textComment);
        mTextBookLocation = (AutoCompleteTextView) view.findViewById(R.id.bookEditPersonal_textBookLocation);
    }

    /**
     * Update the views of the activity using the book information.
     */
    private void renderBookInfo() {
        mCheckBoxRead.setChecked(mBookInfo.isRead != 0);
        mCheckBoxFavourite.setChecked(mBookInfo.isFavourite != 0);
        mTextComment.setText(mBookInfo.comment);
        mTextBookLocation.setText(mBookInfo.bookLocation.name);
    }
}
