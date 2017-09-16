package com.blackbooks.services.search;

import com.blackbooks.model.nonpersistent.BookInfo;

/**
 * The result of a book search.
 */
public interface BookOnlineSearchResult {

    /**
     * Return the source of this result.
     *
     * @return {@link BookSearchResultSource}.
     */
    BookSearchResultSource getResultSource();

    /**
     * Transform the current instance into a new instance of {@link BookInfo}.
     *
     * @return {@link BookInfo}.
     */
    BookInfo toBookInfo();
}
