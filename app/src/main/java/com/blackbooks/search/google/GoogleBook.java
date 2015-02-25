package com.blackbooks.search.google;

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
 * A class used to store the info of a book returned by the Google Books API.
 */
public class GoogleBook implements BookSearchResult {

    private static final String FORMAT_YEAR_MONTH_DAY = "yyyy-MM-dd";
    private static final String FORMAT_YEAR_MONTH = "yyyy-MM";
    private static final String FORMAT_YEAR = "yyyy";
    private static final String ISBN_10 = "ISBN_10";
    private static final String ISBN_13 = "ISBN_13";
    public String title;
    public String subtitle;
    public final List<String> authors;
    public String publisher;
    public String publishedDate;
    public String description;
    public final List<GoogleIndustryIdentifier> industryIdentifiers;
    public Long pageCount;
    public String height;
    public String width;
    public String thickness;
    public String printType;
    public String mainCategory;
    public final List<String> categories;
    public String smallThumbnailLink;
    public String thumbnailLink;
    public byte[] smallThumbnail;
    public byte[] thumbnail;
    public String language;

    /**
     * Default constructor.
     */
    public GoogleBook() {
        this.authors = new ArrayList<String>();
        this.industryIdentifiers = new ArrayList<GoogleIndustryIdentifier>();
        this.categories = new ArrayList<String>();
    }

    @Override
    public BookSearchResultSource getResultSource() {
        return BookSearchResultSource.GOOGLE_BOOKS;
    }

    @Override
    public BookInfo toBookInfo() {
        BookInfo bookInfo = new BookInfo();
        bookInfo.title = this.title;
        bookInfo.subtitle = this.subtitle;
        bookInfo.languageCode = this.language;

        List<String> authors = this.authors;
        for (String authorName : authors) {
            Author a = new Author();
            a.name = authorName;

            bookInfo.authors.add(a);
        }

        for (GoogleIndustryIdentifier industryIdentifer : this.industryIdentifiers) {
            if (industryIdentifer.type.equals(ISBN_10)) {
                bookInfo.isbn10 = industryIdentifer.identifier;
            } else if (industryIdentifer.type.equals(ISBN_13)) {
                bookInfo.isbn13 = industryIdentifer.identifier;
            }
        }

        for (String categoryName : this.categories) {
            Category category = new Category();
            category.name = categoryName;

            bookInfo.categories.add(category);
        }

        bookInfo.pageCount = this.pageCount;
        bookInfo.publisher.name = this.publisher;
        bookInfo.publishedDate = DateUtils.parse(this.publishedDate, FORMAT_YEAR_MONTH_DAY, FORMAT_YEAR_MONTH, FORMAT_YEAR,
                Locale.ENGLISH);
        bookInfo.description = this.description;
        bookInfo.smallThumbnail = this.smallThumbnail;
        bookInfo.thumbnail = this.thumbnail;

        return bookInfo;
    }

}
