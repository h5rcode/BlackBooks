package com.blackbooks.search.google;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;

import org.json.JSONException;

import com.blackbooks.search.BookSearchResult;
import com.blackbooks.utils.HttpUtils;

/**
 * Class that searches books using the Google books API.
 */
public final class GoogleBooksSearcher implements Callable<BookSearchResult> {

	private static final String URI_FORMAT_STRING = "https://www.googleapis.com/books/v1/volumes?q=isbn:%s";

	private String mIsbn;

	/**
	 * Constructor.
	 * 
	 * @param isbn
	 *            ISBN code.
	 */
	public GoogleBooksSearcher(String isbn) {
		mIsbn = isbn;
	}

	@Override
	public BookSearchResult call() throws Exception {
		return search(mIsbn);
	}

	/**
	 * Search book info.
	 * 
	 * @param isbn
	 *            ISBN code.
	 * @return GoogleBook.
	 * @throws URISyntaxException
	 *             If an incorrect URI was built for the search.
	 * @throws JSONException
	 *             If something bad happened during the parsing of the result.
	 * @throws IOException
	 *             If a connection problem occurred.
	 */
	private static GoogleBook search(String isbn) throws URISyntaxException, JSONException, IOException {

		String url = String.format(URI_FORMAT_STRING, isbn);
		String json = HttpUtils.getText(url);

		GoogleBook googleBook = new GoogleBooksJSONParser().parse(json);
		if (googleBook != null) {
			if (googleBook.thumbnailLink != null) {
				googleBook.thumbnail = HttpUtils.getBinary(googleBook.thumbnailLink);
			}
			if (googleBook.smallThumbnailLink != null) {
				googleBook.smallThumbnail = HttpUtils.getBinary(googleBook.smallThumbnailLink);
			}
		}
		return googleBook;
	}
}
