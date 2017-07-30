package com.blackbooks.services;

import com.blackbooks.model.persistent.Author;
import com.blackbooks.repositories.AuthorRepository;

import java.util.List;

/**
 * Author services.
 */
public final class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public void deleteAuthor(long authorId) {
        authorRepository.deleteAuthor(authorId);
    }

    public Author getAuthor(long id) {
        return authorRepository.getAuthor(id);
    }

    public Author getAuthorByCriteria(Author criteria) {
        return authorRepository.getAuthorByCriteria(criteria);
    }

    public List<Author> getAuthorListByText(String text) {
        return authorRepository.getAuthorListByText(text);
    }

    public void updateAuthor(long authorId, String newName) {
        authorRepository.updateAuthor(authorId, newName);
    }
}
