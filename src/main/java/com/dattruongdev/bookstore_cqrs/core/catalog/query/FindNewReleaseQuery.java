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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@HandledBy(handler = FindNewReleaseQueryHandler.class)
public record FindNewReleaseQuery() implements Query<ResponseEntity<IResponse>> {
}

@Service
@RequiredArgsConstructor
class FindNewReleaseQueryHandler implements QueryHandler<FindNewReleaseQuery, ResponseEntity<IResponse>> {
    private final MongoTemplate mongoTemplate;

    @Override
    public ResponseEntity<IResponse> handle(FindNewReleaseQuery query) {

        LookupOperation lookupOperation = LookupOperation.newLookup()
            .from("bookCost")
            .localField("_id")
            .foreignField("bookId")
            .as("cost");

        Aggregation aggregation = Aggregation.newAggregation(
            lookupOperation,
            Aggregation.sort(Sort.Direction.DESC, "publishedDate"),
            Aggregation.limit(6)
        );

        List<Book> books = mongoTemplate.aggregate(aggregation, "book", Book.class).getMappedResults();

        if (books.isEmpty()) {
            return ResponseEntity.status(404).body(new ErrorResponse(404, "No books found"));
        }

        Map<String, Object> response = Map.of(
            "statusCode", 200,
            "message", "Books found",
            "data", books
        );

        return ResponseEntity.ok().body(new ApiResponse(response));
    }
}