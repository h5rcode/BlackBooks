package com.blackbooks.repositories;

import com.blackbooks.model.persistent.BookAuthor;

import java.util.List;

public interface BookAuthorRepository {

    void deleteBookAuthorListByBook(long bookId);

    List<BookAuthor> getBookAuthorListByAuthor(long authorId);

    List<BookAuthor> getBookAuthorListByBook(long bookId);

    long saveBookAuthor(BookAuthor bookAuthor);

    List<BookAuthor> getBookAuthorListByBooks(List<Long> bookIdList);
}
