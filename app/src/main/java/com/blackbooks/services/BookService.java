package com.blackbooks.services;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Book;

import java.util.List;

public interface BookService {
    Book getBook(long bookId);

    BookInfo getBookInfo(long bookId);

    void saveBookInfo(BookInfo bookInfo);

    void deleteBook(long id);

    void returnBook(long id);

    List<Book> getBookListByIsbn(String isbn);

    byte[] getBookSmallThumbnail(long bookId);

    void markBookAsFavourite(long id);

    void markBookAsRead(long id);

    int getBookCountByAuthor(long authorId);

    List<BookInfo> getBookInfoListByAuthor(long authorId, int limit, int offset);

    int getBookCountByBookLocation(Long bookLocationId);

    List<BookInfo> getBookInfoListByBookLocation(long bookLocationId, int limit, int offset);

    int getBookCountByCategory(Long categoryId);

    List<BookInfo> getBookInfoListByCategory(long categoryId, int limit, int offset);

    int getBookCountByFirstLetter(String firstLetter);

    List<BookInfo> getBookInfoListByFirstLetter(String firstLetter, int limit, int offset);

    int getBookCountByLanguage(String languageCode);

    List<BookInfo> getBookInfoListByLanguage(String languageCode, int limit, int offset);

    int getBookCountByLoanedTo(String loanedTo);

    List<BookInfo> getBookInfoListByLoanedTo(String loanedTo, int limit, int offset);

    int getBookCountBySeries(Long seriesId);

    List<BookInfo> getBookInfoListBySeries(long seriesId, int limit, int offset);

    List<BookInfo> getBookInfoListFavourite(int limit, int offset);

    List<BookInfo> getBookInfoListToRead(int limit, int offset);

    List<BookInfo> getBookInfoListFromBookList(List<Book> bookList);
}
