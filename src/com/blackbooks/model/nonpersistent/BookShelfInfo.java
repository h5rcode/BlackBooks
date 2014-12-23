package com.blackbooks.model.nonpersistent;

import java.util.ArrayList;
import java.util.List;

import com.blackbooks.model.persistent.BookShelf;

public class BookShelfInfo extends BookShelf {

	public List<BookInfo> books;

	private static final long serialVersionUID = 2060138705569284648L;

	public BookShelfInfo() {
		this.books = new ArrayList<BookInfo>();
	}

	public BookShelfInfo(BookShelf bookShelf) {
		this();
		this.id = bookShelf.id;
		this.name = bookShelf.name;
	}
}
