package com.blackbooks.test.services;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.services.BookServices;
import com.blackbooks.test.data.Books;
import com.blackbooks.test.data.Categories;

import junit.framework.Assert;

/**
 * Test class of service {@link com.blackbooks.services.BookServices#getBookCountByCategory(android.database.sqlite.SQLiteDatabase, Long)}.
 */
public class GetBookCountByCategoryTest extends AbstractDatabaseTest {

    /**
     * Make sure the method returns the expected value.
     */
    public void testGetBookCountByCategory() {

        Category category = new Category();
        category.name = Categories.ADVENTURE;

        BookInfo bookInfo1 = new BookInfo();
        bookInfo1.title = Books.LA_MAGICIENNE_TRAHIE;
        bookInfo1.categories.add(category);
        BookServices.saveBookInfo(getDb(), bookInfo1);

        BookInfo bookInfo2 = new BookInfo();
        bookInfo2.title = Books.CASINO_ROYALE;
        bookInfo2.categories.add(category);
        BookServices.saveBookInfo(getDb(), bookInfo2);

        int bookCount = BookServices.getBookCountByCategory(getDb(), category.id);

        Assert.assertEquals(2, bookCount);
    }
}
