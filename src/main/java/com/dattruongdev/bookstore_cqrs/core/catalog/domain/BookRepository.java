package com.dattruongdev.bookstore_cqrs.core.catalog.domain;

import com.dattruongdev.bookstore_cqrs.response.IResponse;
import org.bson.types.ObjectId;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface BookRepository extends MongoRepository<Book, ObjectId> {
    @Query(value = "{ 'isFeatured' : true }")
    Page<Book> findIsFeaturedBooks(Pageable pageable);
    @Query(value = "{ '$and': [ {'category': {'$in': ?0}}, {'authors': {'$in': ?1}} ] }")
    Page<Book> findBooksByCategoriesAndAuthorsInPage(List<Category> categories, List<Author> authors, Pageable pageable);

    Page<Book> findByOrderByPublishedDateDesc(Pageable pageable);

    @Query(value = "{'rating': {'$gte':  4.0, '$lte':  5.0}}")
    Page<Book> findByOrderByRatingDesc(PageRequest pageable);

    @Query(value ="{'categories':  {'$in': ?0}, '_id': {'$ne': ?1}}")
    Page<Book> findBySameCategoriesNotBookIdInPage(List<ObjectId> categories, String bookId, Pageable pageable);

}
