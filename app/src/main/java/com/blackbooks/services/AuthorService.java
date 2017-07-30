package com.blackbooks.services;

import com.blackbooks.model.persistent.Author;

import java.util.List;

public interface AuthorService {
    void deleteAuthor(long id);

    Author getAuthor(long id);

    Author getAuthorByCriteria(Author criteria);

    List<Author> getAuthorListByText(String text);

    void updateAuthor(long authorId, String newName);
}
