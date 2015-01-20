package com.blackbooks.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * HTTP utility class.
 */
public final class HttpUtils {

	/**
	 * Private constructor.
	 */
	private HttpUtils() {
	}

	/**
	 * Perform a HTTP request and return its result as a String.
	 * 
	 * @param url
	 *            URL.
	 * @return String.
	 * @throws URISyntaxException
	 *             If the specified URL is incorrect.
	 * @throws IOException
	 *             In case of a connection problem.
	 */
	public static String getText(String url) throws URISyntaxException, IOException {
		URI uri = new URI(url);
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet get = new HttpGet(uri);
		HttpResponse response = httpclient.execute(get);
		InputStream content = response.getEntity().getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(content));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}

	/***
	 * Perform a request and return an array of bytes.
	 * 
	 * @param url
	 *            URL.
	 * @return Byte array.
	 * @throws URISyntaxException
	 *             If the specified URL is incorrect.
	 * @throws IOException
	 *             In case of a connection problem.
	 */
	public static byte[] getBinary(String url) throws URISyntaxException, IOException {
		URI uri = new URI(url);
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet get = new HttpGet(uri);
		HttpResponse response = httpclient.execute(get);
		return EntityUtils.toByteArray(response.getEntity());
	}
}
