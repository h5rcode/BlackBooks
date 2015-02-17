package com.blackbooks.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
import com.blackbooks.activities.BookGroupListActivity;
import com.blackbooks.activities.BookListActivity;
import com.blackbooks.activities.BookListActivity2;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookGroup;
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
    private LinearLayout mLayoutToRead;
    private LinearLayout mLayoutLoaned;
    private LinearLayout mLayoutFavourite;

    private TextView mTextBooksCount;
    private TextView mTextAuthorsCount;
    private TextView mTextCategoriesCount;
    private TextView mTextLanguagesCount;
    private TextView mTextSeriesCount;
    private TextView mTextLocationsCount;
    private TextView mTextToReadCount;
    private TextView mTextLoanedCount;
    private TextView mTextFavouriteCount;

    private TextView mTextLabelBookCount;
    private TextView mTextLabelAuthorCount;
    private TextView mTextLabelCategoryCount;
    private TextView mTextLabelLanguageCount;
    private TextView mTextLabelSeriesCount;
    private TextView mTextLabelLocationCount;
    private TextView mTextLabelToReadCount;
    private TextView mTextLabelLoanedCount;
    private TextView mTextLabelFavouriteCount;

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
        mLayoutToRead = (LinearLayout) view.findViewById(R.id.summary_layoutToRead);
        mLayoutLoaned = (LinearLayout) view.findViewById(R.id.summary_layoutLoaned);
        mLayoutFavourite = (LinearLayout) view.findViewById(R.id.summary_layoutFavourite);

        mLayoutBooks.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startBookGroupListActivity(BookGroup.BookGroupType.FIRST_LETTER);
            }
        });
        mLayoutAuthors.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startBookGroupListActivity(BookGroup.BookGroupType.AUTHOR);
            }
        });
        mLayoutCategories.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startBookGroupListActivity(BookGroup.BookGroupType.CATEGORY);
            }
        });
        mLayoutLanguages.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startBookGroupListActivity(BookGroup.BookGroupType.LANGUAGE);
            }
        });
        mLayoutSeries.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startBookGroupListActivity(BookGroup.BookGroupType.SERIES);
            }
        });
        mLayoutLocations.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startBookGroupListActivity(BookGroup.BookGroupType.BOOK_LOCATION);
            }
        });
        mLayoutToRead.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startBookListActivity(BookListByToReadReadFragment.class);
            }
        });
        mLayoutLoaned.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startBookGroupListActivity(BookGroup.BookGroupType.LOANED);
            }
        });
        mLayoutFavourite.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), BookListActivity2.class);
                i.putExtra(BookListActivity2.EXTRA_BOOK_GROUP_TYPE, BookGroup.BookGroupType.FAVOURITE);
                startActivity(i);
            }
        });

        mTextBooksCount = (TextView) view.findViewById(R.id.summary_booksCount);
        mTextAuthorsCount = (TextView) view.findViewById(R.id.summary_authorsCount);
        mTextCategoriesCount = (TextView) view.findViewById(R.id.summary_categoriesCount);
        mTextLanguagesCount = (TextView) view.findViewById(R.id.summary_languagesCount);
        mTextSeriesCount = (TextView) view.findViewById(R.id.summary_seriesCount);
        mTextLocationsCount = (TextView) view.findViewById(R.id.summary_locationsCount);
        mTextToReadCount = (TextView) view.findViewById(R.id.summary_toReadCount);
        mTextLoanedCount = (TextView) view.findViewById(R.id.summary_loanedCount);
        mTextFavouriteCount = (TextView) view.findViewById(R.id.summary_favouriteCount);

        mTextLabelBookCount = (TextView) view.findViewById(R.id.summary_textLabelBookCount);
        mTextLabelAuthorCount = (TextView) view.findViewById(R.id.summary_textLabelAuthorCount);
        mTextLabelCategoryCount = (TextView) view.findViewById(R.id.summary_textLabelCategoryCount);
        mTextLabelLanguageCount = (TextView) view.findViewById(R.id.summary_textLabelLanguageCount);
        mTextLabelSeriesCount = (TextView) view.findViewById(R.id.summary_textLabelSeriesCount);
        mTextLabelLocationCount = (TextView) view.findViewById(R.id.summary_textLabelLocationCount);
        mTextLabelToReadCount = (TextView) view.findViewById(R.id.summary_textLabelToReadCount);
        mTextLabelLoanedCount = (TextView) view.findViewById(R.id.summary_textLabelLoanedCount);
        mTextLabelFavouriteCount = (TextView) view.findViewById(R.id.summary_textLabelFavouriteCount);

        SQLiteDatabase db = SQLiteHelper.getInstance().getReadableDatabase();
        Summary summary = SummaryServices.getSummary(db);

        mTextBooksCount.setText(String.valueOf(summary.books));
        mTextAuthorsCount.setText(String.valueOf(summary.authors));
        mTextCategoriesCount.setText(String.valueOf(summary.categories));
        mTextLanguagesCount.setText(String.valueOf(summary.languages));
        mTextSeriesCount.setText(String.valueOf(summary.series));
        mTextLocationsCount.setText(String.valueOf(summary.bookLocations));
        mTextToReadCount.setText(String.valueOf(summary.toRead));
        mTextLoanedCount.setText(String.valueOf(summary.loaned));
        mTextFavouriteCount.setText(String.valueOf(summary.favourites));

        Resources res = getResources();

        mTextLabelBookCount.setText(res.getQuantityText(R.plurals.label_summary_books, summary.books));
        mTextLabelAuthorCount.setText(res.getQuantityText(R.plurals.label_summary_authors, summary.authors));
        mTextLabelCategoryCount.setText(res.getQuantityText(R.plurals.label_summary_categories, summary.categories));
        mTextLabelLanguageCount.setText(res.getQuantityText(R.plurals.label_summary_languages, summary.languages));
        mTextLabelSeriesCount.setText(res.getQuantityText(R.plurals.label_summary_series, summary.series));
        mTextLabelLocationCount.setText(res.getQuantityText(R.plurals.label_summary_locations, summary.bookLocations));
        mTextLabelToReadCount.setText(res.getQuantityText(R.plurals.label_summary_to_read, summary.toRead));
        mTextLabelLoanedCount.setText(res.getQuantityText(R.plurals.label_summary_loaned, summary.loaned));
        mTextLabelFavouriteCount.setText(res.getQuantityText(R.plurals.label_summary_favourites, summary.favourites));
    }

    /**
     * Start the {@link BookListActivity}.
     *
     * @param fragmentClass Name of the fragment's class to be displayed.
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

    /**
     * Start the {@link com.blackbooks.activities.BookGroupListActivity}.
     *
     * @param bookGroupType BookGroupType.
     */
    private void startBookGroupListActivity(BookGroup.BookGroupType bookGroupType) {
        Intent i = new Intent(getActivity(), BookGroupListActivity.class);
        i.putExtra(BookGroupListActivity.EXTRA_GROUP_BOOK_TYPE, bookGroupType);
        startActivity(i);
    }
}
