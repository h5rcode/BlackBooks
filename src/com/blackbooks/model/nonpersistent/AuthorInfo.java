package com.blackbooks.model.nonpersistent;

import java.util.ArrayList;
import java.util.List;

import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Book;

/**
 * All the info about an author.
 * 
 */
public class AuthorInfo extends Author {

	private static final long serialVersionUID = -2843891916239884742L;

	public List<Book> books;

	/**
	 * Default constructor.
	 */
	public AuthorInfo() {
		this.books = new ArrayList<Book>();
	}

	/**
	 * Constructor that creates a copy of an instance of Author.
	 * 
	 * @param author
	 *            Instance of Author.
	 */
	public AuthorInfo(Author author) {
		this();
		this.id = author.id;
		this.name = author.name;
	}
}
