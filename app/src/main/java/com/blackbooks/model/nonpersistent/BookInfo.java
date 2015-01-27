package com.blackbooks.model.nonpersistent;

import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.BookLocation;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.model.persistent.Publisher;
import com.blackbooks.model.persistent.Series;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * All the info about a book.
 */
public class BookInfo extends Book implements Serializable {

    private static final long serialVersionUID = 6822093497761500275L;

    public List<Author> authors;

    public Publisher publisher;

    public List<Category> categories;

    public BookLocation bookLocation;

    public Series series;

    /**
     * Default constructor.
     */
    public BookInfo() {
        this.authors = new ArrayList<Author>();
        this.publisher = new Publisher();
        this.categories = new ArrayList<Category>();
        this.bookLocation = new BookLocation();
        this.series = new Series();
    }

    /**
     * Constructor that creates a copy of an instance of Book.
     *
     * @param book Instance of Book.
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
        this.smallThumbnail = book.smallThumbnail;
        this.thumbnail = book.thumbnail;
        this.isRead = book.isRead;
        this.isFavourite = book.isFavourite;
        this.isbn10 = book.isbn10;
        this.isbn13 = book.isbn13;
        this.comment = book.comment;
        this.bookLocationId = book.bookLocationId;
        this.seriesId = book.seriesId;
        this.number = book.number;
        this.loanedTo = book.loanedTo;
        this.loanDate = book.loanDate;
    }

    /**
     * Copy constructor.
     *
     * @param bookInfo BookInfo.
     */
    public BookInfo(BookInfo bookInfo) {
        this((Book) bookInfo);
        for (Author author : bookInfo.authors) {
            this.authors.add(new Author(author));
        }
        this.publisher = new Publisher(bookInfo.publisher);
        for (Category category : bookInfo.categories) {
            this.categories.add(new Category(category));
        }
        this.bookLocation = new BookLocation(bookInfo.bookLocation);
        this.series = new Series(bookInfo.series);
    }

    /**
     * Merge a BookInfo with the current instance.
     *
     * @param bookInfo BookInfo.
     */
    public void merge(BookInfo bookInfo) {
        if (bookInfo != null) {
            if (bookInfo.title != null) {
                this.title = bookInfo.title;
            }
            if (bookInfo.subtitle != null) {
                this.subtitle = bookInfo.subtitle;
            }
            if (bookInfo.languageCode != null) {
                this.languageCode = bookInfo.languageCode;
            }
            if (bookInfo.publishedDate != null) {
                this.publishedDate = bookInfo.publishedDate;
            }
            if (bookInfo.description != null) {
                this.description = bookInfo.description;
            }
            if (bookInfo.pageCount != null) {
                this.pageCount = bookInfo.pageCount;
            }
            if (bookInfo.smallThumbnail != null) {
                this.smallThumbnail = bookInfo.smallThumbnail;
            }
            if (bookInfo.thumbnail != null) {
                this.thumbnail = bookInfo.thumbnail;
            }
            if (bookInfo.isbn10 != null) {
                this.isbn10 = bookInfo.isbn10;
            }
            if (bookInfo.isbn13 != null) {
                this.isbn13 = bookInfo.isbn13;
            }
            mergeAuthors(bookInfo.authors);
            mergeCategories(bookInfo.categories);
            mergePublisher(bookInfo.publisher.name);
        }
    }

    private void mergeAuthors(List<Author> authors) {
        if (authors != null) {
            for (Author author : authors) {
                String authorName = author.name;
                boolean found = false;
                for (Author thisAuthor : this.authors) {
                    if (thisAuthor.name.equalsIgnoreCase(authorName)) {
                        found = true;
                        break;
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
                    if (thisAuthor.name.equalsIgnoreCase(categoryName)) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    this.categories.add(category);
                }
            }
        }
    }

    private void mergePublisher(String publisherName) {
        if (publisherName != null) {
            this.publisher.name = publisherName;
        }
    }
}
