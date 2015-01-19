package com.blackbooks.test.services;

import java.util.List;

import junit.framework.Assert;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.model.persistent.Publisher;
import com.blackbooks.model.persistent.Series;
import com.blackbooks.services.AuthorServices;
import com.blackbooks.services.BookServices;
import com.blackbooks.services.CategoryServices;
import com.blackbooks.services.FullTextSearchServices;
import com.blackbooks.services.PublisherServices;
import com.blackbooks.services.SeriesServices;
import com.blackbooks.test.data.Authors;
import com.blackbooks.test.data.Books;
import com.blackbooks.test.data.Categories;
import com.blackbooks.test.data.Publishers;
import com.blackbooks.test.data.Seriez;

/**
 * Test class of the service {@link BookServices#deleteBook(android.database.sqlite.SQLiteDatabase, long)} ()}.
 * 
 */
public class DeleteBookTest extends AbstractDatabaseTest {

	/**
	 * Test the deletion of a book that only has a title.
	 */
	public void testDeleteBookBasic() {
		BookInfo bookInfo = new BookInfo();
		bookInfo.title = Books.ASTERIX_LE_GAULOIS;

		BookServices.saveBookInfo(getDb(), bookInfo);
		BookServices.deleteBook(getDb(), bookInfo.id);

		Book bookDb = BookServices.getBook(getDb(), bookInfo.id);
		Assert.assertNull(bookDb);
	}

	/**
	 * Verify that a deleted book is no longer present in the corresponding
	 * Full-Text-Search table (FTS).
	 */
	public void testDeleteBookFullTextSearch() {
		BookInfo bookInfo = new BookInfo();
		bookInfo.title = Books.THE_CATCHER_IN_THE_RYE;

		BookServices.saveBookInfo(getDb(), bookInfo);
		BookServices.deleteBook(getDb(), bookInfo.id);

		List<BookInfo> bookInfoList = FullTextSearchServices.searchBooks(getDb(), bookInfo.title);

		Assert.assertEquals(0, bookInfoList.size());
	}

	/**
	 * Test the deletion of a book having authors. The authors must also be
	 * deleted because there is no other books referring to them.
	 */
	public void testDeleteBookWithAuthors() {
		BookInfo bookInfo = new BookInfo();
		bookInfo.title = Books.ASTERIX_LE_GAULOIS;

		Author author1 = new Author();
		author1.name = Authors.RENE_GOSCINNY;

		Author author2 = new Author();
		author2.name = Authors.ALBERT_UDERZO;

		bookInfo.authors.add(author1);
		bookInfo.authors.add(author2);

		BookServices.saveBookInfo(getDb(), bookInfo);
		BookServices.deleteBook(getDb(), bookInfo.id);

		Author author1Db = AuthorServices.getAuthor(getDb(), author1.id);
		Author author2Db = AuthorServices.getAuthor(getDb(), author2.id);

		Assert.assertNull(author1Db);
		Assert.assertNull(author2Db);
	}

	/**
	 * Test the deletion of a book having categories. The categories must also
	 * be deleted because there is no other books referring to them.
	 */
	public void testDeleteBookWithCategories() {
		BookInfo bookInfo = new BookInfo();
		bookInfo.title = Books.ASTERIX_LE_GAULOIS;

		Category category1 = new Category();
		category1.name = Categories.FRENCH_COMICS;

		Category category2 = new Category();
		category2.name = Categories.HUMOR;

		bookInfo.categories.add(category1);
		bookInfo.categories.add(category2);

		BookServices.saveBookInfo(getDb(), bookInfo);
		BookServices.deleteBook(getDb(), bookInfo.id);

		Category category1Db = CategoryServices.getCategory(getDb(), category1.id);
		Category category2Db = CategoryServices.getCategory(getDb(), category2.id);

		Assert.assertNull(category1Db);
		Assert.assertNull(category2Db);
	}

	/**
	 * Test the deletion of a book having a publisher. The publisher must also
	 * be deleted because there is no other books referring to it.
	 */
	public void testDeleteBookWithPublisher() {
		BookInfo bookInfo = new BookInfo();
		bookInfo.title = Books.LE_MYTHE_DE_SISYPHE;
		bookInfo.publisher.name = Publishers.GALLIMARD;

		BookServices.saveBookInfo(getDb(), bookInfo);
		BookServices.deleteBook(getDb(), bookInfo.id);

		Publisher publisherDb = PublisherServices.getPublisher(getDb(), bookInfo.publisherId);

		Assert.assertNull(publisherDb);
	}

	/**
	 * Test the deletion of a book having a series. The series must also be
	 * deleted because there is no other books referring to it.
	 */
	public void testDeleteBookWithSeries() {
		BookInfo bookInfo = new BookInfo();
		bookInfo.title = Books.ASTERIX_LE_GAULOIS;
		bookInfo.series.name = Seriez.ASTERIX;

		BookServices.saveBookInfo(getDb(), bookInfo);
		BookServices.deleteBook(getDb(), bookInfo.id);

		Series series = SeriesServices.getSeries(getDb(), bookInfo.seriesId);

		Assert.assertNull(series);
	}
}
