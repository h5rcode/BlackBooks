package com.blackbooks.services;

import com.blackbooks.model.nonpersistent.BookInfo;

import java.util.List;

public interface FullTextSearchService {
    List<BookInfo> searchBooks(String query, int limit, int offset);

    int getSearchResultCount(String query);
}
