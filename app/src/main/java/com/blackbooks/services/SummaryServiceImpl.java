package com.blackbooks.services;

import com.blackbooks.model.nonpersistent.Summary;
import com.blackbooks.repositories.AuthorRepository;
import com.blackbooks.repositories.BookLocationRepository;
import com.blackbooks.repositories.BookRepository;
import com.blackbooks.repositories.CategoryRepository;
import com.blackbooks.repositories.SeriesRepository;

/**
 * Summary services.
 */
public final class SummaryServiceImpl implements SummaryService {

    private final AuthorRepository authorRepository;
    private final BookLocationRepository bookLocationRepository;
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final SeriesRepository seriesRepository;

    public SummaryServiceImpl(
            AuthorRepository authorRepository,
            BookLocationRepository bookLocationRepository,
            BookRepository bookRepository,
            CategoryRepository categoryRepository,
            SeriesRepository seriesRepository) {
        this.authorRepository = authorRepository;
        this.bookLocationRepository = bookLocationRepository;
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.seriesRepository = seriesRepository;
    }

    public Summary getSummary() {
        Summary summary = new Summary();

        summary.books = bookRepository.getBookCount();
        summary.authors = authorRepository.getAuthorCount();
        summary.categories = categoryRepository.getCategoryCount();
        summary.languages = bookRepository.getLanguageCount();
        summary.series = seriesRepository.getSeriesCount();
        summary.bookLocations = bookLocationRepository.getBookLocationCount();
        summary.toRead = bookRepository.getBookToReadCount();
        summary.loans = bookRepository.getBookLoanCount();
        summary.favourites = bookRepository.getFavouriteBooks();

        return summary;
    }

    /**
     * Return the number of authors in the library.
     *
     * @return Author count.
     */
    public int getFirstLetterCount() {
        return bookRepository.getFirstLetterCount();
    }
}
