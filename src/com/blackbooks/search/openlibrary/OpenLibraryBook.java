package com.blackbooks.search.openlibrary;

import java.util.ArrayList;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.model.persistent.Identifier;
import com.blackbooks.search.BookSearchResult;

/**
 * The result of a book search using the Open Library API.
 */
public class OpenLibraryBook implements BookSearchResult {

	public String title;
	public String subtitle;

	public ArrayList<String> authors;
	public ArrayList<String> publishers;
	public String isbn10;
	public String isbn13;
	public ArrayList<String> subjects;
	public String publishDate;
	public String coverLinkSmall;
	public String coverLinkMedium;
	public String coverLinkLarge;
	public Long numberOfPages;
	public byte[] coverSmall;
	public byte[] coverMedium;

	/**
	 * Default constructor.
	 */
	public OpenLibraryBook() {
		this.authors = new ArrayList<String>();
		this.publishers = new ArrayList<String>();
		this.subjects = new ArrayList<String>();
	}

	@Override
	public BookInfo toBookInfo() {
		BookInfo bookInfo = new BookInfo();
		bookInfo.title = this.title;
		bookInfo.subtitle = this.subtitle;

		ArrayList<String> authors = this.authors;
		for (String authorName : authors) {
			Author a = new Author();
			a.name = authorName;

			bookInfo.authors.add(a);
		}
		if (this.isbn10 != null) {
			Identifier identifier = new Identifier();
			identifier.identifier = this.isbn10;
			bookInfo.identifiers.add(identifier);
		}
		if (this.isbn13 != null) {
			Identifier identifier = new Identifier();
			identifier.identifier = this.isbn13;
			bookInfo.identifiers.add(identifier);
		}

		for (String subjectName : this.subjects) {
			Category category = new Category();
			category.name = subjectName;

			bookInfo.categories.add(category);
		}

		bookInfo.pageCount = this.numberOfPages;
		if (this.publishers.size() > 0) {
			bookInfo.publisher.name = this.publishers.get(0);
		}
		bookInfo.publishedDate = this.publishDate;
		bookInfo.smallThumbnail = this.coverSmall;
		bookInfo.thumbnail = this.coverMedium;
		return bookInfo;
	}
}
