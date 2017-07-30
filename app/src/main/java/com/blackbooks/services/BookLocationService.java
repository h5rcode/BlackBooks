package com.blackbooks.services;

import com.blackbooks.model.persistent.BookLocation;

import java.util.List;

public interface BookLocationService {
    void deleteBookLocation(long bookLocationId);

    BookLocation getBookLocation(Long bookLocationId);

    BookLocation getBookLocationByCriteria(BookLocation criteria);

    List<BookLocation> getBookLocationListByText(String text);

    void updateBookLocation(long bookLocationId, String newName);
}
