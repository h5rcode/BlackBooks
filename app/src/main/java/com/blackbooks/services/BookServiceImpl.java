package com.blackbooks.services;

import android.support.v4.util.LongSparseArray;

import com.blackbooks.cache.ThumbnailManager;
import com.blackbooks.database.TransactionManager;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.BookAuthor;
import com.blackbooks.model.persistent.BookCategory;
import com.blackbooks.model.persistent.BookLocation;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.model.persistent.Publisher;
import com.blackbooks.model.persistent.Series;
import com.blackbooks.model.persistent.fts.BookFTS;
import com.blackbooks.repositories.AuthorRepository;
import com.blackbooks.repositories.BookAuthorRepository;
import com.blackbooks.repositories.BookCategoryRepository;
import com.blackbooks.repositories.BookFTSRepository;
import com.blackbooks.repositories.BookLocationRepository;
import com.blackbooks.repositories.BookRepository;
import com.blackbooks.repositories.CategoryRepository;
import com.blackbooks.repositories.PublisherRepository;
import com.blackbooks.repositories.SeriesRepository;
import com.blackbooks.utils.IsbnUtils;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Book services.
 */
public final class BookServiceImpl implements BookService {

    private final AuthorRepository authorRepository;
    private final BookAuthorRepository bookAuthorRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final BookFTSRepository bookFTSRepository;
    private final BookLocationRepository bookLocationRepository;
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final PublisherRepository publisherRepository;
    private final SeriesRepository seriesRepository;
    private final ThumbnailManager thumbnailManager;
    private final TransactionManager transactionManager;

    public BookServiceImpl(
            AuthorRepository authorRepository,
            BookAuthorRepository bookAuthorRepository,
            BookCategoryRepository bookCategoryRepository,
            BookFTSRepository bookFTSRepository,
            BookLocationRepository bookLocationRepository,
            BookRepository bookRepository,
            CategoryRepository categoryRepository,
            PublisherRepository publisherRepository,
            SeriesRepository seriesRepository,
            ThumbnailManager thumbnailManager, TransactionManager transactionManager) {
        this.authorRepository = authorRepository;
        this.bookAuthorRepository = bookAuthorRepository;
        this.bookCategoryRepository = bookCategoryRepository;
        this.bookFTSRepository = bookFTSRepository;
        this.bookLocationRepository = bookLocationRepository;
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.publisherRepository = publisherRepository;
        this.seriesRepository = seriesRepository;
        this.thumbnailManager = thumbnailManager;
        this.transactionManager = transactionManager;
    }

    public void deleteBook(long bookId) {
        transactionManager.beginTransaction();
        try {
            List<BookAuthor> bookAuthorsByBook = bookAuthorRepository.getBookAuthorListByBook(bookId);
            List<BookCategory> bookCategoriesByBook = bookCategoryRepository.getBookCategoryListByBook(bookId);
            bookRepository.deleteBook(bookId);

            for (BookAuthor bookAuthor : bookAuthorsByBook) {
                List<BookAuthor> baListByAuthor = bookAuthorRepository.getBookAuthorListByAuthor(bookAuthor.authorId);

                if (baListByAuthor.isEmpty()) {
                    authorRepository.deleteAuthor(bookAuthor.authorId);
                }
            }

            for (BookCategory bookCategory : bookCategoriesByBook) {
                List<BookCategory> bcListByCategory = bookCategoryRepository.getBookCategoryListByCategory(bookCategory.categoryId);

                if (bcListByCategory.isEmpty()) {
                    categoryRepository.deleteCategory(bookCategory.categoryId);
                }
            }

            bookFTSRepository.deleteBook(bookId);

            publisherRepository.deletePublishersWithoutBooks();
            seriesRepository.deleteSeriesWithoutBooks();
            bookLocationRepository.deleteBookLocationsWithoutBooks();

            thumbnailManager.removeThumbnails(bookId);

            transactionManager.setTransactionSuccessful();
        } finally {
            transactionManager.endTransaction();
        }
    }

    public Book getBook(long bookId) {
        return bookRepository.getBook(bookId);
    }

