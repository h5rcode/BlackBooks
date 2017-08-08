package com.blackbooks.test.repositories.bookrepository;

import com.blackbooks.BuildConfig;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.BookCategory;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.sql.Broker;
import com.blackbooks.sql.BrokerManager;
import com.blackbooks.test.data.Books;
import com.blackbooks.test.data.Categories;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class GetBookCountByCategoryTest extends AbstractBookRepositoryTest {

    @Test
    public void getBookCountByCategory_should_return_the_expected_value() {

        Category category = new Category();
        category.name = Categories.ADVENTURE;

        Book book1 = new Book();
        book1.title = Books.LE_MYTHE_DE_SISYPHE;

        Book book2 = new Book();
        book2.title = Books.LA_PESTE;

        Broker<Category> categoryBroker = BrokerManager.getBroker(Category.class);
        Broker<Book> bookBroker = BrokerManager.getBroker(Book.class);
        Broker<BookCategory> bookCategoryBroker = BrokerManager.getBroker(BookCategory.class);

        categoryBroker.save(db, category);
        bookBroker.save(db, book1);
        bookBroker.save(db, book2);

        BookCategory bookCategory1 = new BookCategory();
        bookCategory1.categoryId = category.id;
        bookCategory1.bookId = book1.id;

        BookCategory bookCategory2 = new BookCategory();
        bookCategory2.categoryId = category.id;
        bookCategory2.bookId = book2.id;

        bookCategoryBroker.save(db, bookCategory1);
        bookCategoryBroker.save(db, bookCategory2);

        int bookCount = bookRepository.getBookCountByCategory(category.id);

        assertEquals(2, bookCount);
    }
}
