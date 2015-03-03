package com.blackbooks.search.openlibrary;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.search.BookSearchResult;
import com.blackbooks.search.BookSearchResultSource;
import com.blackbooks.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The result of a book search using the Open Library API.
 */
public final class OpenLibraryBook implements BookSearchResult {

    private static final String FORMAT_YEAR_MONTH_DAY = "MMM dd, yyyy";
    private static final String FORMAT_YEAR_MONTH = "MMM yyyy";
    private static final String FORMAT_YEAR = "yyyy";

    public String title;
    public String subtitle;

    public final List<String> authors;
    public final List<String> publishers;
    public String isbn10;
    public String isbn13;
    public final List<String> subjects;
    public String publishDate;
    public String coverLinkSmall;
    public String coverLinkMedium;
    public String coverLinkLarge;
    public Long numberOfPages;
    public byte[] coverSmall;
    public byte[] coverMedium;

    /**
     * Default constructor.
     */
    public OpenLibraryBook() {
        this.authors = new ArrayList<String>();
        this.publishers = new ArrayList<String>();
        this.subjects = new ArrayList<String>();
    }

    @Override
    public BookSearchResultSource getResultSource() {
        return BookSearchResultSource.OPEN_LIBRARY;
    }

    @Override
    public BookInfo toBookInfo() {
        BookInfo bookInfo = new BookInfo();
        bookInfo.title = this.title;
        bookInfo.subtitle = this.subtitle;

        List<String> authors = this.authors;
        for (String authorName : authors) {
            Author a = new Author();
            a.name = authorName;

            bookInfo.authors.add(a);
        }
        if (this.isbn10 != null) {
            bookInfo.isbn10 = this.isbn10;
        }
        if (this.isbn13 != null) {
            bookInfo.isbn13 = this.isbn13;
        }

        for (String subjectName : this.subjects) {
            Category category = new Category();
            category.name = subjectName;

            bookInfo.categories.add(category);
        }

        bookInfo.pageCount = this.numberOfPages;
        if (this.publishers.size() > 0) {
            bookInfo.publisher.name = this.publishers.get(0);
        }
        bookInfo.publishedDate = DateUtils.parse(this.publishDate, FORMAT_YEAR_MONTH_DAY, FORMAT_YEAR_MONTH, FORMAT_YEAR,
                Locale.ENGLISH);
        bookInfo.smallThumbnail = this.coverSmall;
        bookInfo.thumbnail = this.coverMedium;
        return bookInfo;
    }
}
