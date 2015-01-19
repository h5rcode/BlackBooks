package com.blackbooks.test.services;

import java.util.List;

import junit.framework.Assert;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.services.BookServices;
import com.blackbooks.test.data.Authors;
import com.blackbooks.test.data.Books;

/**
 * Test of getBookInfoList.
 */
public class GetBookInfoListTest extends AbstractDatabaseTest {

	/**
	 * Test of testGetBookInfoList.
	 */
	public void testGetBookInfoList() {
		Author josephConrad = new Author();
		josephConrad.name = Authors.JOSEPH_CONRAD;

		Author sirJamesGeorgeFrazer = new Author();
		sirJamesGeorgeFrazer.name = Authors.SIR_JAMES_GEORGE_FRAZER;

		Author reneGoscinny = new Author();
		reneGoscinny.name = Authors.RENE_GOSCINNY;

		Author albertUderzo = new Author();
		albertUderzo.name = Authors.ALBERT_UDERZO;

		BookInfo nostromo = new BookInfo();
		nostromo.title = Books.NOSTROMO;
		nostromo.authors.add(josephConrad);

		BookInfo heartOfDarkness = new BookInfo();
		heartOfDarkness.title = Books.HEART_OF_DARKNESS;
		heartOfDarkness.authors.add(josephConrad);

		BookInfo theGoldeBough = new BookInfo();
		theGoldeBough.title = Books.THE_GOLDEN_BOUGH;
		theGoldeBough.authors.add(sirJamesGeorgeFrazer);

		BookInfo asterixLeGaulois = new BookInfo();
		asterixLeGaulois.title = Books.ASTERIX_LE_GAULOIS;
		asterixLeGaulois.authors.add(reneGoscinny);
		asterixLeGaulois.authors.add(albertUderzo);

		BookInfo theVoynichManuscript = new BookInfo();
		theVoynichManuscript.title = Books.THE_VOYNICH_MANUSCRIPT;

		BookServices.saveBookInfo(getDb(), nostromo);
		BookServices.saveBookInfo(getDb(), heartOfDarkness);
		BookServices.saveBookInfo(getDb(), theGoldeBough);
		BookServices.saveBookInfo(getDb(), asterixLeGaulois);
		BookServices.saveBookInfo(getDb(), theVoynichManuscript);

		List<BookInfo> bookInfoList = BookServices.getBookInfoList(getDb());

		Assert.assertEquals(5, bookInfoList.size());
		BookInfo bookInfoDb1 = bookInfoList.get(0);
		BookInfo bookInfoDb2 = bookInfoList.get(1);
		BookInfo bookInfoDb3 = bookInfoList.get(2);
		BookInfo bookInfoDb4 = bookInfoList.get(3);
		BookInfo bookInfoDb5 = bookInfoList.get(4);

		// First result corresponds to asterixLeGaulois.
		Assert.assertEquals(asterixLeGaulois.id.longValue(), bookInfoDb1.id.longValue());
		Assert.assertEquals(asterixLeGaulois.title, bookInfoDb1.title);
		Assert.assertEquals(2, bookInfoDb1.authors.size());
		Author authorDb1 = bookInfoDb1.authors.get(0);
		Author authorDb2 = bookInfoDb1.authors.get(1);
		Assert.assertEquals(reneGoscinny.id.longValue(), authorDb1.id.longValue());
		Assert.assertEquals(albertUderzo.id.longValue(), authorDb2.id.longValue());

		// Second result corresponds to heartOfDarkness.
		Assert.assertEquals(heartOfDarkness.id.longValue(), bookInfoDb2.id.longValue());
		Assert.assertEquals(heartOfDarkness.title, bookInfoDb2.title);
		Assert.assertEquals(1, bookInfoDb2.authors.size());
		Author authorDb3 = bookInfoDb2.authors.get(0);
		Assert.assertEquals(josephConrad.id.longValue(), authorDb3.id.longValue());

		// Third result corresponds to nostromo.
		Assert.assertEquals(nostromo.id.longValue(), bookInfoDb3.id.longValue());
		Assert.assertEquals(nostromo.title, bookInfoDb3.title);
		Assert.assertEquals(1, bookInfoDb3.authors.size());
		Author authorDb4 = bookInfoDb3.authors.get(0);
		Assert.assertEquals(josephConrad.id.longValue(), authorDb4.id.longValue());

		// Fourth result corresponds to theVoynichManuscript.
		Assert.assertEquals(theVoynichManuscript.id.longValue(), bookInfoDb4.id.longValue());
		Assert.assertEquals(theVoynichManuscript.title, bookInfoDb4.title);
		Assert.assertEquals(0, bookInfoDb4.authors.size());

		// Fifth result corresponds to theGoldeBough.
		Assert.assertEquals(theGoldeBough.id.longValue(), bookInfoDb5.id.longValue());
		Assert.assertEquals(theGoldeBough.title, bookInfoDb5.title);
		Assert.assertEquals(1, bookInfoDb5.authors.size());
		Author authorDb5 = bookInfoDb5.authors.get(0);
		Assert.assertEquals(sirJamesGeorgeFrazer.id.longValue(), authorDb5.id.longValue());
	}
}
