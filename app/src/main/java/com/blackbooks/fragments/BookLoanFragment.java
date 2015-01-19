package com.blackbooks.fragments;

import java.text.ParseException;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.fragments.dialogs.DatePickerFragment;
import com.blackbooks.fragments.dialogs.DatePickerFragment.DatePickerListener;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.services.BookServices;
import com.blackbooks.utils.DateUtils;
import com.blackbooks.utils.VariableUtils;

/**
 * A fragment to loan a book.
 */
public class BookLoanFragment extends Fragment implements DatePickerListener {

	private static final String ARG_BOOK_ID = "ARG_BOOK_ID";
	private static final String TAG_DATE_PICKER_FRAGMENT = "TAG_DATE_PICKER_FRAGMENT";
	private static final int REQUEST_PICK_CONTACT = 0;

	private BookLoanListener mBookLoanListener;

	private LinearLayout mLayoutNotLoaned;
	private LinearLayout mLayoutLoaned;

	private EditText mTextLoanTo;
	private EditText mTextLoanDate;

	private TextView mTextLoanedTo;
	private TextView mTextLoanedOn;

	private SQLiteHelper mDbHelper;
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
		mDbHelper = new SQLiteHelper(activity);
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		mBookInfo = BookServices.getBookInfo(db, bookId);
		db.close();

		mBookLoanListener = (BookLoanListener) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_book_loan, container, false);

		mLayoutNotLoaned = (LinearLayout) view.findViewById(R.id.bookLoan_layoutNotLoaned);
		mLayoutLoaned = (LinearLayout) view.findViewById(R.id.bookLoan_layoutLoaned);

		ImageButton buttonPickContact = (ImageButton) view.findViewById(R.id.bookLoan_buttonPickContact);
		ImageButton buttonPickDate = (ImageButton) view.findViewById(R.id.bookLoan_buttonPickDate);

		mTextLoanTo = (EditText) view.findViewById(R.id.bookLoan_textLoanee);
		mTextLoanDate = (EditText) view.findViewById(R.id.bookLoan_textLoanDate);

		mTextLoanedTo = (TextView) view.findViewById(R.id.bookLoan_textLoanedTo);
		mTextLoanedOn = (TextView) view.findViewById(R.id.bookLoan_textLoanedOn);

		buttonPickContact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
				pickContactIntent.setType(Contacts.CONTENT_TYPE);
				startActivityForResult(pickContactIntent, REQUEST_PICK_CONTACT);
			}
		});

		buttonPickDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DatePickerFragment datePicker = new DatePickerFragment();
				datePicker.setTargetFragment(BookLoanFragment.this, 0);
				datePicker.show(getFragmentManager(), TAG_DATE_PICKER_FRAGMENT);
			}
		});

		if (mBookInfo.loanedTo != null) {
			mLayoutNotLoaned.setVisibility(View.GONE);
			mLayoutLoaned.setVisibility(View.VISIBLE);

			mTextLoanedTo.setText(mBookInfo.loanedTo);
			mTextLoanedOn.setText(DateUtils.DEFAULT_DATE_FORMAT.format(mBookInfo.loanDate));

		} else {
			mLayoutNotLoaned.setVisibility(View.VISIBLE);
			mLayoutLoaned.setVisibility(View.GONE);
		}

		return view;
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		if (mBookInfo.loanedTo == null) {
			inflater.inflate(R.menu.book_loan_not_loaned, menu);
		} else {
			inflater.inflate(R.menu.book_loan_loaned, menu);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result;

		switch (item.getItemId()) {
		case R.id.bookLoan_actionLoan:
			loanBook();
			result = true;
			break;

		case R.id.bookLoan_actionReturn:
			showConfirmReturnDialog();
			result = true;
			break;

		default:
			result = super.onOptionsItemSelected(item);
			break;
		}

		return result;
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
				mTextLoanTo.setError(null);
				mTextLoanTo.setText(cursor.getString(column));
				break;
			}
		}
	}

	@Override
	public void onDateSet(Date date) {
		mTextLoanDate.setError(null);
		mTextLoanDate.setText(DateUtils.DEFAULT_DATE_FORMAT.format(date));
	}

	/**
	 * Return the book (sets {@link Book#loanedTo} and {@link Book#loanDate} and
	 * save it).
	 */
	private void loanBook() {
		boolean isValid = true;

		String loanTo = mTextLoanTo.getText().toString();
		String loanDateString = mTextLoanDate.getText().toString();

		Date loanDate = null;
		try {
			loanDate = DateUtils.DEFAULT_DATE_FORMAT.parse(loanDateString);
		} catch (ParseException e) {
			// Do nothing, the null value of loanDate will be handled.
		}

		if (loanTo == null || loanTo.equals("")) {
			mTextLoanTo.setError(getString(R.string.field_mandatory));
			if (isValid) {
				mTextLoanTo.requestFocus();
				isValid = false;
			}
		} else {
			mTextLoanTo.setError(null);
			mBookInfo.loanedTo = loanTo;
		}
		Date now = new Date();
		if (loanDate == null) {
			mTextLoanDate.setError(getString(R.string.field_invalid_date));
			if (isValid) {
				mTextLoanDate.requestFocus();
				isValid = false;
			}
		} else if (loanDate.compareTo(now) > 0) {
			mTextLoanDate.setError(getString(R.string.field_invalid_loan_date));
			if (isValid) {
				mTextLoanDate.requestFocus();
				isValid = false;
			}
		} else {
			mTextLoanDate.setError(null);
			mBookInfo.loanDate = loanDate;
		}

		if (isValid) {
			SQLiteDatabase db = mDbHelper.getWritableDatabase();
			BookServices.saveBookInfo(db, mBookInfo);
			db.close();

			mTextLoanTo.setText("");
			mTextLoanDate.setText("");

			String message = getString(R.string.message_book_loaned);
			message = String.format(message, mBookInfo.title, mBookInfo.loanedTo);
			Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

			VariableUtils.getInstance().setReloadBookList(true);
			mBookLoanListener.onBookLoaned();
		}
	}

	/**
	 * Return the book (sets {@link Book#loanedTo} and {@link Book#loanDate} to
	 * null and save it).
	 */
	private void returnBook() {
		mBookInfo.loanedTo = null;
		mBookInfo.loanDate = null;

		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		BookServices.returnBook(db, mBookInfo.id);
		db.close();

		String message = getString(R.string.message_book_returned);
		message = String.format(message, mBookInfo.title);
		Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

		VariableUtils.getInstance().setReloadBookList(true);
		mBookLoanListener.onBookReturned();
	}

	/**
	 * Show the return book confirm dialog.
	 */
	private void showConfirmReturnDialog() {
		String message = getString(R.string.message_confirm_return_book, mBookInfo.title);

		String cancelText = getString(R.string.message_confirm_return_book_cancel);
		String confirmText = getString(R.string.message_confirm_return_book_confirm);

		new AlertDialog.Builder(this.getActivity()) //
				.setTitle(R.string.title_dialog_return_book) //
				.setMessage(message) //
				.setPositiveButton(confirmText, new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						returnBook();
					}
				}).setNegativeButton(cancelText, new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Do nothing.
					}
				}).show();
	}

	/**
	 * Activities hosting a {@link BookLoanFragment} should implement this
	 * interface to be notified when a book is loaned or returned.
	 * 
	 */
	public interface BookLoanListener {

		/**
		 * Called when a book has been loaned.
		 */
		void onBookLoaned();

		/**
		 * Called when a book has been returned.
		 */
		void onBookReturned();
	}
}
