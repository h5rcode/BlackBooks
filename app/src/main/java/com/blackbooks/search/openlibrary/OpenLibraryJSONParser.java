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
	public final static String PUBLISHERS = "publishers";
	public final static String PUBLISHER_NAME = "name";
	public final static String IDENTIFIERS = "identifiers";
	public final static String ISBN_10 = "isbn_10";
	public final static String ISBN_13 = "isbn_13";
	public final static String NUMBER_OF_PAGES = "number_of_pages";
	public final static String COVER = "cover";
	public final static String COVER_SMALL = "small";
	public final static String COVER_MEDIUM = "medium";
	public final static String COVER_LARGE = "large";
	public final static String SUBJECTS = "subjects";
	public final static String SUBJECT_NAME = "name";
	public final static String PUBLISH_DATE = "publish_date";
	public final static String AUTHORS = "authors";
	public final static String AUTHOR_NAME = "name";

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
			if (result.has(PUBLISHERS)) {
				JSONArray publishers = result.getJSONArray(PUBLISHERS);

				for (int i = 0; i < publishers.length(); i++) {
					JSONObject publisher = publishers.getJSONObject(i);
					if (publisher.has(PUBLISHER_NAME)) {
						book.publishers.add(publisher.getString(PUBLISHER_NAME));
					}
				}
			}
			if (result.has(IDENTIFIERS)) {
				JSONObject identifiers = result.getJSONObject(IDENTIFIERS);
				if (identifiers.has(ISBN_10)) {
					JSONArray isbn10 = identifiers.getJSONArray(ISBN_10);
					if (isbn10.length() > 0) {
						book.isbn10 = isbn10.getString(0);
					}
				}
				if (identifiers.has(ISBN_13)) {
					JSONArray isbn13 = identifiers.getJSONArray(ISBN_13);
					if (isbn13.length() > 0) {
						book.isbn13 = isbn13.getString(0);
					}
				}
			}
			if (result.has(NUMBER_OF_PAGES)) {
				book.numberOfPages = result.getLong(NUMBER_OF_PAGES);
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
			if (result.has(SUBJECTS)) {
				JSONArray subjects = result.getJSONArray(SUBJECTS);
				for (int i = 0; i < subjects.length(); i++) {
					JSONObject subject = subjects.getJSONObject(i);
					if (subject.has(SUBJECT_NAME)) {
						book.subjects.add(subject.getString(SUBJECT_NAME));
					}
				}
			}
			if (result.has(PUBLISH_DATE)) {
				book.publishDate = result.getString(PUBLISH_DATE);
			}
			if (result.has(AUTHORS)) {
				JSONArray authors = result.getJSONArray(AUTHORS);
				for (int i = 0; i < authors.length(); i++) {
					JSONObject author = authors.getJSONObject(i);
					if (author.has(AUTHOR_NAME)) {
						book.authors.add(author.getString(AUTHOR_NAME));
					}
				}
			}
		}
		return book;
	}
}