    public BookInfo getBookInfo(long bookId) {
        Book book = bookRepository.getBook(bookId);
        BookInfo bookInfo = new BookInfo(book);

        List<BookAuthor> bookAuthorList = bookAuthorRepository.getBookAuthorListByBook(book.id);
        for (BookAuthor bookAuthor : bookAuthorList) {
            Author author = authorRepository.getAuthor(bookAuthor.authorId);
            bookInfo.authors.add(author);
        }

        if (book.publisherId != null) {
            bookInfo.publisher = publisherRepository.getPublisher(book.publisherId);
        }

        if (book.bookLocationId != null) {
            bookInfo.bookLocation = bookLocationRepository.getBookLocation(book.bookLocationId);
        }

        if (book.seriesId != null) {
            bookInfo.series = seriesRepository.getSeries(book.seriesId);
        }

        List<BookCategory> bookCategoryList = bookCategoryRepository.getBookCategoryListByBook(book.id);
        for (BookCategory bookCategory : bookCategoryList) {
            Category category = categoryRepository.getCategory(bookCategory.categoryId);
            bookInfo.categories.add(category);
        }

        return bookInfo;
    }

    public int getBookCountByAuthor(long authorId) {
        return bookRepository.getBookCountByAuthor(authorId);
    }

    public int getBookCountByBookLocation(Long bookLocationId) {
        return bookRepository.getBookCountByBookLocation(bookLocationId);
    }

    public int getBookCountByCategory(Long categoryId) {
        return bookRepository.getBookCountByCategory(categoryId);
    }

    public int getBookCountByFirstLetter(String firstLetter) {
        return bookRepository.getBookCountByFirstLetter(firstLetter);
    }

    public int getBookCountByLanguage(String languageCode) {
        return bookRepository.getBookCountByLanguage(languageCode);
    }

    public int getBookCountByLoanedTo(String loanedTo) {
        return bookRepository.getBookCountByLoanedTo(loanedTo);
    }

    public int getBookCountBySeries(Long seriesId) {
        return bookRepository.getBookCountBySeries(seriesId);
    }

    public List<Book> getBookListByIsbn(String isbn) {
        if (IsbnUtils.isValidIsbn10(isbn)) {
            return bookRepository.getBooksByIsbn10(isbn);
        } else if (IsbnUtils.isValidIsbn13(isbn)) {
            return bookRepository.getBooksByIsbn13(isbn);
        } else {
            throw new InvalidParameterException("isbn");
        }
    }

    public List<BookInfo> getBookInfoListByAuthor(long authorId, int limit, int offset) {
        List<Book> bookList = bookRepository.getBooksByAuthor(authorId, limit, offset);
        return getBookInfoListFromBookList(bookList);
    }

    public List<BookInfo> getBookInfoListByBookLocation(long bookLocationId, int limit, int offset) {
        List<Book> bookList = bookRepository.getBooksByBookLocation(bookLocationId, limit, offset);
        return getBookInfoListFromBookList(bookList);
    }

    public List<BookInfo> getBookInfoListByCategory(long categoryId, int limit, int offset) {
        List<Book> bookList = bookRepository.getBooksByCategory(categoryId, limit, offset);
        return getBookInfoListFromBookList(bookList);
    }

    public List<BookInfo> getBookInfoListByFirstLetter(String firstLetter, int limit, int offset) {
        List<Book> bookList = bookRepository.getBooksByFirstLetter(firstLetter, limit, offset);
        return getBookInfoListFromBookList(bookList);
    }

    public List<BookInfo> getBookInfoListByLanguage(String languageCode, int limit, int offset) {
        List<Book> bookList = bookRepository.getBooksByLanguage(languageCode, limit, offset);
        return getBookInfoListFromBookList(bookList);
    }

    public List<BookInfo> getBookInfoListByLoanedTo(String loanedTo, int limit, int offset) {
        List<Book> bookList = bookRepository.getBooksByLoanedTo(loanedTo, limit, offset);
        return getBookInfoListFromBookList(bookList);
    }

