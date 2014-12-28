package com.blackbooks.fragments;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blackbooks.R;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.Summary;
import com.blackbooks.services.SummaryServices;

/**
 * A fragment displaying statistics of the library.
 */
public class SummaryFragment extends Fragment {

	private TextView mTextBooksCount;
	private TextView mTextAuthorsCount;
	private TextView mTextCategoriesCount;
	private TextView mTextLanguagesCount;
	private TextView mTextSeriesCount;
	private TextView mTextShelvesCount;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_summary, container, false);
	}

	@Override
	public void onResume() {
		super.onResume();

		View view = getView();
		mTextBooksCount = (TextView) view.findViewById(R.id.summary_booksNumber);
		mTextAuthorsCount = (TextView) view.findViewById(R.id.summary_authorsNumber);
		mTextCategoriesCount = (TextView) view.findViewById(R.id.summary_categoriesNumber);
		mTextLanguagesCount = (TextView) view.findViewById(R.id.summary_languagesNumber);
		mTextSeriesCount = (TextView) view.findViewById(R.id.summary_seriesNumber);
		mTextShelvesCount = (TextView) view.findViewById(R.id.summary_shelvesNumber);

		SQLiteHelper dbHelper = new SQLiteHelper(getActivity());
		SQLiteDatabase db = null;
		try {
			db = dbHelper.getReadableDatabase();
			Summary summary = SummaryServices.getSummary(db);

			mTextBooksCount.setText(String.valueOf(summary.books));
			mTextAuthorsCount.setText(String.valueOf(summary.authors));
			mTextCategoriesCount.setText(String.valueOf(summary.categories));
			mTextLanguagesCount.setText(String.valueOf(summary.languages));
			mTextSeriesCount.setText(String.valueOf(summary.series));
			mTextShelvesCount.setText(String.valueOf(summary.bookShelves));
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}
}
