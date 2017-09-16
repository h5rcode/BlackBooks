package com.blackbooks.services.search.google;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Utility class to parse JSON results returned by the Google Books API.
 * <p/>
 * https://developers.google.com/books/docs/v1/reference/volumes
 */
public final class GoogleBooksJSONParser {

    private static final String TOTAL_ITEMS = "totalItems";
    private static final String ITEMS = "items";
    private static final String VOLUME_INFO = "volumeInfo";
    private static final String TITLE = "title";
    private static final String SUBTITLE = "subtitle";
    private static final String AUTHORS = "authors";
    private static final String PUBLISHER = "publisher";
    private static final String PUBLISHED_DATE = "publishedDate";
    private static final String DESCRIPTION = "description";
    private static final String INDUSTRY_IDENTIFIERS = "industryIdentifiers";
    private static final String TYPE = "type";
    private static final String IDENTIFIER = "identifier";
    private static final String PAGE_COUNT = "pageCount";
    private static final String DIMENSIONS = "dimensions";
    private static final String HEIGHT = "height";
    private static final String WIDTH = "width";
    private static final String THICKNESS = "thickness";
    private static final String PRINT_TYPE = "printType";
    private static final String MAIN_CATEGORY = "mainCategory";
    private static final String CATEGORIES = "categories";
    private static final String IMAGE_LINKS = "imageLinks";
    private static final String SMALL_THUMBNAIL = "smallThumbnail";
    private static final String THUMBNAIL = "thumbnail";
    private static final String LANGUAGE = "language";

    private int totalItems;
    private JSONObject volumeInfo;
    private JSONArray authors;
    private JSONArray industryIdentifiers;
    private JSONObject dimensions;
    private JSONArray categories;
    private JSONObject imageLinks;

    /**
     * Constructor.
     */
    public GoogleBooksJSONParser() {
    }

    /**
     * Parse the JSON data returned by Google Books API and return an instance
     * of GoogleBook.
     *
     * @param json The JSON data to parse.
     * @return GoogleBook.
     * @throws JSONException
     */
    public GoogleBook parse(String json) throws JSONException {
        GoogleBook book = null;

        extractObjects(json);

        if (totalItems == 1) {
            book = new GoogleBook();

            if (volumeInfo.has(TITLE)) {
                book.title = volumeInfo.getString(TITLE);
            }

            if (volumeInfo.has(SUBTITLE)) {
                book.subtitle = volumeInfo.getString(SUBTITLE);
            }

            if (authors != null) {
                for (int i = 0; i < authors.length(); i++) {
                    book.authors.add(authors.getString(i));
                }
            }

            if (volumeInfo.has(PUBLISHER)) {
                book.publisher = volumeInfo.getString(PUBLISHER);
            }

            if (volumeInfo.has(PUBLISHED_DATE)) {
                book.publishedDate = volumeInfo.getString(PUBLISHED_DATE);
            }

            if (volumeInfo.has(DESCRIPTION)) {
                book.description = volumeInfo.getString(DESCRIPTION);
            }

            if (industryIdentifiers != null) {
                for (int i = 0; i < industryIdentifiers.length(); i++) {
                    JSONObject industryIdentifier = industryIdentifiers.getJSONObject(i);
                    GoogleIndustryIdentifier identifier = new GoogleIndustryIdentifier();
                    identifier.type = industryIdentifier.getString(TYPE);
                    identifier.identifier = industryIdentifier.getString(IDENTIFIER);
                    book.industryIdentifiers.add(identifier);
                }
            }

            if (volumeInfo.has(PAGE_COUNT)) {
                book.pageCount = volumeInfo.getLong(PAGE_COUNT);
            }

            if (dimensions != null) {
                if (dimensions.has(HEIGHT)) {
                    book.height = dimensions.getString(HEIGHT);
                }

                if (dimensions.has(WIDTH)) {
                    book.width = dimensions.getString(WIDTH);
                }

                if (dimensions.has(THICKNESS)) {
                    book.thickness = dimensions.getString(THICKNESS);
                }
            }

            if (volumeInfo.has(PRINT_TYPE)) {
                book.printType = volumeInfo.getString(PRINT_TYPE);
            }
            if (volumeInfo.has(MAIN_CATEGORY)) {
                book.mainCategory = volumeInfo.getString(MAIN_CATEGORY);
            }
            if (categories != null) {
                for (int i = 0; i < categories.length(); i++) {
                    book.categories.add(categories.getString(i));
                }
            }
            if (imageLinks != null) {
                if (imageLinks.has(SMALL_THUMBNAIL)) {
                    book.smallThumbnailLink = imageLinks.getString(SMALL_THUMBNAIL);
                }
                if (imageLinks.has(THUMBNAIL)) {
                    book.thumbnailLink = imageLinks.getString(THUMBNAIL);
                }
            }
            if (volumeInfo.has(LANGUAGE)) {
                book.language = volumeInfo.getString(LANGUAGE);
            }
        }
        return book;
    }

    /**
     * Extract the different items of the main JSON object.
     *
     * @param json The JSON data to extract the items from.
     * @throws JSONException If a requested data does not exist in the JSON object.
     */
    private void extractObjects(String json) throws JSONException {
        JSONObject result = new JSONObject(json);
        if (result.has(TOTAL_ITEMS)) {
            totalItems = result.getInt(TOTAL_ITEMS);
        }
        if (totalItems == 1) {
            JSONArray items = result.getJSONArray(ITEMS);
            JSONObject item = items.getJSONObject(0);
            volumeInfo = item.getJSONObject(VOLUME_INFO);
            if (volumeInfo.has(AUTHORS)) {
                authors = volumeInfo.getJSONArray(AUTHORS);
            }
            if (volumeInfo.has(INDUSTRY_IDENTIFIERS)) {
                industryIdentifiers = volumeInfo.getJSONArray(INDUSTRY_IDENTIFIERS);
            }
            if (volumeInfo.has(DIMENSIONS)) {
                dimensions = volumeInfo.getJSONObject(DIMENSIONS);
            }
            if (volumeInfo.has(CATEGORIES)) {
                categories = volumeInfo.getJSONArray(CATEGORIES);
            }
            if (volumeInfo.has(IMAGE_LINKS)) {
                imageLinks = volumeInfo.getJSONObject(IMAGE_LINKS);
            }
        }
    }
}
