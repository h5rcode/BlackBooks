package com.blackbooks.search.amazon;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;

import org.xmlpull.v1.XmlPullParserException;

import com.blackbooks.search.BookSearchResult;
import com.blackbooks.utils.HttpUtils;

/**
 * Class that searches books using the Amazon Product Advertising API.
 */
public class AmazonSearcher implements Callable<BookSearchResult> {

	private static final String URI_FORMAT_STRING = "http://isbnlookup-h5rcode.rhcloud.com/api/v1/aws/book/%s/Large";

	private String mIsbn;

	/**
	 * Constructor.
	 * 
	 * @param isbn
	 *            ISBN code.
	 */
	public AmazonSearcher(String isbn) {
		mIsbn = isbn;
	}

	@Override
	public BookSearchResult call() throws Exception {
		return search(mIsbn);
	}

	/**
	 * Search book info.
	 * 
	 * @param isbn
	 *            ISBN code.
	 * @return AmazonBook.
	 * @throws URISyntaxException
	 *             If an incorrect URI was built for the search.
	 * @throws IOException
	 *             If a connection problem occurred.
	 * @throws XmlPullParserException
	 *             If something bad happened during the parsing of the result.
	 */
	private static AmazonBook search(String isbn) throws URISyntaxException, IOException, XmlPullParserException {
		String url = getAmazonUrl(isbn);
		String xml = HttpUtils.getText(url);
		AmazonBook amazonBook = AmazonXmlParser.parse(xml);
		if (amazonBook != null) {
			if (amazonBook.smallImageLink != null) {
				amazonBook.smallImage = HttpUtils.getBinary(amazonBook.smallImageLink);
			}
			if (amazonBook.mediumImageLink != null) {
				amazonBook.mediumImage = HttpUtils.getBinary(amazonBook.mediumImageLink);
			}
			if (amazonBook.largeImageLink != null) {
				amazonBook.largeImage = HttpUtils.getBinary(amazonBook.largeImageLink);
			}
		}
		return amazonBook;
	}

	/**
	 * Make a call to the REST web-service that builds the request to the Amazon
	 * Product API.
	 * 
	 * @param isbn
	 *            ISBN code.
	 * @return URL to get the info from the Amazon Product API.
	 * @throws URISyntaxException
	 *             If a incorrect URI was built.
	 * @throws IOException
	 *             In case of a connection problem.
	 */
	private static String getAmazonUrl(String isbn) throws URISyntaxException, IOException {
		String url = String.format(URI_FORMAT_STRING, isbn);
		String amazonUrl = HttpUtils.getText(url);
		return amazonUrl;
	}
}
