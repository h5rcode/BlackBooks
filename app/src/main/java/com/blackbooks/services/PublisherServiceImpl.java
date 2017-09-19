package com.blackbooks.services;

import com.blackbooks.model.persistent.Publisher;
import com.blackbooks.repositories.PublisherRepository;

import java.util.List;

/**
 * Publisher services.
 */
public final class PublisherServiceImpl implements PublisherService {

    private final PublisherRepository publisherRepository;

    public PublisherServiceImpl(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    /**
     * Delete the publishers that are not referred by any books in the database.
     */
    public void deletePublishersWithoutBooks() {
        publisherRepository.deletePublisherWithoutBooks();
    }

    /**
     * Get a publisher.
     *
     * @param publisherId Id of the publisher.
     * @return Publisher.
     */
    public Publisher getPublisher(long publisherId) {
        return publisherRepository.getPublisher(publisherId);
    }

    /**
     * Get the one row matching a criteria. If no rows or more that one rows
     * match the criteria, the method returns null.
     *
     * @param criteria The search criteria.
     * @return Author.
     */
    public Publisher getPublisherByCriteria(Publisher criteria) {
        return publisherRepository.getPublisherByCriteria(criteria);
    }

    /**
     * Get the list of publishers whose name contains a given text.
     *
     * @param text Text.
     * @return List of Publisher.
     */
    public List<Publisher> getPublisherListByText(String text) {
        return publisherRepository.getPublisherListByText(text);
    }

    /**
     * Save an publisher in the database.
     *
     * @param publisher Publisher.
     * @return Id of the saved publisher.
     */
    public long savePublisher(Publisher publisher) {
        return publisherRepository.savePublisher(publisher);
    }
}
