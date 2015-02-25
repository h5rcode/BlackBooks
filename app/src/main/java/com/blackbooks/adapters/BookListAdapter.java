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
import com.blackbooks.utils.StringUtils;


/**
 * The adapter of the book list.
 */
public final class BookListAdapter extends ArrayAdapter<BookInfo> {

    private final LayoutInflater mLayoutInflater;
    private final ThumbnailManager mThumbnailManager;

    /**
     * Constructor.
     *
     * @param context Context.
     */
    public BookListAdapter(Context context) {
        super(context, 0);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mThumbnailManager = ThumbnailManager.getInstance();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_books_item, parent, false);
        }
        BookInfo book = getItem(position);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.books_item_book_small_thumbnail);
        ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.books_item_book_progressBar);
        ImageView imageRead = (ImageView) convertView.findViewById(R.id.books_item_book_imageRead);
        ImageView imageFavourite = (ImageView) convertView.findViewById(R.id.books_item_book_imageFavourite);
        ImageView imageLoaned = (ImageView) convertView.findViewById(R.id.books_item_book_imageLoaned);

        mThumbnailManager.drawSmallThumbnail(book.id, getContext(), imageView, progressBar);
        TextView textTitle = (TextView) convertView.findViewById(R.id.books_item_book_title);
        textTitle.setText(book.title);

        TextView textAuthor = (TextView) convertView.findViewById(R.id.books_item_book_author);
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

        return convertView;
    }
}
