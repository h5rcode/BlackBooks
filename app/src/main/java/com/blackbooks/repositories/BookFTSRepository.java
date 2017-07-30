package com.blackbooks.repositories;

import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.fts.BookFTS;

import java.util.List;

public interface BookFTSRepository {
    int getSearchResultCount(String query);

    List<Book> searchBooks(String query, int limit, int offset);

    void deleteBook(long bookId);

    void insert(BookFTS bookFts);

    void update(BookFTS bookFts);
}