    public List<BookInfo> getBookInfoListBySeries(long seriesId, int limit, int offset) {
        List<Book> bookList = bookRepository.getBooksBySeries(seriesId, limit, offset);
        return getBookInfoListFromBookList(bookList);
    }

    public List<BookInfo> getBookInfoListFavourite(int limit, int offset) {
        List<Book> bookList = bookRepository.getFavouriteBooks(limit, offset);
        return getBookInfoListFromBookList(bookList);
    }

    public List<BookInfo> getBookInfoListToRead(int limit, int offset) {
        List<Book> bookList = bookRepository.getBookInfoListToRead(limit, offset);
        return getBookInfoListFromBookList(bookList);
    }

    public List<BookInfo> getBookInfoListFromBookList(List<Book> bookList) {
        List<BookInfo> bookInfoList = new ArrayList<>();

        if (!bookList.isEmpty()) {
            List<Long> bookIdList = new ArrayList<>();
            for (Book book : bookList) {
                bookIdList.add(book.id);
            }

            List<BookAuthor> bookAuthorList = bookAuthorRepository.getBookAuthorListByBooks(bookIdList);

            List<Long> authorIdList = new ArrayList<>();
            for (BookAuthor bookAuthor : bookAuthorList) {
                if (!authorIdList.contains(bookAuthor.authorId)) {
                    authorIdList.add(bookAuthor.authorId);
                }
            }

            List<Author> authorList = authorRepository.getAuthorsByIds(authorIdList);

            LongSparseArray<List<BookAuthor>> bookAuthorMap = new LongSparseArray<>();
            LongSparseArray<Author> authorMap = new LongSparseArray<>();
            for (BookAuthor bookAuthor : bookAuthorList) {
                if (bookAuthorMap.get(bookAuthor.bookId) == null) {
                    bookAuthorMap.put(bookAuthor.bookId, new ArrayList<BookAuthor>());
                }
                List<BookAuthor> baList = bookAuthorMap.get(bookAuthor.bookId);
                baList.add(bookAuthor);
            }
            for (Author author : authorList) {
                authorMap.put(author.id, author);
            }

            for (Book book : bookList) {
                BookInfo bookInfo = new BookInfo(book);

                List<BookAuthor> baList = bookAuthorMap.get(book.id);
                if (baList != null) {
                    for (BookAuthor bookAuthor : baList) {
                        Author author = authorMap.get(bookAuthor.authorId);
                        bookInfo.authors.add(author);
                    }
                }

                bookInfoList.add(bookInfo);
            }
        }
        return bookInfoList;
    }

    public byte[] getBookSmallThumbnail(long bookId) {
        return bookRepository.getBookSmallThumbnail(bookId);
    }

    public void markBookAsFavourite(long bookId) {
        bookRepository.markBookAsFavourite(bookId);
    }

    public void markBookAsRead(long bookId) {
        bookRepository.markBookAsRead(bookId);
    }

    public void returnBook(long bookId) {
        bookRepository.returnBook(bookId);
    }

    public void saveBookInfo(BookInfo bookInfo) {
        transactionManager.beginTransaction();
        try {
            boolean isCreation = bookInfo.id == null;

            Publisher publisher = bookInfo.publisher;
            if (publisher.name != null) {
                bookInfo.publisherId = savePublisherIfNotExists(publisher);
            } else {
                bookInfo.publisherId = null;
            }

            BookLocation bookLocation = bookInfo.bookLocation;
            if (bookLocation.name != null) {
                bookInfo.bookLocationId = saveBookLocationIfNotExists(bookLocation);
            } else {
                bookInfo.bookLocationId = null;
            }

            Series series = bookInfo.series;
            if (series.name != null) {
                bookInfo.seriesId = saveSeriesIfNotExists(series);
            } else {
                bookInfo.seriesId = null;
            }

            if (bookInfo.isbn10 != null && !IsbnUtils.isValidIsbn10(bookInfo.isbn10)) {
                throw new InvalidParameterException("Invalid ISBN-10.");
            }

            if (bookInfo.isbn13 != null && !IsbnUtils.isValidIsbn13(bookInfo.isbn13)) {
                throw new InvalidParameterException("Invalid ISBN-13.");
            }

            bookInfo.id = bookRepository.save(bookInfo);

            BookFTS bookFts = new BookFTS(bookInfo);
            if (isCreation) {
                bookFTSRepository.insert(bookFts);
            } else {
                bookFTSRepository.update(bookFts);

                publisherRepository.deletePublishersWithoutBooks();
                seriesRepository.deleteSeriesWithoutBooks();
                bookLocationRepository.deleteBookLocationsWithoutBooks();

                thumbnailManager.removeThumbnails(bookInfo.id);
            }

            updateBookAuthorList(bookInfo);
            updateBookCategoryList(bookInfo);

            transactionManager.setTransactionSuccessful();
        } finally {
            transactionManager.endTransaction();
        }
    }

