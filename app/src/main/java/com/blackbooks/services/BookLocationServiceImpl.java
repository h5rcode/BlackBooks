package com.blackbooks.services;

import com.blackbooks.model.persistent.BookLocation;
import com.blackbooks.repositories.BookLocationRepository;

import java.util.List;

/**
 * Book location services.
 */
public final class BookLocationServiceImpl implements BookLocationService {

    private final BookLocationRepository bookLocationRepository;

    public BookLocationServiceImpl(BookLocationRepository bookLocationRepository) {
        this.bookLocationRepository = bookLocationRepository;
    }

    /**
     * Delete a book location.
     *
     * @param bookLocationId Id of the book location.
     */
    public void deleteBookLocation(long bookLocationId) {
        bookLocationRepository.deleteBookLocation(bookLocationId);
    }

    /**
     * Get a book location.
     *
     * @param bookLocationId Id of a book location.
     * @return BookLocation.
     */
    public BookLocation getBookLocation(Long bookLocationId) {
        return bookLocationRepository.getBookLocation(bookLocationId);
    }

    /**
     * Get the one row matching a criteria. If no rows or more that one rows
     * match the criteria, the method returns null.
     *
     * @param criteria The search criteria.
     * @return Author.
     */
    public BookLocation getBookLocationByCriteria(BookLocation criteria) {
        return bookLocationRepository.getBookLocationByCriteria(criteria);
    }

    /**
     * Get the list of book locations whose name contains a given text.
     *
     * @param text Text.
     * @return List of BookLocation.
     */
    public List<BookLocation> getBookLocationListByText(String text) {
        return bookLocationRepository.getBookLocationListByText(text);
    }

    /**
     * Update a book location.
     *
     * @param bookLocationId Id of the book location.
     * @param newName        New name.
     */
    public void updateBookLocation(long bookLocationId, String newName) {
        bookLocationRepository.updateBookLocation(bookLocationId, newName);
    }
}
