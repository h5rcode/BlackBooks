package com.blackbooks.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

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
	 * @return BookInfo.
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
		String url = String.format(URI_FORMAT_STRING, isbn);
		URI uri = new URI(url);
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet get = new HttpGet(uri);

		try {
			HttpResponse response = httpclient.execute(get);
			InputStream content = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(content));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			String json = sb.toString();

			GoogleBook googleBook = new GoogleBooksJSONParser().parse(json);
			if (googleBook != null) {
				if (googleBook.thumbnailLink != null) {
					uri = new URI(googleBook.thumbnailLink);
					get = new HttpGet(uri);
					response = httpclient.execute(get);
					googleBook.thumbnail = EntityUtils.toByteArray(response.getEntity());
				}
				if (googleBook.smallThumbnailLink != null) {
					uri = new URI(googleBook.smallThumbnailLink);
					get = new HttpGet(uri);
					response = httpclient.execute(get);
					googleBook.smallThumbnail = EntityUtils.toByteArray(response.getEntity());
				}
			}

			return googleBook;
		} catch (ClientProtocolException e) {
			throw e;
		} catch (UnknownHostException e) {
			throw e;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
