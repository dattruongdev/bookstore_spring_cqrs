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
    private final BookRepository bookRepository;

    @Override
    public ResponseEntity<IResponse> handle(FindBooksByFilterQuery query) {
        List<Book> books = bookRepository.findByFilter(
                query.categoryIds().stream().map(ObjectId::new).toList(),
                query.authorIds().stream().map(ObjectId::new).toList(),
                query.priceRange().get(0), query.priceRange().get(1),
                query.rating());

            return ResponseEntity.ok().body(new ApiResponse(Map.of(
                    "status", 200,
                    "message", "Books found",
                    "data", books,
                    "totalBooks", books.size(),
                    "count", books.size()
            )));
    }
}