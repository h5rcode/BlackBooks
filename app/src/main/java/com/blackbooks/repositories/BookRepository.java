package com.blackbooks.repositories;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Book;

import java.util.List;

public interface BookRepository {

    Book getBook(long bookId);

    void save(BookInfo bookInfo);

    void deleteBook(long bookId);

    List<Book> getBookListBySeries(long seriesId, int limit, int offset);

    List<Book> getBookInfoListFavourite(int limit, int offset);

    List<Book> getBookInfoListToRead(int limit, int offset);

    int getBookCountByAuthor(long authorId);

    int getBookCountByBookLocation(Long bookLocationId);

    int getBookCountByCategory(Long categoryId);

    int getBookCountByFirstLetter(String firstLetter);

    int getBookCountByLanguage(String languageCode);

    int getBookCountByLoanedTo(String loanedTo);

    int getBookCountBySeries(Long seriesId);

    int getBookCount();

    int getBookToReadCount();

    int getBookLoanCount();

    int getFavouriteBooks();

    int getLanguageCount();

    int getFirstLetterCount();

    List<Book> getBooksByLoanedTo(String loanedTo, int limit, int offset);

    List<Book> getBooksByLanguage(String languageCode, int limit, int offset);

    byte[] getBookSmallThumbnail(long bookId);

    void markBookAsFavourite(long bookId);

    void markBookAsRead(long bookId);

    void returnBook(long bookId);

    List<Book> getBooksByFirstLetter(String firstLetter, int limit, int offset);

    List<Book> getBooksByCategory(long categoryId, int limit, int offset);

    List<Book> getBooksByBookLocation(long bookLocationId, int limit, int offset);

    List<Book> getBooksByAuthor(long authorId, int limit, int offset);

    List<Book> getBooksByIsbn10(String isbn);

    List<Book> getBooksByIsbn13(String isbn);
}
