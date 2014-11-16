package com.blackbooks.search.google;

import java.util.ArrayList;
import java.util.List;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.model.persistent.Identifier;
import com.blackbooks.search.BookSearchResult;

/**
 * A class used to store the info of a book returned by the Google Books API.
 */
public class GoogleBook implements BookSearchResult {
	public String title;
	public String subtitle;
	public List<String> authors;
	public String publisher;
	public String publishedDate;
	public String description;
	public List<GoogleIndustryIdentifier> industryIdentifiers;
	public Long pageCount;
	public String height;
	public String width;
	public String thickness;
	public String printType;
	public String mainCategory;
	public List<String> categories;
	public String smallThumbnailLink;
	public String thumbnailLink;
	public byte[] smallThumbnail;
	public byte[] thumbnail;
	public String language;

	/**
	 * Default constructor.
	 */
	public GoogleBook() {
		this.authors = new ArrayList<String>();
		this.industryIdentifiers = new ArrayList<GoogleIndustryIdentifier>();
		this.categories = new ArrayList<String>();
	}

	@Override
	public BookInfo toBookInfo() {
		BookInfo bookInfo = new BookInfo();
		bookInfo.title = this.title;
		bookInfo.subtitle = this.subtitle;
		bookInfo.languageCode = this.language;

		List<String> authors = this.authors;
		for (String authorName : authors) {
			Author a = new Author();
			a.name = authorName;

			bookInfo.authors.add(a);
		}

		for (GoogleIndustryIdentifier industryIdentifer : this.industryIdentifiers) {
			Identifier identifier = new Identifier();
			identifier.identifier = industryIdentifer.identifier;

			bookInfo.identifiers.add(identifier);
		}

		for (String categoryName : this.categories) {
			Category category = new Category();
			category.name = categoryName;

			bookInfo.categories.add(category);
		}

		bookInfo.pageCount = this.pageCount;
		bookInfo.publisher.name = this.publisher;
		bookInfo.publishedDate = this.publishedDate;
		bookInfo.description = this.description;
		bookInfo.smallThumbnail = this.smallThumbnail;
		bookInfo.thumbnail = this.thumbnail;
		return bookInfo;
	}
}
