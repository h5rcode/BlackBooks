package com.blackbooks.adapters;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackbooks.R;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.utils.StringUtils;

/**
 * An adapter that displays the results of a book search. The matching text
 * inside the books' title is highlighted.
 */
public class BookSearchResultsAdapter extends ArrayAdapter<BookInfo> {

	private String mQuery;
	private LayoutInflater mInflater;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            Context.
	 * @param query
	 *            The query that returned the books displayed by the adapter.
	 */
	public BookSearchResultsAdapter(Context context, String query) {
		super(context, 0);
		this.mQuery = query;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		BookInfo bookInfo = this.getItem(position);

		if (bookInfo != null) {
			view = mInflater.inflate(R.layout.list_books_by_first_letter_item_book, null);

			byte[] smallThumbnail = bookInfo.smallThumbnail;
			if (smallThumbnail != null && smallThumbnail.length > 0) {
				ImageView imageView = (ImageView) view.findViewById(R.id.books_by_first_letter_item_book_small_thumbnail);
				Bitmap bitmap = BitmapFactory.decodeByteArray(smallThumbnail, 0, smallThumbnail.length);
				imageView.setImageBitmap(bitmap);
			}

			TextView textTitle = (TextView) view.findViewById(R.id.books_by_first_letter_item_book_title);
			CharSequence highlightedTitle = highlight(mQuery, bookInfo.title);
			textTitle.setText(highlightedTitle);

			TextView textAuthor = (TextView) view.findViewById(R.id.books_by_first_letter_item_book_author);
			List<Author> authorList = bookInfo.authors;
			String authors;
			if (authorList.size() > 0) {
				authors = StringUtils.joinAuthorNameList(bookInfo.authors, ", ");
			} else {
				authors = getContext().getString(R.string.label_unspecified_author);
			}
			textAuthor.setText(authors);
		}

		return view;
	}

	/**
	 * Highlight the parts of the original text that match the searched text.
	 * 
	 * @param search
	 *            Searched text.
	 * @param originalText
	 *            Original text.
	 * @return A CharSequence where the parts matching the searched text are
	 *         highlighted.
	 */
	public static CharSequence highlight(String search, String originalText) {
		// ignore case and accents
		// the same thing should have been done for the search text
		String normalizedOriginalText = StringUtils.normalize(originalText);
		String normalizedSearch = StringUtils.normalize(search);

		int start = normalizedOriginalText.indexOf(normalizedSearch);
		if (start < 0) {
			// not found, nothing to to
			return originalText;
		} else {
			// highlight each appearance in the original text
			// while searching in normalized text
			Spannable highlighted = new SpannableString(originalText);
			while (start >= 0) {
				int spanStart = Math.min(start, originalText.length());
				int spanEnd = Math.min(start + normalizedSearch.length(), originalText.length());

				highlighted.setSpan(new StyleSpan(Typeface.BOLD), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

				start = normalizedOriginalText.indexOf(normalizedSearch, spanEnd);
			}

			return highlighted;
		}
	}
}
