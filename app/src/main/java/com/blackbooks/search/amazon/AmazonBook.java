package com.blackbooks.search.amazon;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.search.BookSearchResult;
import com.blackbooks.search.BookSearchResultSource;
import com.blackbooks.utils.IsbnUtils;

/**
 * A class used to store the info of a book returned by the Amazon Product
 * Advertising API.
 */
public final class AmazonBook implements BookSearchResult {

    public String title;
    public String author;
    public String isbn;
    public String publisher;
    public String smallImageLink;
    public String mediumImageLink;
    public String largeImageLink;
    public byte[] smallImage;
    public byte[] mediumImage;
    public byte[] largeImage;

    @Override
    public BookSearchResultSource getResultSource() {
        return BookSearchResultSource.AMAZON;
    }

    @Override
    public BookInfo toBookInfo() {
        BookInfo bookInfo = new BookInfo();
        bookInfo.title = this.title;

        if (this.author != null) {
            Author a = new Author();
            a.name = this.author;

            bookInfo.authors.add(a);
        }
        if (this.isbn != null) {
            if (IsbnUtils.isValidIsbn10(this.isbn)) {
                bookInfo.isbn10 = this.isbn;
            } else if (IsbnUtils.isValidIsbn13(this.isbn)) {
                bookInfo.isbn13 = this.isbn;
            }
        }

        if (this.publisher != null) {
            bookInfo.publisher.name = this.publisher;
        }
        bookInfo.smallThumbnail = this.smallImage;
        if (this.largeImage != null) {
            bookInfo.thumbnail = this.largeImage;
        } else {
            bookInfo.thumbnail = this.mediumImage;
        }
        return bookInfo;
    }
}
