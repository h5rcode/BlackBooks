package com.blackbooks.repositories;

import com.blackbooks.model.persistent.BookLocation;

import java.util.List;

public interface BookLocationRepository {
    void deleteBookLocation(long bookLocationId);

    void deleteBookLocationsWithoutBooks();

    BookLocation getBookLocation(Long bookLocationId);

    BookLocation getBookLocationByCriteria(BookLocation criteria);

    List<BookLocation> getBookLocationListByText(String text);

    long saveBookLocation(BookLocation bookLocation);

    void updateBookLocation(long bookLocationId, String newName);

    int getBookLocationCount();
}
