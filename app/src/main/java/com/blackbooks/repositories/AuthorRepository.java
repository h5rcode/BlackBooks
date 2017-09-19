package com.blackbooks.repositories;

import com.blackbooks.model.persistent.Author;

import java.util.List;

public interface AuthorRepository {

    void deleteAuthor(long authorId);

    List<Author> getAuthorsByIds(List<Long> authorIdList);

    void deleteAuthorsWithoutBooks();

    Author getAuthor(long id);

    Author getAuthorByCriteria(Author criteria);

    List<Author> getAuthorListByText(String text);

    long saveAuthor(Author author);

    void updateAuthor(long authorId, String newName);

    int getAuthorCount();
}
