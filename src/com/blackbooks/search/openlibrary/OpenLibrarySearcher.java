package com.blackbooks.search.openlibrary;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import com.blackbooks.utils.HttpUtils;

/**
 * Class that searches books using the OpenLibrary API.
 * 
 */
public final class OpenLibrarySearcher {

	private final static String URI_FORMAT_STRING = "https://openlibrary.org/api/books?bibkeys=ISBN:%s&jscmd=data&format=json";

	/**
	 * Private constructor.
	 */
	private OpenLibrarySearcher() {
	}

	/**
	 * Search book info.
	 * 
	 * @param isbn
	 *            ISBN code.
	 * @return OpenLibraryBook.
	 * @throws URISyntaxException
	 * @throws ClientProtocolException
	 * @throws JSONException
	 */
	public static OpenLibraryBook search(String isbn) throws URISyntaxException, ClientProtocolException, JSONException {
		String url = String.format(URI_FORMAT_STRING, isbn);
		try {
			String json = HttpUtils.getJson(url);
			OpenLibraryBook openLibraryBook = OpenLibraryJSONParser.parse(json);
			if (openLibraryBook != null) {
				if (openLibraryBook.coverLinkSmall != null) {
					openLibraryBook.coverSmall = HttpUtils.getBinary(openLibraryBook.coverLinkSmall);
				}
				if (openLibraryBook.coverLinkMedium != null) {
					openLibraryBook.coverMedium = HttpUtils.getBinary(openLibraryBook.coverLinkMedium);
				}
			}
			return openLibraryBook;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
