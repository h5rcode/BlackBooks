package com.blackbooks.model.nonpersistent;

import java.util.ArrayList;
import java.util.List;

import com.blackbooks.model.persistent.BookLocation;

public class BookLocationInfo extends BookLocation {

	public List<BookInfo> books;

	private static final long serialVersionUID = 2060138705569284648L;

	public BookLocationInfo() {
		this.books = new ArrayList<BookInfo>();
	}

	public BookLocationInfo(BookLocation bookLocation) {
		this();
		this.id = bookLocation.id;
		this.name = bookLocation.name;
	}
}
