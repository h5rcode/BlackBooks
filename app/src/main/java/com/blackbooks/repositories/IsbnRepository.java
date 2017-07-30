package com.blackbooks.repositories;

import com.blackbooks.model.persistent.Isbn;

import java.util.List;

public interface IsbnRepository {
    void deleteAllLookedUpIsbns();

    void markIsbnLookedUp(long isbnId, Long bookId);

    int getIsbnListToLookUpCount();

    List<Isbn> getIsbnListToLookUp(int limit, int offset);

    int getIsbnListLookedUpCount();

    List<Isbn> getIsbnListLookedUp(int limit, int offset);

    void deleteAllPendingIsbns();
}
