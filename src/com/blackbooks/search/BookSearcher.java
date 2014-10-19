package com.blackbooks.search;

import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.search.google.GoogleBook;
import com.blackbooks.search.google.GoogleBooksSearcher;
import com.blackbooks.search.openlibrary.OpenLibraryBook;
import com.blackbooks.search.openlibrary.OpenLibrarySearcher;
import com.blackbooks.utils.GoogleBookUtils;
import com.blackbooks.utils.HttpUtils;

/**
 * A class that searches for books using ISBN numbers.
 */
public final class BookSearcher {

	/**
	 * Private constructor.
	 */
	private BookSearcher() {
	}

	/**
	 * Search information about the book corresponding to the given ISBN.
	 * 
	 * TODO: Properly merge info retrieved from GoogleBooksAPI and OpenLibrary.
	 * 
	 * @param isbn
	 *            ISBN.
	 * @return An instance of BookInfo if information were found, null
	 *         otherwise.
	 * @throws ClientProtocolException
	 * @throws UnknownHostException
	 * @throws URISyntaxException
	 * @throws JSONException
	 */
	public static BookInfo search(String isbn) throws ClientProtocolException, UnknownHostException, URISyntaxException, JSONException {
		GoogleBook googleBook = GoogleBooksSearcher.search(isbn);
		OpenLibraryBook openLibraryBook = OpenLibrarySearcher.search(isbn);

		BookInfo bookInfo = null;
		if (googleBook != null) {
			bookInfo = GoogleBookUtils.toBookInfo(googleBook);
		}

		if (openLibraryBook != null) {
			if (bookInfo == null) {
				bookInfo = new BookInfo();
			}
			if (openLibraryBook.coverLinkSmall != null) {
				bookInfo.smallThumbnail = HttpUtils.getBinary(openLibraryBook.coverLinkSmall);
			}
			if (openLibraryBook.coverLinkMedium != null) {
				bookInfo.thumbnail = HttpUtils.getBinary(openLibraryBook.coverLinkMedium);
			}
		}

		return bookInfo;
	}
}
