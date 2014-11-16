package com.blackbooks.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.util.Log;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.search.amazon.AmazonSearcher;
import com.blackbooks.search.google.GoogleBooksSearcher;
import com.blackbooks.search.openlibrary.OpenLibrarySearcher;

/**
 * A class that searches for books using ISBN numbers.
 */
public final class BookSearcher {

	private static final String TAG = BookSearcher.class.getName();

	/**
	 * Private constructor.
	 */
	private BookSearcher() {
	}

	/**
	 * Search information about the book corresponding to the given ISBN. This
	 * method starts asynchronous tasks to search different sources of
	 * information in parallel (Amazon, Google books, Open library, etc.).
	 * 
	 * @param isbn
	 *            ISBN.
	 * @return An instance of {@link BookInfo} if information were found, null
	 *         otherwise.
	 * @throws IOException
	 *             If any book search threw an IOException (most likely because
	 *             of an internet connection problem).
	 * @throws InterruptedException
	 *             If the book search is interrupted.
	 */
	public static BookInfo search(String isbn) throws IOException, InterruptedException {
		List<Callable<BookSearchResult>> searchers = new ArrayList<Callable<BookSearchResult>>();

		AmazonSearcher amazonSearcher = new AmazonSearcher(isbn);
		GoogleBooksSearcher googleBooksSearcher = new GoogleBooksSearcher(isbn);
		OpenLibrarySearcher openLibrarySearcher = new OpenLibrarySearcher(isbn);

		searchers.add(amazonSearcher);
		searchers.add(googleBooksSearcher);
		searchers.add(openLibrarySearcher);

		List<BookSearchResult> bookSearchResultList = new ArrayList<BookSearchResult>();
		ExecutorService executorService = Executors.newCachedThreadPool();
		try {
			List<Future<BookSearchResult>> futureSearchResultList = executorService.invokeAll(searchers);
			bookSearchResultList = getBookSearchResultList(futureSearchResultList);
		} finally {
			executorService.shutdown();
		}

		BookInfo bookInfo = mergeSearchResults(bookSearchResultList);

		return bookInfo;

	}

	/**
	 * Return a list of BookSearchResult from a list of future BookSearchResult.
	 * 
	 * @param futureSearchResultList
	 *            List of future {@link BookSearchResult}.
	 * @return List of {@link BookSearchResult}.
	 * @throws InterruptedException
	 *             If a book search was interrupted.
	 * @throws IOException
	 *             If a book search failed because an IOException was thrown.
	 */
	private static List<BookSearchResult> getBookSearchResultList(List<Future<BookSearchResult>> futureSearchResultList) throws InterruptedException,
			IOException {
		List<BookSearchResult> bookSearchResultList = new ArrayList<BookSearchResult>();
		for (Future<BookSearchResult> futureSearchResult : futureSearchResultList) {
			BookSearchResult bookSearchResult = null;
			try {
				bookSearchResult = futureSearchResult.get();
			} catch (ExecutionException e) {
				Throwable cause = e.getCause();
				if (cause instanceof IOException) {
					throw (IOException) cause;
				} else {
					Log.e(TAG, "A book search encountered an error.");
					Log.e(TAG, cause.getMessage(), cause);
				}
			}
			if (bookSearchResult != null) {
				bookSearchResultList.add(bookSearchResult);
			}
		}
		return bookSearchResultList;
	}

	/**
	 * Merge the different results of a book search into a single instance of
	 * {@link BookInfo}.
	 * 
	 * @param bookSearchResultList
	 *            List of {@link BookSearchResult}.
	 * @return Null if the input list was empty, an instance of {@link BookInfo}
	 *         otherwise.
	 */
	private static BookInfo mergeSearchResults(List<BookSearchResult> bookSearchResultList) {
		BookInfo bookInfo = null;
		boolean isFirst = true;
		for (BookSearchResult bookSearchResult : bookSearchResultList) {
			if (isFirst) {
				isFirst = false;
				bookInfo = new BookInfo();
			}
			BookInfo resultBookInfo = bookSearchResult.toBookInfo();
			bookInfo.merge(resultBookInfo);
		}
		return bookInfo;
	}
}
