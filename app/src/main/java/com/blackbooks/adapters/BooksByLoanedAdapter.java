package com.blackbooks.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blackbooks.R;
import com.blackbooks.cache.ThumbnailManager;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.utils.StringUtils;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * An adapter to render a list of books grouped by "To read" or "Read".
 */
@Deprecated
public class BooksByLoanedAdapter extends ArrayAdapter<ListItem> {

    private final LayoutInflater mInflater;
    private final ThumbnailManager mThumbnailManager;

    /**
     * Constructor.
     *
     * @param context Context.
     */
    public BooksByLoanedAdapter(Context context) {
        super(context, 0);
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mThumbnailManager = ThumbnailManager.getInstance();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        ListItem item = this.getItem(position);
        if (item != null) {
            ListItemType itemType = item.getListItemType();
            if (itemType == ListItemType.ENTRY) {
                BookItem entry = (BookItem) item;
                BookInfo book = entry.getBook();

                view = mInflater.inflate(R.layout.list_books_by_loaned_item_book, parent, false);

                ImageView imageView = (ImageView) view.findViewById(R.id.books_by_loaned_item_book_small_thumbnail);
                ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.books_by_loaned_item_book_progressBar);
                ImageView imageRead = (ImageView) view.findViewById(R.id.books_by_loaned_item_book_imageRead);
                ImageView imageFavourite = (ImageView) view.findViewById(R.id.books_by_loaned_item_book_imageFavourite);
                ImageView imageLoaned = (ImageView) view.findViewById(R.id.books_by_loaned_item_book_imageLoaned);

                mThumbnailManager.drawSmallThumbnail(book.id, getContext(), imageView, progressBar);
                TextView textTitle = (TextView) view.findViewById(R.id.books_by_loaned_item_book_title);
                textTitle.setText(book.title);

                TextView textAuthor = (TextView) view.findViewById(R.id.books_by_loaned_item_book_author);
                String authors;
                if (book.authors.size() > 0) {
                    authors = StringUtils.joinAuthorNameList(book.authors, ", ");
                } else {
                    authors = getContext().getString(R.string.label_unspecified_author);
                }
                textAuthor.setText(authors);

                if (book.isRead != 0) {
                    imageRead.setVisibility(View.VISIBLE);
                } else {
                    imageRead.setVisibility(View.GONE);
                }
                if (book.isFavourite != 0) {
                    imageFavourite.setVisibility(View.VISIBLE);
                } else {
                    imageFavourite.setVisibility(View.GONE);
                }
                if (book.loanedTo != null) {
                    imageLoaned.setVisibility(View.VISIBLE);
                } else {
                    imageLoaned.setVisibility(View.GONE);
                }

            } else if (itemType == ListItemType.HEADER_3) {
                LoanDateItem header3 = (LoanDateItem) item;

                view = mInflater.inflate(R.layout.list_books_by_loaned_item_loan_date, parent, false);

                TextView textViewLoanDate = (TextView) view.findViewById(R.id.books_by_loaned_item_loan_date);

                textViewLoanDate.setText(header3.getLoanDate());

            } else if (itemType == ListItemType.HEADER_2) {
                LoanedToItem header2 = (LoanedToItem) item;

                view = mInflater.inflate(R.layout.list_books_by_loaned_item_loaned_to, parent, false);

                TextView textViewName = (TextView) view.findViewById(R.id.books_by_loaned_item_loaned_to_loaned_to);
                textViewName.setText(header2.getLoanedTo());
            } else if (itemType == ListItemType.HEADER) {
                LoanedAvailableItem header = (LoanedAvailableItem) item;

                view = mInflater.inflate(R.layout.list_books_by_loaned_item_loaned_available, parent, false);

                TextView textViewLabel = (TextView) view.findViewById(R.id.books_by_loaned_item_loaned_available_label);
                TextView textViewTotal = (TextView) view.findViewById(R.id.books_by_loaned_item_loaned_available_total);

                if (header.isLoaned()) {
                    textViewLabel.setText(R.string.header_loaned);
                } else {
                    textViewLabel.setText(R.string.header_available);
                }
                String total = this.getContext().getString(R.string.label_total);
                total = String.format(total, header.getTotal());
                textViewTotal.setText(total);
            }
        }

        return view;
    }

    public static final class LoanedAvailableItem implements ListItem {

        private final boolean mIsLoaned;
        private final int mTotal;

        public LoanedAvailableItem(boolean isLoaned, int total) {
            mIsLoaned = isLoaned;
            mTotal = total;
        }

        @Override
        public ListItemType getListItemType() {
            return ListItemType.HEADER;
        }

        public boolean isLoaned() {
            return mIsLoaned;
        }

        public int getTotal() {
            return mTotal;
        }
    }

    public static final class LoanedToItem implements ListItem {

        private final String mLoanedTo;
        private final int mTotalBook;

        public LoanedToItem(String loanedTo, int totalBooks) {
            mLoanedTo = "\u2192 " + loanedTo;
            mTotalBook = totalBooks;
        }

        @Override
        public ListItemType getListItemType() {
            return ListItemType.HEADER_2;
        }

        public int getTotalBooks() {
            return mTotalBook;
        }

        public String getLoanedTo() {
            return mLoanedTo;
        }
    }

    public static final class LoanDateItem implements ListItem {

        private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());

        private final Context mContext;
        private final Date mLoanDate;

        public LoanDateItem(Context context, Date loanDate) {
            mContext = context;
            mLoanDate = loanDate;
        }

        @Override
        public ListItemType getListItemType() {
            return ListItemType.HEADER_3;
        }

        public String getLoanDate() {
            String loanDate = DATE_FORMAT.format(mLoanDate);

            Calendar dateCalendar = Calendar.getInstance();
            dateCalendar.setTime(mLoanDate);
            Calendar todayCalendar = Calendar.getInstance();
            todayCalendar.set(Calendar.HOUR_OF_DAY, 0);
            todayCalendar.set(Calendar.MINUTE, 0);
            todayCalendar.set(Calendar.SECOND, 0);
            todayCalendar.set(Calendar.MILLISECOND, 0);

            long todayDays = TimeUnit.DAYS.convert(todayCalendar.getTimeInMillis(), TimeUnit.MILLISECONDS);
            long dateDays = TimeUnit.DAYS.convert(dateCalendar.getTimeInMillis(), TimeUnit.MILLISECONDS);

            int diffYear = todayCalendar.get(Calendar.YEAR) - dateCalendar.get(Calendar.YEAR);
            int diffMonth = diffYear * 12 + todayCalendar.get(Calendar.MONTH) - dateCalendar.get(Calendar.MONTH);
            int diffDay = (int) (todayDays - dateDays);

            String timeAgo;
            Resources res = mContext.getResources();

            if (diffYear > 1) {
                timeAgo = res.getQuantityString(R.plurals.label_n_years_ago, diffYear, diffYear);
            } else if (diffMonth > 1) {
                timeAgo = res.getQuantityString(R.plurals.label_n_months_ago, diffMonth, diffMonth);
            } else if (diffDay > 0) {
                timeAgo = res.getQuantityString(R.plurals.label_n_days_ago, diffDay, diffDay);
            } else {
                timeAgo = mContext.getString(R.string.label_today);
            }

            return mContext.getString(R.string.label_loan_date_format, loanDate, timeAgo);
        }
    }
}
