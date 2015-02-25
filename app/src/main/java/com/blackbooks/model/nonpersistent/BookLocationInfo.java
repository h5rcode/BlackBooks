package com.blackbooks.model.nonpersistent;

import com.blackbooks.model.persistent.BookLocation;

import java.util.ArrayList;
import java.util.List;

public class BookLocationInfo extends BookLocation {

    private static final long serialVersionUID = 2060138705569284648L;

    public final List<BookInfo> books = new ArrayList<BookInfo>();

    /**
     * Default constructor.
     */
    public BookLocationInfo() {
        super();
    }

    /**
     * Copy constructor.
     *
     * @param bookLocation BookLocation.
     */
    public BookLocationInfo(BookLocation bookLocation) {
        super(bookLocation);
    }
}
