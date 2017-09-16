package com.blackbooks.services.search;

import com.blackbooks.model.nonpersistent.BookInfo;

import java.io.IOException;

public interface BookOnlineSearchService {

    BookInfo search(String isbn) throws IOException, InterruptedException;
}
