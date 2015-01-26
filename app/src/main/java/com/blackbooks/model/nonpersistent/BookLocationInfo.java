package com.blackbooks.model.nonpersistent;

import com.blackbooks.model.persistent.BookLocation;

import java.util.ArrayList;
import java.util.List;

public class BookLocationInfo extends BookLocation {

    private static final long serialVersionUID = 2060138705569284648L;
    public List<BookInfo> books;

    public BookLocationInfo() {
        this.books = new ArrayList<BookInfo>();
    }

    public BookLocationInfo(BookLocation bookLocation) {
        this();
        this.id = bookLocation.id;
        this.name = bookLocation.name;
    }
}
