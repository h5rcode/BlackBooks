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
import com.blackbooks.utils.OpenLibraryBookUtils;

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

		BookInfo bookInfo = mergeSearchResults(googleBook, openLibraryBook);

		return bookInfo;
	}

	/**
	 * Merge the search results into an instance of BookInfo.
	 * 
	 * @param googleBook
	 *            GoogleBook.
	 * @param openLibraryBook
	 *            OpenLibraryBook.
	 * @return BookInfo.
	 */
	private static BookInfo mergeSearchResults(GoogleBook googleBook, OpenLibraryBook openLibraryBook) {
		BookInfo bookInfo = null;
		BookInfo googleBookInfo = null;
		BookInfo openLibraryBookInfo = null;
		if (googleBook != null) {
			bookInfo = new BookInfo();
			googleBookInfo = GoogleBookUtils.toBookInfo(googleBook);

			bookInfo.merge(googleBookInfo);
		}
		if (openLibraryBook != null) {
			if (bookInfo == null) {
				bookInfo = new BookInfo();
			}
			openLibraryBookInfo = OpenLibraryBookUtils.toBookInfo(openLibraryBook);
			bookInfo.merge(openLibraryBookInfo);
		}

		return bookInfo;
	}
}
