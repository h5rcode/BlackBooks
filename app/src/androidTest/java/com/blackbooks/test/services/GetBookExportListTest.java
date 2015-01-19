package com.blackbooks.test.services;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import com.blackbooks.model.nonpersistent.BookExport;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.services.BookServices;
import com.blackbooks.services.ExportServices;
import com.blackbooks.test.data.Authors;
import com.blackbooks.test.data.BookLocations;
import com.blackbooks.test.data.Books;
import com.blackbooks.test.data.Categories;
import com.blackbooks.test.data.Languages;
import com.blackbooks.test.data.Publishers;
import com.blackbooks.test.data.Seriez;
import com.blackbooks.utils.DateUtils;

/**
 * Test class of {@link ExportServices#getBookExportList()} .
 */
public class GetBookExportListTest extends AbstractDatabaseTest {

	/**
	 * Test of getBookExportList.
	 */
	public void testGetBookExportList() {
		Author author1 = new Author();
		author1.name = Authors.GRZEGORZ_ROSINSKI;

		Author author2 = new Author();
		author2.name = Authors.JEAN_VAN_HAMME;

		Category category1 = new Category();
		category1.name = Categories.BELGIAN_COMICS;

		Category category2 = new Category();
		category2.name = Categories.ADVENTURE;

		BookInfo bookInfo = new BookInfo();
		bookInfo.title = Books.LA_MAGICIENNE_TRAHIE;
		bookInfo.subtitle = Books.LA_MAGICIENNE_TRAHIE;
		bookInfo.authors.add(author1);
		bookInfo.authors.add(author2);
		bookInfo.categories.add(category1);
		bookInfo.categories.add(category2);
		bookInfo.series.name = Seriez.THORGAL;
		bookInfo.number = 1L;
		bookInfo.publisher.name = Publishers.LE_LOMBARD;
		bookInfo.publishedDate = new Date();
		bookInfo.pageCount = 48L;
		bookInfo.languageCode = Languages.FRENCH;
		bookInfo.description = "Thorgal Aegirsson est condamne a mort par Gandalf-le-fou, roi des Vikings.\n";
		bookInfo.isbn10 = "2803603586";
		bookInfo.isbn13 = "9782803603589";
		bookInfo.isRead = 1L;
		bookInfo.isFavourite = 0L;
		bookInfo.bookLocation.name = BookLocations.LIVING_ROOM;

		BookServices.saveBookInfo(getDb(), bookInfo);

		List<BookExport> bookExportList = ExportServices.getBookExportList(getDb(), null);

		Assert.assertEquals(1, bookExportList.size());
		BookExport bookExport = bookExportList.get(0);

		Assert.assertNotNull(bookExport.id);
		Assert.assertNotNull(bookExport.title);
		Assert.assertNotNull(bookExport.subtitle);
		Assert.assertNotNull(bookExport.authors);
		Assert.assertNotNull(bookExport.categories);
		Assert.assertNotNull(bookExport.series);
		Assert.assertNotNull(bookExport.number);
		Assert.assertNotNull(bookExport.pageCount);
		Assert.assertNotNull(bookExport.languageCode);
		Assert.assertNotNull(bookExport.description);
		Assert.assertNotNull(bookExport.isbn10);
		Assert.assertNotNull(bookExport.isbn13);

		Assert.assertEquals(bookInfo.id.longValue(), bookExport.id.longValue());
		Assert.assertEquals(bookInfo.title, bookExport.title);
		Assert.assertEquals(bookInfo.subtitle, bookExport.subtitle);
		Assert.assertEquals(author1.name + "," + author2.name, bookExport.authors);
		Assert.assertEquals(category1.name + "," + category2.name, bookExport.categories);
		Assert.assertEquals(bookInfo.series.name, bookExport.series);
		Assert.assertEquals(bookInfo.number.longValue(), bookExport.number.longValue());
		Assert.assertEquals(bookInfo.pageCount.longValue(), bookExport.pageCount.longValue());
		Assert.assertEquals(bookInfo.languageCode, bookExport.languageCode);
		Assert.assertTrue(bookInfo.description.contains("\n"));
		Assert.assertEquals(bookInfo.description.replace("\n", ""), bookExport.description);
		Assert.assertEquals(bookInfo.publisher.name, bookExport.publisher);
		Assert.assertEquals(DateUtils.DEFAULT_DATE_FORMAT.format(bookInfo.publishedDate), bookExport.publishedDate);
		Assert.assertEquals(bookInfo.isbn10, bookExport.isbn10);
		Assert.assertEquals(bookInfo.isbn13, bookExport.isbn13);

	}
}
