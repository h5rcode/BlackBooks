package com.blackbooks.model.nonpersistent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.Series;

public class SeriesInfo extends Series implements Serializable {

	private static final long serialVersionUID = 3177599050568320257L;

	public List<Book> books;

	public SeriesInfo() {
		this.books = new ArrayList<Book>();
	}

	public SeriesInfo(Series series) {
		this();
		this.id = series.id;
		this.name = series.name;
	}

}
