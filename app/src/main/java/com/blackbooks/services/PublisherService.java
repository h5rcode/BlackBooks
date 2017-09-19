package com.blackbooks.services;

import com.blackbooks.model.persistent.Publisher;

import java.util.List;

public interface PublisherService {

    /**
     * Delete the publishers that are not referred by any books in the database.
     */
     void deletePublishersWithoutBooks();

    /**
     * Get a publisher.
     *
     * @param publisherId Id of the publisher.
     * @return Publisher.
     */
     Publisher getPublisher(long publisherId);

    /**
     * Get the one row matching a criteria. If no rows or more that one rows
     * match the criteria, the method returns null.
     *
     * @param criteria The search criteria.
     * @return Author.
     */
     Publisher getPublisherByCriteria(Publisher criteria);

    /**
     * Get the list of publishers whose name contains a given text.
     *
     * @param text Text.
     * @return List of Publisher.
     */
     List<Publisher> getPublisherListByText(String text);

    /**
     * Save an publisher in the database.
     *
     * @param publisher Publisher.
     * @return Id of the saved publisher.
     */
     long savePublisher(Publisher publisher);
}
