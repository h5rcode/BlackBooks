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
 * An adapter handling instances of ListItem representing either the first
 * letter of a book or a book.
 */
public class BooksByFirstLetterAdapter extends ArrayAdapter<ListItem> {

	private final LayoutInflater mInflater;
	private final ThumbnailManager mThumbnailManager;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            Context.
	 */
	public BooksByFirstLetterAdapter(Context context) {
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
			if (itemType == ListItemType.Entry) {
				BookItem entry = (BookItem) item;
				BookInfo book = entry.getBook();

				view = mInflater.inflate(R.layout.list_books_by_first_letter_item_book, parent, false);

				ImageView imageView = (ImageView) view.findViewById(R.id.books_by_first_letter_item_book_small_thumbnail);
				ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.books_by_first_letter_item_book_progressBar);
				ImageView imageRead = (ImageView) view.findViewById(R.id.books_by_first_letter_item_book_imageRead);
				ImageView imageFavourite = (ImageView) view.findViewById(R.id.books_by_first_letter_item_book_imageFavourite);

				mThumbnailManager.drawSmallThumbnail(book.id, getContext(), imageView, progressBar);
				TextView textTitle = (TextView) view.findViewById(R.id.books_by_first_letter_item_book_title);
				textTitle.setText(book.title);

				TextView textAuthor = (TextView) view.findViewById(R.id.books_by_first_letter_item_book_author);
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

			} else if (itemType == ListItemType.Header) {
				FirstLetterItem header = (FirstLetterItem) item;

				view = mInflater.inflate(R.layout.list_books_by_first_letter_item_letter, parent, false);

				TextView textViewName = (TextView) view.findViewById(R.id.books_by_first_letter_item_letter);
				textViewName.setText(header.getValue());
			}
		}

		return view;
	}
}
