package com.blackbooks.fragments.summary;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blackbooks.R;
import com.blackbooks.activities.BookGroupListActivity;
import com.blackbooks.activities.BookListActivity;
import com.blackbooks.model.nonpersistent.BookGroup;
import com.blackbooks.model.nonpersistent.Summary;
import com.blackbooks.services.SummaryService;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

/**
 * A fragment displaying statistics of the library.
 */
public final class SummaryFragment extends Fragment {

    @Inject
    SummaryService summaryService;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_summary, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        View view = getView();
        CardView mLayoutBooks = (CardView) view.findViewById(R.id.summary_layoutBooks);
        CardView mLayoutAuthors = (CardView) view.findViewById(R.id.summary_layoutAuthors);
        CardView mLayoutCategories = (CardView) view.findViewById(R.id.summary_layoutCategories);
        CardView mLayoutLanguages = (CardView) view.findViewById(R.id.summary_layoutLanguages);
        CardView mLayoutSeries = (CardView) view.findViewById(R.id.summary_layoutSeries);
        CardView mLayoutLocations = (CardView) view.findViewById(R.id.summary_layoutLocations);
        CardView mLayoutToRead = (CardView) view.findViewById(R.id.summary_layoutToRead);
        CardView mLayoutLoaned = (CardView) view.findViewById(R.id.summary_layoutLoans);
        CardView mLayoutFavourite = (CardView) view.findViewById(R.id.summary_layoutFavourite);

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
                Intent i = new Intent(getActivity(), BookListActivity.class);
                i.putExtra(BookListActivity.EXTRA_BOOK_GROUP_TYPE, BookGroup.BookGroupType.TO_READ);
                startActivity(i);
            }
        });
        mLayoutLoaned.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startBookGroupListActivity(BookGroup.BookGroupType.LOAN);
            }
        });
        mLayoutFavourite.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), BookListActivity.class);
                i.putExtra(BookListActivity.EXTRA_BOOK_GROUP_TYPE, BookGroup.BookGroupType.FAVOURITE);
                startActivity(i);
            }
        });

        TextView mTextBooksCount = (TextView) view.findViewById(R.id.summary_booksCount);
        TextView mTextAuthorsCount = (TextView) view.findViewById(R.id.summary_authorsCount);
        TextView mTextCategoriesCount = (TextView) view.findViewById(R.id.summary_categoriesCount);
        TextView mTextLanguagesCount = (TextView) view.findViewById(R.id.summary_languagesCount);
        TextView mTextSeriesCount = (TextView) view.findViewById(R.id.summary_seriesCount);
        TextView mTextLocationsCount = (TextView) view.findViewById(R.id.summary_locationsCount);
        TextView mTextToReadCount = (TextView) view.findViewById(R.id.summary_toReadCount);
        TextView mTextLoanedCount = (TextView) view.findViewById(R.id.summary_loanCount);
        TextView mTextFavouriteCount = (TextView) view.findViewById(R.id.summary_favouriteCount);

        Summary summary = summaryService.getSummary();
        Resources res = getResources();

        mTextBooksCount.setText(res.getQuantityString(R.plurals.label_summary_books, summary.books, summary.books));
        mTextAuthorsCount.setText(res.getQuantityString(R.plurals.label_summary_authors, summary.authors, summary.authors));
        mTextCategoriesCount.setText(res.getQuantityString(R.plurals.label_summary_categories, summary.categories, summary.categories));
        mTextLanguagesCount.setText(res.getQuantityString(R.plurals.label_summary_languages, summary.languages, summary.languages));
        mTextSeriesCount.setText(res.getQuantityString(R.plurals.label_summary_series, summary.series, summary.series));
        mTextLocationsCount.setText(res.getQuantityString(R.plurals.label_summary_locations, summary.bookLocations, summary.bookLocations));
        mTextToReadCount.setText(res.getQuantityString(R.plurals.label_summary_to_read, summary.toRead, summary.toRead));
        mTextLoanedCount.setText(res.getQuantityString(R.plurals.label_summary_loans, summary.loans, summary.loans));
        mTextFavouriteCount.setText(res.getQuantityString(R.plurals.label_summary_favourites, summary.favourites, summary.favourites));
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
