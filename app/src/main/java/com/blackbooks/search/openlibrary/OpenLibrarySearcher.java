package com.blackbooks.search.openlibrary;

import com.blackbooks.search.BookSearchResult;
import com.blackbooks.utils.HttpUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;

/**
 * Class that searches books using the OpenLibrary API.
 */
public final class OpenLibrarySearcher implements Callable<BookSearchResult> {

    private static final String URI_FORMAT_STRING = "https://openlibrary.org/api/books?bibkeys=ISBN:%s&jscmd=data&format=json";

    private String mIsbn;

    /**
     * Constructor.
     */
    public OpenLibrarySearcher(String isbn) {
        mIsbn = isbn;
    }

    /**
     * Search book info.
     *
     * @param isbn ISBN code.
     * @return OpenLibraryBook.
     * @throws URISyntaxException If an incorrect URI was built for the search.
     * @throws JSONException      If something bad happened during the parsing of the result.
     * @throws IOException        If a connection problem occurred.
     */
    private static OpenLibraryBook search(String isbn) throws URISyntaxException, JSONException, IOException {
        String url = String.format(URI_FORMAT_STRING, isbn);
        String json = HttpUtils.getText(url);
        return OpenLibraryJSONParser.parse(json);
    }

    @Override
    public BookSearchResult call() throws Exception {
        return search(mIsbn);
    }
}
