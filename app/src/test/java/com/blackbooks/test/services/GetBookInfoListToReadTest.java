package com.blackbooks.test.services;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.test.data.Books;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetBookInfoListToReadTest extends AbstractBookServiceTest {

    @Test
    public void getBookInfoListToRead_should_return_the_expected_result() {
        Book book1 = new Book();
        book1.id = 253L;
        book1.title = Books.HARRY_POTTER_AND_THE_CHAMBER_OF_SECRETS;

        Book book2 = new Book();
        book2.id = 536L;
        book2.title = Books.HARRY_POTTER_AND_THE_PHILOSOPHER_S_STONE;

        int limit = Integer.MAX_VALUE;
        int offset = 0;

        List<Book> books = new ArrayList<>();
        books.add(book1);
        books.add(book2);

        when(bookRepository.getBookInfoListToRead(limit, offset)).thenReturn(books);

        List<BookInfo> bookInfoList = bookService.getBookInfoListToRead(limit, offset);

        Assert.assertEquals(2, bookInfoList.size());

        BookInfo bookInfoResult1 = bookInfoList.get(0);
        BookInfo bookInfoResult2 = bookInfoList.get(1);

        Assert.assertEquals(book1.id.longValue(), bookInfoResult1.id.longValue());
        Assert.assertEquals(book2.id.longValue(), bookInfoResult2.id.longValue());
    }
}
