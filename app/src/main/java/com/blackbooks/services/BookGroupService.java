package com.blackbooks.services;

import com.blackbooks.model.nonpersistent.BookGroup;

import java.util.List;

public interface BookGroupService {
    List<BookGroup> getBookGroupListAuthor(int limit, int offset);

    List<BookGroup> getBookGroupListBookLocation(int limit, int offset);

    List<BookGroup> getBookGroupListCategory(int limit, int offset);

    List<BookGroup> getBookGroupListFirstLetter(int limit, int offset);

    List<BookGroup> getBookGroupListLanguage();

    List<BookGroup> getBookGroupListLoan(int limit, int offset);

    List<BookGroup> getBookGroupListSeries(int limit, int offset);
}
