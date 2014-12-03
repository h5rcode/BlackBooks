package com.blackbooks.adapters;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blackbooks.R;
import com.blackbooks.cache.ThumbnailManager;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.utils.StringUtils;

/**
 * An adapter that displays the results of a book search. The matching text
 * inside the books' title is highlighted.
 */
public class BookSearchResultsAdapter extends ArrayAdapter<BookInfo> {

	private static final Pattern PATTERN = Pattern.compile("[^\\s\\p{Punct}]+");
	private final String mQuery;
	private final ThumbnailManager mThumbnailManager;
	private final LayoutInflater mInflater;

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
		this.mThumbnailManager = ThumbnailManager.getInstance();
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		BookInfo bookInfo = this.getItem(position);

		if (bookInfo != null) {
			view = mInflater.inflate(R.layout.search_results_item_book, parent, false);

			ImageView imageView = (ImageView) view.findViewById(R.id.search_results_item_book_small_thumbnail);
			ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.search_results_item_book_progressBar);
			TextView textTitle = (TextView) view.findViewById(R.id.search_results_item_book_title);
			TextView textSubtitle = (TextView) view.findViewById(R.id.search_results_item_book_subtitle);
			LinearLayout layoutDescription = (LinearLayout) view.findViewById(R.id.search_results_item_book_description_layout);
			TextView textDescriptionStart = (TextView) view.findViewById(R.id.search_results_item_book_description_start);
			TextView textDescriptionEnd = (TextView) view.findViewById(R.id.search_results_item_book_description_end);
			TextView textAuthor = (TextView) view.findViewById(R.id.search_results_item_book_author);

			mThumbnailManager.drawSmallThumbnail(bookInfo.id, getContext(), imageView, progressBar);
			textTitle.setText(highlight(mQuery, bookInfo.title));
			if (bookInfo.subtitle == null) {
				textSubtitle.setVisibility(View.GONE);
			} else {
				textSubtitle.setText(highlight(mQuery, bookInfo.subtitle));
			}
			if (bookInfo.description == null) {
				layoutDescription.setVisibility(View.GONE);
			} else {
				highlightDescription(mQuery, bookInfo.description, textDescriptionStart, textDescriptionEnd);
			}

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
	private static CharSequence highlight(String search, String originalText) {
		String normalizedOriginalText = StringUtils.normalize(originalText);
		String normalizedSearch = StringUtils.normalize(search);

		int start = firstIndexOf(normalizedSearch, normalizedOriginalText);
		if (start < 0) {
			return originalText;
		} else {
			Spannable highlighted = new SpannableString(originalText);
			if (start >= 0) {
				int spanStart = Math.min(start, originalText.length());
				int spanEnd = Math.min(start + normalizedSearch.length(), originalText.length());

				highlightSpannable(highlighted, spanStart, spanEnd);

				start = normalizedOriginalText.indexOf(normalizedSearch, spanEnd);
			}

			return highlighted;
		}
	}

	/**
	 * Highlight a portion of a spannable.
	 * 
	 * @param spannable
	 *            The spannable.
	 * @param highlightStart
	 *            Start index of the highlight within the spannable.
	 * @param highlightEnd
	 *            End index of the highlight within the spannable.
	 */
	private static void highlightSpannable(Spannable spannable, int highlightStart, int highlightEnd) {
		spannable.setSpan(new StyleSpan(Typeface.BOLD), highlightStart, highlightEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	private static void highlightDescription(String search, String originalText, TextView textDescriptionStart,
			TextView textDescriptionEnd) {
		String normalizedOriginalText = StringUtils.normalize(originalText);
		String normalizedSearch = StringUtils.normalize(search);

		int startIndex = firstIndexOf(normalizedSearch, normalizedOriginalText);

		if (startIndex >= 0) {
			int endIndex = startIndex + normalizedSearch.length();

			Spannable highlighted = new SpannableString(originalText.substring(0, endIndex));
			highlightSpannable(highlighted, startIndex, endIndex);
			String endText = originalText.substring(endIndex);

			textDescriptionStart.setText(highlighted);
			textDescriptionEnd.setText(endText);
		} else {
			textDescriptionStart.setVisibility(View.GONE);
			textDescriptionEnd.setText(originalText);
		}
	}

	/**
	 * Returns the index of the first token of a text that starts with a search
	 * term.
	 * 
	 * @param search
	 *            The search term.
	 * @param text
	 *            The searched text.
	 * @return Index of the first token that starts with the search term, -1 if
	 *         there is none.
	 */
	private static int firstIndexOf(String search, String text) {
		Matcher matcher = PATTERN.matcher(text);

		int startIndex = -1;

		boolean found = false;
		while (!found && matcher.find()) {
			String token = matcher.group();
			if (token.startsWith(search)) {
				found = true;
				startIndex = matcher.start();
			}
		}
		return startIndex;
	}
}
