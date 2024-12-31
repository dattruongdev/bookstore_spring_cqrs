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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@HandledBy(handler = FindByReadersPickQueryHandler.class)
public record FindByReadersPickQuery() implements Query<ResponseEntity<IResponse>> {
}

@Service
@RequiredArgsConstructor
class FindByReadersPickQueryHandler implements QueryHandler<FindByReadersPickQuery, ResponseEntity<IResponse>> {
    private final BookRepository bookRepository;

    @Override
    public ResponseEntity<IResponse> handle(FindByReadersPickQuery query) {
        List<Book> books = bookRepository.findByOrderByRatingDesc(PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "rating"))).toList();

        if (books.isEmpty()) {
            return ResponseEntity.status(404).body(new ErrorResponse(404, "Books not found"));
        }

        Map<String, Object> response = Map.of(
                "statusCode", 200,
                "message", "Books found",
                "data", books
        );
        return ResponseEntity.ok().body(new ApiResponse(response));
    }
}