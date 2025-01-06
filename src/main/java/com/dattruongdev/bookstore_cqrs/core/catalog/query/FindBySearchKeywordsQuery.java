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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@HandledBy(handler = FindBySearchKeywordsQueryHandler.class)
public record FindBySearchKeywordsQuery(String title , String author , String year) implements Query<ResponseEntity<IResponse>> {
}

@Service
@RequiredArgsConstructor
class FindBySearchKeywordsQueryHandler implements QueryHandler<FindBySearchKeywordsQuery, ResponseEntity<IResponse>> {
    private final MongoTemplate mongoTemplate;
    @Override
    public ResponseEntity<IResponse> handle(FindBySearchKeywordsQuery query) {
        String title = query.title().isEmpty() ? "asdlkfjasaskldfjlkasd" : query.title();
        String author = query.author().isEmpty() ? "asdlkfjasaskldfjlkasd" : query.author();
        String year = query.year().isEmpty() ? "asdlkfjasaskldfjlkasd" : query.year();
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.lookup("bookPricing", "bookPricing", "_id", "bookPricing"),
                Aggregation.unwind("bookPricing"),
                Aggregation.lookup("category", "categories", "_id", "categories"),
                Aggregation.lookup("author", "authors", "_id", "authors"),
                Aggregation.match(new Criteria().orOperator(
                        Criteria.where("title").regex(".*" + title + ".*", "i"),
                        Criteria.where("authors.fullName").regex(".*" + author + ".*", "i"),
                        Criteria.where("publishedDate").regex(".*" + year + ".*", "i")
                ))
        );

        AggregationResults<Book> docs =  mongoTemplate.aggregate(aggregation, "book", Book.class);
        if (docs.getMappedResults().isEmpty()) {
            return ResponseEntity.status(404).body(new ErrorResponse(404, "No books found"));
        }

        return ResponseEntity.ok().body(new ApiResponse(Map.of(
                "status", 200,
                "data", docs.getRawResults().get("results", ArrayList.class),
                "totalBooks", docs.getMappedResults().size(),
                "message", "Books found"
        )));
    }
}
