package com.blackbooks.search.openlibrary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Utility class to parse JSON results returned by the OpenLibrary API.
 * 
 * https://openlibrary.org/dev/docs/api/books
 */
public final class OpenLibraryJSONParser {

	public final static String TITLE = "title";
	public final static String SUBTITLE = "subtitle";
	public final static String AUTHORS = "authors";
	public final static String AUTHOR_NAME = "name";
	public final static String PUBLISHERS = "publishers";
	public final static String PUBLISH_DATE = "publish_date";
	public final static String COVER = "cover";
	public final static String COVER_SMALL = "small";
	public final static String COVER_MEDIUM = "medium";
	public final static String COVER_LARGE = "large";
	public final static String NUMBER_OF_PAGES = "number_of_pages";

	/**
	 * Parse the JSON data returned by the OpenLibrary API and return an
	 * instance of OpenLibraryBook.
	 * 
	 * @param json
	 *            The JSON data to parse.
	 * @return OpenLibraryBook.
	 * @throws JSONException
	 */
	public static OpenLibraryBook parse(String json) throws JSONException {
		OpenLibraryBook book = null;

		JSONObject result = new JSONObject(json);
		JSONArray names = result.names();
		if (names != null && names.length() > 0) {
			result = result.getJSONObject(names.getString(0));
			book = new OpenLibraryBook();
			if (result.has(TITLE)) {
				book.title = result.getString(TITLE);
			}
			if (result.has(SUBTITLE)) {
				book.subtitle = result.getString(SUBTITLE);
			}
			if (result.has(AUTHORS)) {
				JSONArray authors = result.getJSONArray(AUTHORS);
				for (int i = 0; i < authors.length(); i++) {
					JSONObject author = authors.getJSONObject(i);
					book.authors.add(author.getString(AUTHOR_NAME));
				}
			}
			if (result.has(PUBLISH_DATE)) {
				book.publishDate = result.getString(PUBLISH_DATE);
			}
			if (result.has(COVER)) {
				JSONObject cover = result.getJSONObject(COVER);
				if (cover.has(COVER_SMALL)) {
					book.coverLinkSmall = cover.getString(COVER_SMALL);
				}
				if (cover.has(COVER_MEDIUM)) {
					book.coverLinkMedium = cover.getString(COVER_MEDIUM);
				}
				if (cover.has(COVER_LARGE)) {
					book.coverLinkLarge = cover.getString(COVER_LARGE);
				}
			}
			if (result.has(NUMBER_OF_PAGES)) {
				book.numberOfPages = result.getLong(NUMBER_OF_PAGES);
			}
		}
		return book;
	}
}
