package com.blackbooks.utils;

import java.util.ArrayList;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.model.persistent.Identifier;
import com.blackbooks.search.GoogleBook;
import com.blackbooks.search.GoogleIndustryIdentifier;

/**
 * Utility class to handle GoogleBook objects.
 */
public final class GoogleBookUtils {

	/**
	 * Build an instance of BookInfo from a GoogleBook instance.
	 * 
	 * @param googleBook
	 *            GoogleBook.
	 * @return BookInfo.
	 */
	public static BookInfo toBookInfo(GoogleBook googleBook) {
		BookInfo bookInfo = new BookInfo();
		bookInfo.title = googleBook.title;
		bookInfo.subtitle = googleBook.subtitle;
		bookInfo.languageCode = googleBook.language;

		ArrayList<String> authors = googleBook.authors;
		for (String authorName : authors) {
			Author a = new Author();
			a.name = authorName;

			bookInfo.authors.add(a);
		}

		for (GoogleIndustryIdentifier industryIdentifer : googleBook.industryIdentifiers) {
			Identifier identifier = new Identifier();
			identifier.identifier = industryIdentifer.identifier;

			bookInfo.identifiers.add(identifier);
		}

		for (String categoryName : googleBook.categories) {
			Category category = new Category();
			category.name = categoryName;

			bookInfo.categories.add(category);
		}

		bookInfo.pageCount = googleBook.pageCount;
		bookInfo.publisher.name = googleBook.publisher;
		bookInfo.publishedDate = googleBook.publishedDate;
		bookInfo.description = googleBook.description;
		bookInfo.smallThumbnail = googleBook.smallThumbnail;
		bookInfo.thumbnail = googleBook.thumbnail;
		return bookInfo;
	}

}
