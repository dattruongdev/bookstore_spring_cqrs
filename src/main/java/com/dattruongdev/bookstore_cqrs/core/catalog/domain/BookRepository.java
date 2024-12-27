package com.dattruongdev.bookstore_cqrs.core.catalog.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Map;

public interface BookRepository extends MongoRepository<Book, String> {
//    @Query(value = "{ 'isFeatured' : true }")
//    Page<Book> findIsFeaturedBooks(Pageable pageable);
//    @Query(value = "{ '$and': [ {'category': {'$in': ?0}}, {'authors': {'$in': ?1}} ] }")
//    Page<Book> findBooksByCategoriesAndAuthorsInPage(List<Category> categories, List<Author> authors, Pageable pageable);
//
//    Page<Book> findByOrderByPublishedDateDesc(Pageable pageable);
}
