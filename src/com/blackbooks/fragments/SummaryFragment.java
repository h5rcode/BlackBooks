package com.blackbooks.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blackbooks.R;
import com.blackbooks.activities.BookListActivity;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.Summary;
import com.blackbooks.services.SummaryServices;

/**
 * A fragment displaying statistics of the library.
 */
public class SummaryFragment extends Fragment {

	private LinearLayout mLayoutBooks;
	private LinearLayout mLayoutAuthors;
	private LinearLayout mLayoutCategories;
	private LinearLayout mLayoutLanguages;
	private LinearLayout mLayoutSeries;
	private LinearLayout mLayoutLocations;

	private TextView mTextBooksCount;
	private TextView mTextAuthorsCount;
	private TextView mTextCategoriesCount;
	private TextView mTextLanguagesCount;
	private TextView mTextSeriesCount;
	private TextView mTextLocationsCount;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_summary, container, false);
	}

	@Override
	public void onResume() {
		super.onResume();

		View view = getView();
		mLayoutBooks = (LinearLayout) view.findViewById(R.id.summary_layoutBooks);
		mLayoutAuthors = (LinearLayout) view.findViewById(R.id.summary_layoutAuthors);
		mLayoutCategories = (LinearLayout) view.findViewById(R.id.summary_layoutCategories);
		mLayoutLanguages = (LinearLayout) view.findViewById(R.id.summary_layoutLanguages);
		mLayoutSeries = (LinearLayout) view.findViewById(R.id.summary_layoutSeries);
		mLayoutLocations = (LinearLayout) view.findViewById(R.id.summary_layoutLocations);

		mLayoutBooks.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startBookListActivity(BookListByFirstLetterFragment.class);
			}
		});
		mLayoutAuthors.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startBookListActivity(BookListByAuthorFragment.class);
			}
		});
		mLayoutCategories.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startBookListActivity(BookListByCategoryFragment.class);
			}
		});
		mLayoutLanguages.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startBookListActivity(BookListByLanguageFragment.class);
			}
		});
		mLayoutSeries.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startBookListActivity(BookListBySeriesFragment.class);
			}
		});
		mLayoutLocations.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startBookListActivity(BookListByBookLocationFragment.class);
			}
		});

		mTextBooksCount = (TextView) view.findViewById(R.id.summary_booksCount);
		mTextAuthorsCount = (TextView) view.findViewById(R.id.summary_authorsCount);
		mTextCategoriesCount = (TextView) view.findViewById(R.id.summary_categoriesCount);
		mTextLanguagesCount = (TextView) view.findViewById(R.id.summary_languagesCount);
		mTextSeriesCount = (TextView) view.findViewById(R.id.summary_seriesCount);
		mTextLocationsCount = (TextView) view.findViewById(R.id.summary_LocationsCount);

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
			mTextLocationsCount.setText(String.valueOf(summary.bookLocations));
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	/**
	 * Start the {@link BookListActivity}.
	 * 
	 * @param fragmentClass
	 *            Name of the fragment's class to be displayed.
	 */
	private void startBookListActivity(Class<?> fragmentClass) {
		String className = fragmentClass.getName();

		SharedPreferences sharedPref = getActivity().getSharedPreferences(BookListActivity.PREFERENCES, Context.MODE_PRIVATE);
		String defaultList = sharedPref.getString(BookListActivity.PREF_DEFAULT_LIST, null);
		if (!className.equals(defaultList)) {
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString(BookListActivity.PREF_DEFAULT_LIST, className);
			editor.commit();
		}
		Intent i = new Intent(getActivity(), BookListActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(i);
	}
}
