package com.blackbooks.services.search;

import android.util.Log;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A class that searches for books using ISBN numbers.
 */
public class BookOnlineSearchServiceImpl implements BookOnlineSearchService {

    private final List<BookSearcher> _bookSearchers;

    public BookOnlineSearchServiceImpl(List<BookSearcher> bookSearchers) {
        _bookSearchers = bookSearchers;
    }

    /**
     * Search information about the book corresponding to the given ISBN. This
     * method starts asynchronous tasks to search different sources of
     * information in parallel (Amazon, Google books, Open library, etc.).
     *
     * @param isbn ISBN.
     * @return An instance of {@link BookInfo} if information were found, null
     * otherwise.
     */
    public BookInfo search(String isbn) {

        List<BookOnlineSearchResult> bookOnlineSearchResultList = new ArrayList<>();

        for (BookSearcher bookSearcher : _bookSearchers) {
            try {
                BookOnlineSearchResult bookOnlineSearchResult = bookSearcher.search(isbn);
                bookOnlineSearchResultList.add(bookOnlineSearchResult);
            } catch (Exception e) {
                Log.w(LogUtils.TAG, "A book search encountered an error.", e);
            }
        }

        return mergeSearchResults(bookOnlineSearchResultList);
    }

    /**
     * Merge the different results of a book search into a single instance of
     * {@link BookInfo}.
     *
     * @param bookOnlineSearchResultList List of {@link BookOnlineSearchResult}.
     * @return Null if the input list was empty, an instance of {@link BookInfo}
     * otherwise.
     */
    private static BookInfo mergeSearchResults(List<BookOnlineSearchResult> bookOnlineSearchResultList) {
        BookInfo bookInfo = null;
        Map<Integer, BookOnlineSearchResult> resultMap = new TreeMap<>();
        for (BookOnlineSearchResult bookOnlineSearchResult : bookOnlineSearchResultList) {
            resultMap.put(bookOnlineSearchResult.getResultSource().getMergeOrder(), bookOnlineSearchResult);
        }

        boolean isFirst = true;
        for (BookOnlineSearchResult bookOnlineSearchResult : resultMap.values()) {
            if (isFirst) {
                isFirst = false;
                bookInfo = new BookInfo();
            }

            BookInfo resultBookInfo = bookOnlineSearchResult.toBookInfo();
            bookInfo.merge(resultBookInfo);
        }

        return bookInfo;
    }
}