    private long saveSeriesIfNotExists(Series series) {
        Series criteria = new Series();
        criteria.name = series.name;

        Series seriesDb = seriesRepository.getSeriesByCriteria(criteria);

        long seriesId;
        if (seriesDb != null) {
            seriesId = seriesDb.id;
        } else {
            seriesRepository.saveSeries(series);
            seriesId = series.id;
        }

        return seriesId;
    }

    private long saveBookLocationIfNotExists(BookLocation bookLocation) {
        BookLocation criteria = new BookLocation();
        criteria.name = bookLocation.name;

        BookLocation bookLocationDb = bookLocationRepository.getBookLocationByCriteria(criteria);

        long bookLocationId;
        if (bookLocationDb != null) {
            bookLocationId = bookLocationDb.id;
        } else {
            bookLocationRepository.saveBookLocation(bookLocation);
            bookLocationId = bookLocation.id;
        }

        return bookLocationId;
    }

    private long savePublisherIfNotExists(Publisher publisher) {
        Publisher criteria = new Publisher();
        criteria.name = publisher.name;

        Publisher publisherDb = publisherRepository.getPublisherByCriteria(criteria);

        long publisherId;
        if (publisherDb != null) {
            publisherId = publisherDb.id;
        } else {
            publisherRepository.savePublisher(publisher);
            publisherId = publisher.id;
        }

        return publisherId;
    }

    /**
     * Delete the previous BookAuthor relationships of a book and create the new
     * ones.
     *
     * @param bookInfo BookInfo.
     */
    private void updateBookAuthorList(BookInfo bookInfo) {
        bookAuthorRepository.deleteBookAuthorListByBook(bookInfo.id);
        for (Author author : bookInfo.authors) {
            Author criteria = new Author();
            criteria.name = author.name;

            Author authorDb = authorRepository.getAuthorByCriteria(criteria);

            long authorId;
            if (authorDb != null) {
                authorId = authorDb.id;
            } else {
                authorRepository.saveAuthor(author);
                authorId = author.id;
            }

            BookAuthor bookAuthor = new BookAuthor();
            bookAuthor.authorId = authorId;
            bookAuthor.bookId = bookInfo.id;

            bookAuthorRepository.saveBookAuthor(bookAuthor);
        }

        authorRepository.deleteAuthorsWithoutBooks();
    }

    /**
     * Delete the previous BookCategory relationships of a book and create the
     * new ones.
     *
     * @param bookInfo BookInfo.
     */
    private void updateBookCategoryList(BookInfo bookInfo) {
        bookCategoryRepository.deleteBookCategoryListByBook(bookInfo.id);
        for (Category category : bookInfo.categories) {

            Category criteria = new Category();
            criteria.name = category.name;

            Category categoryDb = categoryRepository.getCategoryByCriteria(criteria);

            long categoryId;
            if (categoryDb != null) {
                categoryId = categoryDb.id;
            } else {
                categoryRepository.saveCategory(category);
                categoryId = category.id;
            }

            BookCategory bookCategory = new BookCategory();
            bookCategory.bookId = bookInfo.id;
            bookCategory.categoryId = categoryId;

            bookCategoryRepository.saveBookCategory(bookCategory);
        }

        categoryRepository.deleteCategoriesWithoutBooks();
    }
}
