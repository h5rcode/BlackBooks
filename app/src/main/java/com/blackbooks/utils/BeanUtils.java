package com.blackbooks.utils;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.BookLocation;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.model.persistent.Publisher;
import com.blackbooks.model.persistent.Series;

/**
 * Bean utility class.
 */
public final class BeanUtils {

    /**
     * Private constructor.
     */
    private BeanUtils() {
    }

    public static boolean areBooksEqual(BookInfo b1, BookInfo b2) {
        boolean areEqual = false;
        if (b1 == null && b2 == null) {
            areEqual = true;
        } else if (b1 == null) {
            areEqual = false;
        } else if (b2 == null) {
            areEqual = false;
        } else {
            areEqual = areEqual(b1.title, b2.title) //
                    && areEqual(b1.subtitle, b2.subtitle) //
                    && areEqual(b1.languageCode, b2.languageCode) //
                    && areEqual(b1.publishedDate, b2.publishedDate) //
                    && areEqual(b1.description, b2.description) //
                    && areEqual(b1.pageCount, b2.pageCount) //
                    && areEqual(b1.smallThumbnail, b2.smallThumbnail) //
                    && areEqual(b1.thumbnail, b2.thumbnail) //
                    && areEqual(b1.isRead, b2.isRead) //
                    && areEqual(b1.isFavourite, b2.isFavourite) //
                    && areEqual(b1.isbn10, b2.isbn10) //
                    && areEqual(b1.comment, b2.comment) //
                    && areEqual(b1.series.name, b2.series.name) //
                    && areEqual(b1.number, b2.number) //
                    && areEqual(b1.loanedTo, b2.loanedTo) //
                    && areEqual(b1.loanDate, b2.loanDate) //
                    && b1.authors.size() == b2.authors.size() //
                    && b1.categories.size() == b2.categories.size() //
                    && arePublishersEqual(b1.publisher, b2.publisher) //
                    && areSeriesEqual(b1.series, b2.series) //
                    && areBookLocationsEqual(b1.bookLocation, b2.bookLocation);

            for (Author a1 : b1.authors) {
                boolean found = false;
                for (Author a2 : b2.authors) {
                    if (areAuthorsEqual(a1, a2)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
            }

            for (Category c1 : b1.categories) {
                boolean found = false;
                for (Category c2 : b2.categories) {
                    if (areCategoriesEqual(c1, c2)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
            }
        }
        return areEqual;
    }

    public static boolean areAuthorsEqual(Author a1, Author a2) {
        boolean areEqual = false;
        if (a1 == null && a2 == null) {
            areEqual = true;
        } else if (a1 == null) {
            areEqual = false;
        } else if (a2 == null) {
            areEqual = false;
        } else {
            areEqual = areEqual(a1.id, a2.id) && areEqual(a1.name, a2.name);
        }
        return areEqual;
    }

    public static boolean areBookLocationsEqual(BookLocation b1, BookLocation b2) {
        boolean areEqual = false;
        if (b1 == null && b2 == null) {
            areEqual = true;
        } else if (b1 == null) {
            areEqual = false;
        } else if (b2 == null) {
            areEqual = false;
        } else {
            areEqual = areEqual(b1.id, b2.id) && areEqual(b1.name, b2.name);
        }
        return areEqual;
    }

    public static boolean areCategoriesEqual(Category c1, Category c2) {
        boolean areEqual = false;
        if (c1 == null && c2 == null) {
            areEqual = true;
        } else if (c1 == null) {
            areEqual = false;
        } else if (c2 == null) {
            areEqual = false;
        } else {
            areEqual = areEqual(c1.id, c2.id) && areEqual(c1.name, c2.name);
        }
        return areEqual;
    }

    public static boolean arePublishersEqual(Publisher p1, Publisher p2) {
        boolean areEqual = false;
        if (p1 == null && p2 == null) {
            areEqual = true;
        } else if (p1 == null) {
            areEqual = false;
        } else if (p2 == null) {
            areEqual = false;
        } else {
            areEqual = areEqual(p1.id, p2.id) && areEqual(p1.name, p2.name);
        }
        return areEqual;
    }

    public static boolean areSeriesEqual(Series s1, Series s2) {
        boolean areEqual = false;
        if (s1 == null && s2 == null) {
            areEqual = true;
        } else if (s1 == null) {
            areEqual = false;
        } else if (s2 == null) {
            areEqual = false;
        } else {
            areEqual = areEqual(s1.id, s2.id) && areEqual(s1.name, s2.name);
        }
        return areEqual;
    }

    private static boolean areEqual(Object left, Object right) {
        boolean areEqual;
        if (left == null && right == null) {
            areEqual = true;
        } else if (left == null) {
            areEqual = false;
        } else if (right == null) {
            areEqual = false;
        } else {
            areEqual = left.equals(right);
        }
        return areEqual;
    }
}
