package com.blackbooks.repositories;

import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.persistent.BookCategory;
import com.blackbooks.sql.BrokerManager;

import java.util.List;

/**
 * Services related to the BookCategory class.
 */
public class BookCategoryRepositoryImpl extends AbstractRepository implements BookCategoryRepository {
    public BookCategoryRepositoryImpl(SQLiteHelper sqLiteHelper) {
        super(sqLiteHelper);
    }

    /**
     * Delete all the BookCategory relationships involving a given book.
     *
     * @param bookId Id of a book.
     */
    public void deleteBookCategoryListByBook(long bookId) {
        BookCategory bookCategory = new BookCategory();
        bookCategory.bookId = bookId;
        BrokerManager.getBroker(BookCategory.class).deleteAllByCriteria(getWritableDatabase(), bookCategory);
    }

    /**
     * Get the list of BookCategory referencing a book.
     *
     * @param bookId Id of a book.
     * @return List of BookCategory.
     */
    public List<BookCategory> getBookCategoryListByBook(long bookId) {
        BookCategory bookCategory = new BookCategory();
        bookCategory.bookId = bookId;
        return BrokerManager.getBroker(BookCategory.class).getAllByCriteria(getReadableDatabase(), bookCategory);
    }

    /**
     * Get the list of BookCategory referencing a category.
     *
     * @param categoryId Id of a category.
     * @return List of BookCategory.
     */
    public List<BookCategory> getBookCategoryListByCategory(long categoryId) {
        BookCategory bookCategory = new BookCategory();
        bookCategory.categoryId = categoryId;
        return BrokerManager.getBroker(BookCategory.class).getAllByCriteria(getReadableDatabase(), bookCategory);
    }

    /**
     * Save a BookCategory.
     *
     * @param bookCategory BookCategory.
     * @return Id of the saved BookCategory
     */
    public long saveBookCategory(BookCategory bookCategory) {
        return BrokerManager.getBroker(BookCategory.class).save(getWritableDatabase(), bookCategory);
    }
}
