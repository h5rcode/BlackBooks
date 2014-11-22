package com.blackbooks.adapters;

import java.util.List;

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

				view = mInflater.inflate(R.layout.list_books_by_first_letter_item_book, parent, false);

				ImageView imageView = (ImageView) view.findViewById(R.id.books_by_first_letter_item_book_small_thumbnail);
				ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.books_by_first_letter_item_book_progressBar);
				mThumbnailManager.drawSmallThumbnail(entry.getId(), getContext(), imageView, progressBar);
				TextView textTitle = (TextView) view.findViewById(R.id.books_by_first_letter_item_book_title);
				textTitle.setText(entry.getTitle());

				TextView textAuthor = (TextView) view.findViewById(R.id.books_by_first_letter_item_book_author);
				List<String> authorList = entry.getAuthors();
				String authors;
				if (authorList.size() > 0) {
					authors = StringUtils.join(entry.getAuthors().toArray(new String[] {}), ", ");
				} else {
					authors = getContext().getString(R.string.label_unspecified_author);
				}
				textAuthor.setText(authors);
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
