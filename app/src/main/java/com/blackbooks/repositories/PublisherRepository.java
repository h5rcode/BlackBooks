package com.blackbooks.repositories;

import com.blackbooks.model.persistent.Publisher;

import java.util.List;

public interface PublisherRepository {
    void deletePublisherWithoutBooks();

    Publisher getPublisher(long publisherId);

    Publisher getPublisherByCriteria(Publisher criteria);

    List<Publisher> getPublisherListByText(String text);

    long savePublisher(Publisher publisher);

    void deletePublishersWithoutBooks();
}
