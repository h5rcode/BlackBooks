package com.blackbooks.services.search.google;

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
 * Class that searches books using the Google books API.
 */
public final class GoogleBooksSearcher implements BookSearcher {

    private static final String URI_FORMAT_STRING = "https://www.googleapis.com/books/v1/volumes?q=isbn:%s";
    private final RequestQueue _requestQueue;

    public GoogleBooksSearcher(RequestQueue requestQueue) {
        _requestQueue = requestQueue;
    }

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
            return new GoogleBooksJSONParser().parse(jsonObject);
        } catch (ExecutionException | JSONException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
