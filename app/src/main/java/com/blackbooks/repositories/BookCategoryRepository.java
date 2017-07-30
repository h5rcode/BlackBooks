package com.blackbooks.repositories;

import com.blackbooks.model.persistent.BookCategory;

import java.util.List;

public interface BookCategoryRepository {

    void deleteBookCategoryListByBook(long bookId);

    List<BookCategory> getBookCategoryListByBook(long bookId);

    List<BookCategory> getBookCategoryListByCategory(long categoryId);

    long saveBookCategory(BookCategory bookCategory);
}
