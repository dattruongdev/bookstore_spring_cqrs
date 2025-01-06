package com.dattruongdev.bookstore_cqrs.core.catalog.domain;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ReviewRepository extends MongoRepository<Review, ObjectId> {
    Page<Review> findByBookId(ObjectId bookId, Pageable pageable);
    Page<Review> findByBookIdOrderByCreatedAtDesc(ObjectId bookId, Pageable pageable);
    @Query(value = "{bookId: ?0}", count = true)
    long countByBookId(ObjectId bookId);
}
