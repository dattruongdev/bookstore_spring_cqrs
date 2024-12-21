package com.dattruongdev.bookstore_cqrs.core.catalog.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookRepository extends MongoRepository<Book, Isbn> {
}
