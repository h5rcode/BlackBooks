package com.blackbooks.services.search.openlibrary;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.blackbooks.services.search.BookOnlineSearchResult;
import com.blackbooks.services.search.BookSearcher;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Class that searches books using the OpenLibrary API.
 */
public final class OpenLibrarySearcher implements BookSearcher {

    private static final String URI_FORMAT_STRING = "https://openlibrary.org/api/books?bibkeys=ISBN:%s&jscmd=data&format=json";

    private final RequestQueue _requestQueue;

    public OpenLibrarySearcher(RequestQueue requestQueue) {
        _requestQueue = requestQueue;
    }

    /**
     * Search book info.
     *
     * @param isbn ISBN code.
     * @return OpenLibraryBook.
     */
    public BookOnlineSearchResult search(String isbn) {
        String url = String.format(URI_FORMAT_STRING, isbn);
        RequestFuture<JSONObject> future = RequestFuture.newFuture();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                future,
                future
        );
        _requestQueue.add(jsonObjectRequest);

        try {
            JSONObject jsonObject = future.get();
            return OpenLibraryJSONParser.parse(jsonObject);
        } catch (InterruptedException | ExecutionException | JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
