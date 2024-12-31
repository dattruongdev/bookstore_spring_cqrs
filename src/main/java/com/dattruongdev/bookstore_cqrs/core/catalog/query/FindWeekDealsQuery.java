package com.dattruongdev.bookstore_cqrs.core.catalog.query;

import com.dattruongdev.bookstore_cqrs.core.catalog.domain.Author;
import com.dattruongdev.bookstore_cqrs.core.catalog.domain.Book;
import com.dattruongdev.bookstore_cqrs.core.catalog.domain.BookPricing;
import com.dattruongdev.bookstore_cqrs.core.catalog.domain.BookRepository;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.HandledBy;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.query.Query;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.query.QueryHandler;
import com.dattruongdev.bookstore_cqrs.response.ApiResponse;
import com.dattruongdev.bookstore_cqrs.response.IResponse;
import com.mongodb.client.model.UnwindOptions;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
//import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@HandledBy(handler = FindWeekDealsQueryHandler.class)
public record FindWeekDealsQuery() implements Query<ResponseEntity<IResponse>> {
}

@Service
@RequiredArgsConstructor
class FindWeekDealsQueryHandler implements QueryHandler<FindWeekDealsQuery, ResponseEntity<IResponse>>{
    private final BookRepository bookRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public ResponseEntity<IResponse> handle(FindWeekDealsQuery query) {
        LookupOperation authorsLookupOperation = LookupOperation.newLookup()
                .from("author")
                .localField("authors")
                .foreignField("_id")
                .as("authors");

        LookupOperation categoriesLookupOperation = LookupOperation.newLookup()
                .from("category")
                .localField("categories")
                .foreignField("_id")
                .as("categories");

        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("bookPricing")
                .localField("bookPricing")
                .foreignField("_id")
                .as("bookPricing");

        Aggregation aggregation = Aggregation.newAggregation(
                authorsLookupOperation,
                categoriesLookupOperation,
                lookupOperation,
                Aggregation.match(Criteria.where("bookPricing.isWeekDeal").is(true)),
                Aggregation.unwind("bookPricing")
        );

        AggregationResults<Book> results = mongoTemplate.aggregate(aggregation, "book", Book.class);

        var docs = results.getRawResults().get("results", ArrayList.class);

        // Mapping here is not working "bookPricing" is null in mappedresults but has value in rawresults
//        List<Book> books = results.getMappedResults();

        Map<String, Object> response = Map.of(
                "statusCode", 200,
                "message", "Books found",
                "data", docs
        );

        return ResponseEntity.ok().body(new ApiResponse(response));
    }
}
