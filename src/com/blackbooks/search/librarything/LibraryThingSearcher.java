package com.blackbooks.search.librarything;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * LibraryThing searcher. Requires a developer key.
 * https://www.librarything.com/.
 */
public final class LibraryThingSearcher {

	private final static String DEVELOPER_KEY = "";
	private final static String URI_FORMAT_STRING = "http://covers.librarything.com/devkey/%s/%s/isbn/%s";

	private final static String SIZE_SMALL = "small";
	private final static String SIZE_MEDIUM = "medium";
	private final static String SIZE_LARGE = "large";

	/**
	 * Private constructor.
	 */
	private LibraryThingSearcher() {
	}

	/**
	 * Get the small cover of a book.
	 * 
	 * @param isbn
	 *            ISBN.
	 * @return Cover.
	 * @throws ClientProtocolException
	 * @throws URISyntaxException
	 */
	public static byte[] getSmallCover(String isbn) throws ClientProtocolException, URISyntaxException {
		return getCover(isbn, SIZE_SMALL);
	}

	/**
	 * Get the medium-size cover of a book.
	 * 
	 * @param isbn
	 *            ISBN.
	 * @return Cover.
	 * @throws ClientProtocolException
	 * @throws URISyntaxException
	 */
	public static byte[] getMediumCover(String isbn) throws ClientProtocolException, URISyntaxException {
		return getCover(isbn, SIZE_MEDIUM);
	}

	/**
	 * Get the large cover of a book.
	 * 
	 * @param isbn
	 *            ISBN.
	 * @return Cover.
	 * @throws ClientProtocolException
	 * @throws URISyntaxException
	 */
	public static byte[] getLargeCover(String isbn) throws ClientProtocolException, URISyntaxException {
		return getCover(isbn, SIZE_LARGE);
	}

	/**
	 * Get the cover of a book.
	 * 
	 * @param isbn
	 *            ISBN.
	 * @param size
	 *            Size (small/medium/large).
	 * @return Cover.
	 * @throws ClientProtocolException
	 * @throws URISyntaxException
	 */
	private static byte[] getCover(String isbn, String size) throws URISyntaxException, ClientProtocolException {
		String url = String.format(URI_FORMAT_STRING, DEVELOPER_KEY, size, isbn);
		URI uri = new URI(url);
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet get = new HttpGet(uri);

		HttpResponse response;
		try {
			response = httpclient.execute(get);
			return EntityUtils.toByteArray(response.getEntity());
		} catch (ClientProtocolException e) {
			throw e;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
