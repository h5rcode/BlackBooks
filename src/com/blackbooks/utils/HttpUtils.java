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
 * Http utility class.
 */
public final class HttpUtils {

	/**
	 * Private constructor.
	 */
	private HttpUtils() {
	}

	/**
	 * Perform a request and return a String representing a JSON object.
	 * 
	 * @param url
	 *            URL.
	 * @return String.
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static String getJson(String url) throws URISyntaxException, IOException {
		URI uri = new URI(url);
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet get = new HttpGet(uri);
		HttpResponse response = httpclient.execute(get);
		InputStream content = response.getEntity().getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(content));
		StringBuilder sb = new StringBuilder();
		String line = null;
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
	 * @throws IOException 
	 */
	public static byte[] getBinary(String url) throws URISyntaxException, IOException {
		URI uri = new URI(url);
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet get = new HttpGet(uri);
		HttpResponse response = httpclient.execute(get);
		return EntityUtils.toByteArray(response.getEntity());
	}
}
