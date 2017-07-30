package com.blackbooks.utils;

import java.io.IOException;
import java.net.URISyntaxException;

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
     * @param url URL.
     * @return String.
     * @throws URISyntaxException If the specified URL is incorrect.
     * @throws IOException        In case of a connection problem.
     */
    public static String getText(String url) throws URISyntaxException, IOException {
        return null;
    }

    /**
     * Perform a request and return an array of bytes.
     *
     * @param url URL.
     * @return Byte array.
     * @throws URISyntaxException If the specified URL is incorrect.
     * @throws IOException        In case of a connection problem.
     */
    public static byte[] getBinary(String url) throws URISyntaxException, IOException {
        return null;
    }
}
