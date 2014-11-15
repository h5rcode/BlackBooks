package com.blackbooks.model.nonpersistent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.model.persistent.Identifier;
import com.blackbooks.model.persistent.Publisher;

/**
 * All the info about a book.
 * 
 */
public class BookInfo extends Book implements Serializable {

	private static final long serialVersionUID = 6822093497761500275L;

	public List<Author> authors;

	public Publisher publisher;

	public List<Identifier> identifiers;

	public List<Category> categories;

	/*
	 * Default constructor.
	 */
	public BookInfo() {
		this.authors = new ArrayList<Author>();
		this.publisher = new Publisher();
		this.identifiers = new ArrayList<Identifier>();
		this.categories = new ArrayList<Category>();
	}

	/**
	 * Constructor that creates a copy of an instance of Book.
	 * 
	 * @param book
	 *            Instance of Book.
	 */
	public BookInfo(Book book) {
		this();
		this.id = book.id;
		this.title = book.title;
		this.subtitle = book.subtitle;
		this.languageCode = book.languageCode;
		this.publisherId = book.publisherId;
		this.publishedDate = book.publishedDate;
		this.description = book.description;
		this.pageCount = book.pageCount;
		this.height = book.height;
		this.width = book.width;
		this.thickness = book.thickness;
		this.printType = book.printType;
		this.mainCategory = book.mainCategory;
		this.smallThumbnail = book.smallThumbnail;
		this.thumbnail = book.thumbnail;
		this.isRead = book.isRead;
		this.isFavourite = book.isFavourite;
	}

	/**
	 * Merge a BookInfo with the current instance.
	 * 
	 * @param bookInfo
	 *            BookInfo.
	 */
	public void merge(BookInfo bookInfo) {
		if (bookInfo != null) {
			if (this.title == null) {
				this.title = bookInfo.title;
			}
			if (this.subtitle == null) {
				this.subtitle = bookInfo.subtitle;
			}
			if (this.languageCode == null) {
				this.languageCode = bookInfo.languageCode;
			}
			if (this.publishedDate == null) {
				this.publishedDate = bookInfo.publishedDate;
			}
			if (this.description == null) {
				this.description = bookInfo.description;
			}
			if (this.pageCount == null) {
				this.pageCount = bookInfo.pageCount;
			}
			if (this.height == null) {
				this.height = bookInfo.height;
			}
			if (this.width == null) {
				this.width = bookInfo.width;
			}
			if (this.thickness == null) {
				this.thickness = bookInfo.thickness;
			}
			if (this.printType == null) {
				this.printType = bookInfo.printType;
			}
			if (this.mainCategory == null) {
				this.mainCategory = bookInfo.mainCategory;
			}
			if (this.smallThumbnail == null) {
				this.smallThumbnail = bookInfo.smallThumbnail;
			}
			if (this.thumbnail == null) {
				this.thumbnail = bookInfo.thumbnail;
			}
		}

		mergeAuthors(bookInfo.authors);
		mergeCategories(bookInfo.categories);
		mergeIdentifiers(bookInfo.identifiers);
		mergePublisher(bookInfo.publisher.name);
	}

	private void mergeAuthors(List<Author> authors) {
		if (authors != null) {
			for (Author author : authors) {
				String authorName = author.name;
				boolean found = false;
				for (Author thisAuthor : this.authors) {
					if (thisAuthor.name.equals(authorName)) {
						found = true;
						continue;
					}
				}

				if (!found) {
					this.authors.add(author);
				}
			}
		}
	}

	private void mergeCategories(List<Category> categories) {
		if (categories != null) {
			for (Category category : categories) {
				String categoryName = category.name;
				boolean found = false;
				for (Category thisAuthor : this.categories) {
					if (thisAuthor.name.equals(categoryName)) {
						found = true;
						continue;
					}
				}

				if (!found) {
					this.categories.add(category);
				}
			}
		}
	}

	private void mergeIdentifiers(List<Identifier> identifiers) {
		if (identifiers != null) {
			for (Identifier identifier : identifiers) {
				String identifierValue = identifier.identifier;
				boolean found = false;
				for (Identifier thisAuthor : this.identifiers) {
					if (thisAuthor.identifier.equals(identifierValue)) {
						found = true;
						continue;
					}
				}

				if (!found) {
					this.identifiers.add(identifier);
				}
			}
		}
	}

	private void mergePublisher(String publisherName) {
		if (this.publisher.name == null) {
			this.publisher.name = publisherName;
		}
	}
}
