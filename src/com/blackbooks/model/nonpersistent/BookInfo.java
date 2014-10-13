package com.blackbooks.model.nonpersistent;

import java.io.Serializable;
import java.util.ArrayList;

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

	public ArrayList<Author> authors;

	public Publisher publisher;

	public ArrayList<Identifier> identifiers;

	public ArrayList<Category> categories;

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
	}
}
