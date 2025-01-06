package com.dattruongdev.bookstore_cqrs.core.catalog.domain;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface AuthorRepository extends MongoRepository<Author, String> {
    // find author by name in array of names
    @Query("{ 'fullName' : { $in: ?0 } }")
    List<Author> findByFullNameIn(List<String> names);
}
