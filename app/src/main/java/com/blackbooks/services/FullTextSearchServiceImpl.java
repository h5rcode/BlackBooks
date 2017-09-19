package com.blackbooks.services;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.repositories.BookFTSRepository;

import java.util.List;

/**
 * Full-Text-Search services.
 */
public final class FullTextSearchServiceImpl implements FullTextSearchService {

    private final BookFTSRepository bookFTSRepository;
    private final BookService bookService;

    public FullTextSearchServiceImpl(BookFTSRepository bookFTSRepository, BookService bookService) {
        this.bookFTSRepository = bookFTSRepository;
        this.bookService = bookService;
    }

    public int getSearchResultCount(String query) {
        return bookFTSRepository.getSearchResultCount(query);
    }

    public List<BookInfo> searchBooks(String query, int limit, int offset) {

        List<Book> bookList = bookFTSRepository.searchBooks(query, limit, offset);
        return bookService.getBookInfoListFromBookList(bookList);
    }

}
