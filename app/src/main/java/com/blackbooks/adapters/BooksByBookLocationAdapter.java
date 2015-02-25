package com.blackbooks.adapters;

import android.content.Context;
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
import com.blackbooks.model.nonpersistent.BookLocationInfo;
import com.blackbooks.utils.StringUtils;

/**
 * An adapter handling instances of ListItem representing either a book location
 * or a book.
 */
@Deprecated
public class BooksByBookLocationAdapter extends ArrayAdapter<ListItem> {

    private final LayoutInflater mInflater;
    private final ThumbnailManager mThumbnailManager;

    /**
     * Constructor.
     *
     * @param context Context.
     */
    public BooksByBookLocationAdapter(Context context) {
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

                view = mInflater.inflate(R.layout.list_books_by_booklocation_item_book, parent, false);

                ImageView imageView = (ImageView) view.findViewById(R.id.books_by_booklocation_item_book_small_thumbnail);
                ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.books_by_booklocation_item_book_progressBar);
                ImageView imageRead = (ImageView) view.findViewById(R.id.books_by_booklocation_item_book_imageRead);
                ImageView imageFavourite = (ImageView) view.findViewById(R.id.books_by_booklocation_item_book_imageFavourite);
                ImageView imageLoaned = (ImageView) view.findViewById(R.id.books_by_booklocation_item_book_imageLoaned);

                mThumbnailManager.drawSmallThumbnail(book.id, getContext(), imageView, progressBar);
                TextView textTitle = (TextView) view.findViewById(R.id.books_by_booklocation_item_book_title);
                textTitle.setText(book.title);

                TextView textAuthor = (TextView) view.findViewById(R.id.books_by_booklocation_item_book_author);
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
            } else if (itemType == ListItemType.HEADER) {
                BookLocationItem header = (BookLocationItem) item;
                BookLocationInfo bookLocation = header.getBookLocation();

                view = mInflater.inflate(R.layout.list_books_by_booklocation_item_booklocation, parent, false);

                TextView textViewName = (TextView) view.findViewById(R.id.books_by_booklocation_name);
                TextView textViewTotal = (TextView) view.findViewById(R.id.books_by_booklocation_item_total);

                textViewName.setText(bookLocation.name);
                String total = this.getContext().getString(R.string.label_total);
                total = String.format(total, bookLocation.books.size());
                textViewTotal.setText(total);
            }
        }

        return view;
    }

    public static final class BookLocationItem implements ListItem {

        private final BookLocationInfo mBookLocationInfo;

        public BookLocationItem(BookLocationInfo bookLocationInfo) {
            mBookLocationInfo = bookLocationInfo;
        }

        @Override
        public ListItemType getListItemType() {
            return ListItemType.HEADER;
        }

        public BookLocationInfo getBookLocation() {
            return mBookLocationInfo;
        }
    }
}
