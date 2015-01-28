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

    public List<Author> authors = new ArrayList<Author>();
    public Publisher publisher = new Publisher();
    public List<Category> categories = new ArrayList<Category>();
    public BookLocation bookLocation = new BookLocation();
    public Series series = new Series();

    /**
     * Default constructor.
     */
    public BookInfo() {
        super();
    }

    /**
     * Copy constructor.
     *
     * @param book Book.
     */
    public BookInfo(Book book) {
        super(book);
    }

    /**
     * Copy constructor.
     *
     * @param bookInfo BookInfo.
     */
    public BookInfo(BookInfo bookInfo) {
        super(bookInfo);
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
