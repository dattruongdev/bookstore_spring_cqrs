package com.dattruongdev.bookstore_cqrs.core.catalog.domain;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CopyRepository extends MongoRepository<Copy, String> {
    List<Copy> findByAvailable(boolean available);
    List<Copy> findByBookId(String bookId);

    @Aggregation(pipeline = {
            "{ $lookup: { from: 'book', localField: '', foreignField: 'book._id', as: 'copies' } }",
            "{ $match: { 'copies.available': true } }",
    })
    List<Copy> findBooksInIdListAndCopiesAvailable(List<ObjectId> bookIds);
    List<Copy> findCopiesAvailableByBookId(ObjectId bookId);
    // findCount copies available
    @Aggregation(pipeline = {
            "{ $match: { 'bookId': ?0, 'available': true } }",
            "{ $count: 'available' }"
    })
    Integer findCountCopiesAvailableByBookId(ObjectId bookId);

}
