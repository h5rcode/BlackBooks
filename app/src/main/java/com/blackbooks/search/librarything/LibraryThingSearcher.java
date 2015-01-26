package com.blackbooks.search.librarything;

import com.blackbooks.utils.HttpUtils;

import java.io.IOException;
import java.net.URISyntaxException;

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
     * @param isbn ISBN.
     * @return Cover.
     * @throws URISyntaxException
     * @throws IOException
     */
    public static byte[] getSmallCover(String isbn) throws URISyntaxException, IOException {
        return getCover(isbn, SIZE_SMALL);
    }

    /**
     * Get the medium-size cover of a book.
     *
     * @param isbn ISBN.
     * @return Cover.
     * @throws URISyntaxException
     * @throws IOException
     */
    public static byte[] getMediumCover(String isbn) throws URISyntaxException, IOException {
        return getCover(isbn, SIZE_MEDIUM);
    }

    /**
     * Get the large cover of a book.
     *
     * @param isbn ISBN.
     * @return Cover.
     * @throws URISyntaxException
     * @throws IOException
     */
    public static byte[] getLargeCover(String isbn) throws URISyntaxException, IOException {
        return getCover(isbn, SIZE_LARGE);
    }

    /**
     * Get the cover of a book.
     *
     * @param isbn ISBN.
     * @param size Size (small/medium/large).
     * @return Cover.
     * @throws URISyntaxException
     * @throws IOException
     */
    private static byte[] getCover(String isbn, String size) throws URISyntaxException, IOException {
        String url = String.format(URI_FORMAT_STRING, DEVELOPER_KEY, size, isbn);
        return HttpUtils.getBinary(url);
    }
}
