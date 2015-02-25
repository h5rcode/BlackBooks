package com.blackbooks.search.amazon;

import com.blackbooks.search.BookSearchResult;
import com.blackbooks.utils.HttpUtils;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Class that searches books using the Amazon Product Advertising API.
 */
public class AmazonSearcher implements Callable<BookSearchResult> {

    private static final String URI_FORMAT_STRING = "http://isbnlookup-h5rcode.rhcloud.com/api/v1/aws/book/%s/Large";

    private final String mIsbn;

    /**
     * Constructor.
     *
     * @param isbn ISBN code.
     */
    public AmazonSearcher(String isbn) {
        mIsbn = isbn;
    }

    /**
     * Search book info.
     *
     * @param isbn ISBN code.
     * @return AmazonBook.
     * @throws URISyntaxException     If an incorrect URI was built for the search.
     * @throws IOException            If a connection problem occurred.
     * @throws XmlPullParserException If something bad happened during the parsing of the result.
     */
    private static AmazonBook search(String isbn) throws URISyntaxException, IOException, XmlPullParserException {
        String url = getAmazonUrl(isbn);
        String xml = HttpUtils.getText(url);
        List<AmazonBook> amazonBookList = AmazonXmlParser.parse(xml);

        AmazonBook amazonBook = selectAmazonBook(amazonBookList);

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
     * @param isbn ISBN code.
     * @return URL to get the info from the Amazon Product API.
     * @throws URISyntaxException If a incorrect URI was built.
     * @throws IOException        In case of a connection problem.
     */
    private static String getAmazonUrl(String isbn) throws URISyntaxException, IOException {
        String url = String.format(URI_FORMAT_STRING, isbn);
        String amazonUrl = HttpUtils.getText(url);
        return amazonUrl;
    }

    /**
     * Select the AmazonBook that will be returned by the search.
     *
     * @param amazonBookList List of AmazonBook.
     * @return AmazonBook.
     */
    private static AmazonBook selectAmazonBook(List<AmazonBook> amazonBookList) {
        AmazonBook result = null;
        int maxScore = -1;
        for (AmazonBook amazonBook : amazonBookList) {
            int score = 0;

            if (amazonBook.title != null) {
                score += 10000;
            }
            if (amazonBook.author != null) {
                score += 1000;
            }
            if (amazonBook.largeImageLink != null) {
                score += 100;
            }
            if (amazonBook.mediumImageLink != null) {
                score += 10;
            }
            if (amazonBook.smallImageLink != null) {
                score += 1;
            }
            if (score > maxScore) {
                maxScore = score;
                result = amazonBook;
            }
        }
        return result;
    }

    @Override
    public BookSearchResult call() throws Exception {
        return search(mIsbn);
    }
}
