package com.dattruongdev.bookstore_cqrs.core.catalog.query;

import com.dattruongdev.bookstore_cqrs.core.catalog.domain.Book;
import com.dattruongdev.bookstore_cqrs.core.catalog.domain.BookRepository;
import com.dattruongdev.bookstore_cqrs.core.lending.query.FindCopyAvailableQuery;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.HandledBy;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.query.Query;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.query.QueryHandler;
import com.dattruongdev.bookstore_cqrs.response.ApiResponse;
import com.dattruongdev.bookstore_cqrs.response.ErrorResponse;
import com.dattruongdev.bookstore_cqrs.response.IResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@HandledBy(handler = FindAllBooksQueryHandler.class)
public record FindAllBooksQuery() implements Query<ResponseEntity<IResponse>> {
}

@Service
@RequiredArgsConstructor
class FindAllBooksQueryHandler implements QueryHandler<FindAllBooksQuery, ResponseEntity<IResponse>> {
    private final MongoTemplate mongoTemplate;

    @Override
    public ResponseEntity<IResponse> handle(FindAllBooksQuery dispatchable) {
        LookupOperation costLookupOperation = LookupOperation.newLookup()
                .from("bookCost")
                .localField("_id")
                .foreignField("bookId")
                .as("cost");
        LookupOperation authorsLookupOperation = LookupOperation.newLookup()
                .from("author")
                .localField("authors")
                .foreignField("_id")
                .as("authors");
        Aggregation aggregation = Aggregation.newAggregation(
                authorsLookupOperation,
                costLookupOperation
        );

        List<Book> books = mongoTemplate.aggregate(aggregation, "book", Book.class).getMappedResults();

        if (books.isEmpty()) {
            return ResponseEntity.status(404).body(new ErrorResponse(404, "No books found"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 200);
        response.put("message", "Books found");
        response.put("data", books);


        return ResponseEntity.ok().body(new ApiResponse(response));
    }
}
