package com.dattruongdev.bookstore_cqrs.core.catalog.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface BookRepository extends MongoRepository<Book, String> {
    @Query("{ 'isFeatured' : true }")
    Page<Book> findIsFeaturedBooks(Pageable pageable);
}
