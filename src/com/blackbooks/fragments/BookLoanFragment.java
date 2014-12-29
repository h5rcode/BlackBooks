package com.blackbooks.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.blackbooks.R;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.BookServices;

public class BookLoanFragment extends Fragment {

	private static final String ARG_BOOK_ID = "ARG_BOOK_ID";
	private static final int REQUEST_PICK_CONTACT = 0;

	private LinearLayout mLayoutNotLoaned;
	private LinearLayout mLayoutLoaned;
	private EditText mTextLoanee;

	private BookInfo mBookInfo;

	/**
	 * Create a new instance of BookLoanFragment, initialized to display a book.
	 * 
	 * @param bookId
	 *            Id of the book.
	 * @return BookLoanFragment.
	 */
	public static BookLoanFragment newInstance(long bookId) {
		BookLoanFragment fragment = new BookLoanFragment();
		Bundle args = new Bundle();
		args.putLong(ARG_BOOK_ID, bookId);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Bundle args = getArguments();
		long bookId = args.getLong(ARG_BOOK_ID);
		SQLiteHelper dbHelper = new SQLiteHelper(activity);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		mBookInfo = BookServices.getBookInfo(db, bookId);
		db.close();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_book_loan, container, false);

		mLayoutNotLoaned = (LinearLayout) view.findViewById(R.id.bookLoan_layoutNotLoaned);
		mLayoutLoaned = (LinearLayout) view.findViewById(R.id.bookLoan_layoutLoaned);

		ImageButton buttonPickContact = (ImageButton) view.findViewById(R.id.bookLoan_buttonPickContact);
		mTextLoanee = (EditText) view.findViewById(R.id.bookLoan_textLoanee);

		buttonPickContact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
				pickContactIntent.setType(Contacts.CONTENT_TYPE);
				startActivityForResult(pickContactIntent, REQUEST_PICK_CONTACT);
			}
		});

		if (mBookInfo.loanedTo != null) {
			mLayoutNotLoaned.setVisibility(View.GONE);
			mLayoutLoaned.setVisibility(View.VISIBLE);
		} else {
			mLayoutNotLoaned.setVisibility(View.VISIBLE);
			mLayoutLoaned.setVisibility(View.GONE);
		}

		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUEST_PICK_CONTACT:
				Uri contactUri = data.getData();
				String[] projection = { Phone.DISPLAY_NAME };

				// CAUTION: The query() method should be called from a separate
				// thread to avoid blocking
				// your app's UI thread. (For simplicity of the sample, this
				// code doesn't do that.)
				// Consider using CursorLoader to perform the query.
				Cursor cursor = getActivity().getContentResolver().query(contactUri, projection, null, null, null);
				cursor.moveToFirst();

				int column = cursor.getColumnIndex(Phone.DISPLAY_NAME);
				mTextLoanee.setText(cursor.getString(column));
				break;
			}
		}
	}
}
