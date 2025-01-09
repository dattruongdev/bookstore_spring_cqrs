package com.dattruongdev.bookstore_cqrs.core.catalog.domain;

import com.dattruongdev.bookstore_cqrs.response.IResponse;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface BookRepository extends MongoRepository<Book, ObjectId> {
    @Aggregation(pipeline = {
            "{ $match: {isFeatured: true} }",
            "{ $lookup: { from: 'copy', let: {book_id:  $_id} , pipeline: [{ $match: { $expr:  { $and: [{ $eq:  [$$book_id, $bookId]}, {$eq:  [$available, true]}] }} }], as: 'copiesList' } }",
            // addFields copiesCount with size of $copiesList and project for copiesCount
            "{ $addFields: { numberOfCopies: { $size: '$copiesList' } } }",
            "{ $skip: ?0 }",
            "{ $limit: ?1 }"
    })
    List<Book> findIsFeaturedBooks(int page, int size);
    @Query(value = "{ '$and': [ {'category': {'$in': ?0}}, {'authors': {'$in': ?1}} ] }")
    Page<Book> findBooksByCategoriesAndAuthorsInPage(List<Category> categories, List<Author> authors, Pageable pageable);

    Page<Book> findByOrderByPublishedDateDesc(Pageable pageable);

    @Aggregation(pipeline = {
            "{ $lookup: { from: 'copy', let: {book_id:  $_id} , pipeline: [{ $match: { $expr:  { $and: [{ $eq:  [$$book_id, $bookId]}, {$eq:  [$available, true]}] }} }], as: 'copiesList' } }",
            // addFields copiesCount with size of $copiesList and project for copiesCount
            "{ $addFields: { numberOfCopies: { $size: '$copiesList' } } }",
            "{ $match: { rating: { $gte: 4.0, $lte: 5.0 } } }",
            "{ $sort: { rating: -1 } }",
            "{ $skip: ?0 }",
            "{ $limit: ?1 }"
    })
    List<Book> findByOrderByRatingDesc(int page, int size);

    @Query(value ="{'categories':  {'$in': ?0}, '_id': {'$ne': ?1}}")
    Page<Book> findBySameCategoriesNotBookIdInPage(List<ObjectId> categories, String bookId, Pageable pageable);

    @Aggregation(pipeline = {
            "{ $lookup: { from: 'copy', let: {book_id:  $_id} , pipeline: [{ $match: { $expr:  { $and: [{ $eq:  [$$book_id, $bookId]}, {$eq:  [$available, true]}] }} }], as: 'copiesList' } }",
            // addFields copiesCount with size of $copiesList and project for copiesCount
            "{ $addFields: { numberOfCopies: { $size: '$copiesList' } } }",
            "{$skip: ?0}",
            "{$limit: ?1}"
    })
    List<Book> findBooksAvailable(int page, int size);
    @Aggregation(pipeline = {
            "{ $lookup: { from: 'copy', let: {book_id:  $_id} , pipeline: [{ $match: { $expr:  { $and: [{ $eq:  [$$book_id, $bookId]}, {$eq:  [$available, true]}] }} }], as: 'copiesList' } }",
            // addFields copiesCount with size of $copiesList and project for copiesCount
            "{ $addFields: { copiesCount: { $size: '$copiesList' } } }",
            "{ $project: { _id: 0, copiesCount: 1 } }"
    })
    List<Integer> findCountCopiesAvailable();

    // findByFilter

//                org.springframework.data.mongodb.core.aggregation.Aggregation.lookup("bookPricing", "bookPricing", "_id", "bookPricing"),
//                        org.springframework.data.mongodb.core.aggregation.Aggregation.unwind("bookPricing"),
//                        org.springframework.data.mongodb.core.aggregation.Aggregation.lookup("category", "categories", "_id", "categories"),
//                        org.springframework.data.mongodb.core.aggregation.Aggregation.lookup("author", "authors", "_id", "authors"),
//                        org.springframework.data.mongodb.core.aggregation.Aggregation.match((new Criteria().orOperator(
//            Criteria.where("authors._id").in(query.authorIds().stream().map(ObjectId::new).toList()),
//            Criteria.where("categories._id").in(query.categoryIds().stream().map(ObjectId::new).toList())
//            ))),
//////                // in price range
//            org.springframework.data.mongodb.core.aggregation.Aggregation.match(Criteria.where("bookPricing.cost.amount").gte(query.priceRange().get(0)).lte(query.priceRange().get(1))),
//            org.springframework.data.mongodb.core.aggregation.Aggregation.match(Criteria.where("rating").lte(query.rating()))

    @Aggregation(pipeline = {
            "{ $match: {$or: [{authors: {$in:  ?1}}, {categories:  {$in:  ?0}}]}  }",
            "{ $match: { rating: { $lte: ?4 } } }",
            "{ $lookup: { from: 'bookPricing', localField: 'bookPricing', foreignField: '_id', as: 'price' } }",
            "{ $unwind: '$price' }",
            "{ $match: { 'price.cost.amount': { $gte: ?2, $lte: ?3 } } }",
            "{$addFields: { totalBooks: { $size: _id } } }",
    })
    List<Book> findByFilter(List<ObjectId> categoryIds, List<ObjectId> authorIds, double minPrice, double maxPrice, double rating);
}
