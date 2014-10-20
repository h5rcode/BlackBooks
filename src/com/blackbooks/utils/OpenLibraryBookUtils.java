package com.blackbooks.utils;

import java.util.ArrayList;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.model.persistent.Identifier;
import com.blackbooks.search.openlibrary.OpenLibraryBook;

/**
 * Utility class to handle OpenLibraryBook objects.
 */
public final class OpenLibraryBookUtils {

	private OpenLibraryBookUtils() {
	}

	/**
	 * Build an instance of BookInfo from a OpenLibraryBook instance.
	 * 
	 * @param openLibraryBook
	 *            OpenLibraryBook.
	 * @return BookInfo.
	 */
	public static BookInfo toBookInfo(OpenLibraryBook openLibraryBook) {
		BookInfo bookInfo = new BookInfo();
		bookInfo.title = openLibraryBook.title;
		bookInfo.subtitle = openLibraryBook.subtitle;

		ArrayList<String> authors = openLibraryBook.authors;
		for (String authorName : authors) {
			Author a = new Author();
			a.name = authorName;

			bookInfo.authors.add(a);
		}
		if (openLibraryBook.isbn10 != null) {
			Identifier identifier = new Identifier();
			identifier.identifier = openLibraryBook.isbn10;
			bookInfo.identifiers.add(identifier);
		}
		if (openLibraryBook.isbn13 != null) {
			Identifier identifier = new Identifier();
			identifier.identifier = openLibraryBook.isbn13;
			bookInfo.identifiers.add(identifier);
		}

		for (String subjectName : openLibraryBook.subjects) {
			Category category = new Category();
			category.name = subjectName;

			bookInfo.categories.add(category);
		}

		bookInfo.pageCount = openLibraryBook.numberOfPages;
		if (openLibraryBook.publishers.size() > 0) {
			bookInfo.publisher.name = openLibraryBook.publishers.get(0);
		}
		bookInfo.publishedDate = openLibraryBook.publishDate;
		bookInfo.smallThumbnail = openLibraryBook.coverSmall;
		bookInfo.thumbnail = openLibraryBook.coverMedium;
		return bookInfo;
	}
}
