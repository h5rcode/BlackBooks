package com.blackbooks.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackbooks.R;
import com.blackbooks.utils.StringUtils;

/**
 * An adapter handling instances of ListItem representing either the first
 * letter of a book or a book.
 */
public class BooksByFirstLetterAdapter extends ArrayAdapter<ListItem> {

	private LayoutInflater mInflater;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            Context.
	 */
	public BooksByFirstLetterAdapter(Context context) {
		super(context, 0);
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		ListItem item = this.getItem(position);
		if (item != null) {
			ListItemType itemType = item.getListItemType();
			if (itemType == ListItemType.Entry) {
				BookItem entry = (BookItem) item;

				view = mInflater.inflate(R.layout.list_books_by_first_letter_item_book, null);

				byte[] smallThumbnail = entry.getSmallThumbnail();
				if (smallThumbnail != null && smallThumbnail.length > 0) {
					ImageView imageView = (ImageView) view.findViewById(R.id.books_by_first_letter_item_book_small_thumbnail);
					Bitmap bitmap = BitmapFactory.decodeByteArray(smallThumbnail, 0, smallThumbnail.length);
					imageView.setImageBitmap(bitmap);
				}

				TextView textTitle = (TextView) view.findViewById(R.id.books_by_first_letter_item_book_title);
				textTitle.setText(entry.getTitle());

				TextView textAuthor = (TextView) view.findViewById(R.id.books_by_first_letter_item_book_author);
				String authors = StringUtils.join(entry.getAuthors().toArray(new String[] {}), ", ");
				textAuthor.setText(authors);
			} else if (itemType == ListItemType.Header) {
				FirstLetterItem header = (FirstLetterItem) item;

				view = mInflater.inflate(R.layout.list_books_by_first_letter_item_letter, null);

				TextView textViewName = (TextView) view.findViewById(R.id.books_by_first_letter_item_letter);
				textViewName.setText(header.getValue());
			}
		}

		return view;
	}
}
