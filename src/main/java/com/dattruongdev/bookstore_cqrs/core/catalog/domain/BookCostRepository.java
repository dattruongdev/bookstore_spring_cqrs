package com.dattruongdev.bookstore_cqrs.core.catalog.domain;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookCostRepository extends MongoRepository<BookPricing, ObjectId> {
}
