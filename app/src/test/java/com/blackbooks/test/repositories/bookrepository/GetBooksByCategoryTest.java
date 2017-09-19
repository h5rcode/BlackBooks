package com.blackbooks.test.repositories.bookrepository;

import com.blackbooks.BuildConfig;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.BookCategory;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.sql.Broker;
import com.blackbooks.sql.BrokerManager;
import com.blackbooks.test.data.Books;
import com.blackbooks.test.data.Categories;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class GetBooksByCategoryTest extends AbstractBookRepositoryTest {

    @Test
    public void getBooksByCategory_should_return_the_expected_results() {
        Category category = new Category();
        category.name = Categories.ADVENTURE;

        Book book1 = new Book();
        book1.title = Books.CASINO_ROYALE;

        Book book2 = new Book();
        book2.title = Books.LA_MAGICIENNE_TRAHIE;

        Broker<Book> bookBroker = BrokerManager.getBroker(Book.class);
        Broker<Category> categoryBroker = BrokerManager.getBroker(Category.class);
        Broker<BookCategory> bookCategoryBroker = BrokerManager.getBroker(BookCategory.class);

        bookBroker.save(db, book1);
        bookBroker.save(db, book2);
        categoryBroker.save(db, category);

        BookCategory bookCategory1 = new BookCategory();
        bookCategory1.categoryId = category.id;
        bookCategory1.bookId = book1.id;

        BookCategory bookCategory2 = new BookCategory();
        bookCategory2.categoryId = category.id;
        bookCategory2.bookId = book2.id;

        bookCategoryBroker.save(db, bookCategory1);
        bookCategoryBroker.save(db, bookCategory2);

        List<Book> books = bookRepository.getBooksByCategory(category.id, Integer.MAX_VALUE, 0);

        Assert.assertEquals(2, books.size());

        Book bookResult1 = books.get(0);
        Book bookResult2 = books.get(1);

        Assert.assertEquals(book1.id.longValue(), bookResult1.id.longValue());
        Assert.assertEquals(book2.id.longValue(), bookResult2.id.longValue());
    }
}
