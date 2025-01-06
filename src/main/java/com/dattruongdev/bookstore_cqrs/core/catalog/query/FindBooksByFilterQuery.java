package com.dattruongdev.bookstore_cqrs.core.catalog.query;

import com.dattruongdev.bookstore_cqrs.core.catalog.domain.Book;
import com.dattruongdev.bookstore_cqrs.core.catalog.domain.BookRepository;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.HandledBy;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.query.Query;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.query.QueryHandler;
import com.dattruongdev.bookstore_cqrs.response.ApiResponse;
import com.dattruongdev.bookstore_cqrs.response.ErrorResponse;
import com.dattruongdev.bookstore_cqrs.response.IResponse;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@HandledBy(handler = FindBooksByFilterQueryHandler.class)
public record FindBooksByFilterQuery(List<String> categoryIds, List<String> authorIds, List<Double> priceRange, double rating) implements Query<ResponseEntity<IResponse>> {
}

@Service
@RequiredArgsConstructor
class FindBooksByFilterQueryHandler implements QueryHandler<FindBooksByFilterQuery, ResponseEntity<IResponse>> {
    private final MongoTemplate mongoTemplate;

    @Override
    public ResponseEntity<IResponse> handle(FindBooksByFilterQuery query) {
        Aggregation aggregation = Aggregation.newAggregation(

                Aggregation.lookup("bookPricing", "bookPricing", "_id", "bookPricing"),
                Aggregation.unwind("bookPricing"),
                Aggregation.lookup("category", "categories", "_id", "categories"),
                Aggregation.lookup("author", "authors", "_id", "authors"),
                Aggregation.match((new Criteria().orOperator(
                        Criteria.where("authors._id").in(query.authorIds().stream().map(ObjectId::new).toList()),
                        Criteria.where("categories._id").in(query.categoryIds().stream().map(ObjectId::new).toList())
                )))
////                // in price range
//                Aggregation.match(Criteria.where("bookPricing.cost.amount").gte(query.priceRange().get(0)).lte(query.priceRange().get(1)))
//                Aggregation.match(Criteria.where("rating").lte(query.rating()))
        );

         AggregationResults<Book> results = mongoTemplate.aggregate(aggregation, "book", Book.class);

         if (results.getMappedResults().isEmpty()) {
             return ResponseEntity.status(404).body(new ErrorResponse(404, "No books found"));
         }

         var docs =  results.getRawResults().get("results", ArrayList.class);

            return ResponseEntity.ok().body(new ApiResponse(Map.of(
                    "status", 200,
                    "message", "Books found",
                    "data", docs,
                    "totalBooks", docs.size(),
                    "count", docs.size()
            )));
    }
}