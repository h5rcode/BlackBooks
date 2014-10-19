package com.blackbooks.search.google;

import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import com.blackbooks.utils.HttpUtils;

/**
 * Class that searches books using the Google books API.
 * 
 */
public class GoogleBooksSearcher {

	private final static String URI_FORMAT_STRING = "https://www.googleapis.com/books/v1/volumes?q=isbn:%s";

	/**
	 * Search book info.
	 * 
	 * @param isbn
	 *            ISBN code.
	 * @return GoogleBook.
	 * @throws URISyntaxException
	 *             If the requested URI could not be parsed.
	 * @throws ClientProtocolException
	 *             If an HTTP error occurs.
	 * @throws JSONException
	 *             If something bad happened during the parsing of the JSON
	 *             result.
	 * @throws UnknownHostException
	 */
	public static GoogleBook search(String isbn) throws URISyntaxException, ClientProtocolException, JSONException, UnknownHostException {

		try {
			String url = String.format(URI_FORMAT_STRING, isbn);
			String json = HttpUtils.getJson(url);

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
		} catch (ClientProtocolException e) {
			throw e;
		}
	}
}
