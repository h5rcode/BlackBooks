package com.blackbooks.model.nonpersistent;

import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * All the info about an author.
 */
public class AuthorInfo extends Author {

    private static final long serialVersionUID = -2843891916239884742L;

    public List<Book> books = new ArrayList<Book>();
    public List<SeriesInfo> series = new ArrayList<SeriesInfo>();

    /**
     * Default constructor.
     */
    public AuthorInfo() {
        super();
    }

    /**
     * Copy constructor.
     *
     * @param author Author.
     */
    public AuthorInfo(Author author) {
        super(author);
    }
}
