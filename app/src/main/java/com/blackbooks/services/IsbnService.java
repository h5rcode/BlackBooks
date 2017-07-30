package com.blackbooks.services;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Isbn;

import java.util.List;

public interface IsbnService {

    void deleteAllPendingIsbns();

    List<Isbn> getIsbnListToLookUp(int limit, int offset);

    int getIsbnListToLookUpCount();

    void saveIsbn(String number);

    void saveBookInfo(BookInfo bookInfo, long isbnId);

    void markIsbnLookedUp(long isbnId, Long bookId);
}
