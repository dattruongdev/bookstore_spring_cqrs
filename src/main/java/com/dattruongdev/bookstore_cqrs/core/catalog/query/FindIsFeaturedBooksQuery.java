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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@HandledBy(handler = FindIsFeaturedBooksQueryHandler.class)
public record FindIsFeaturedBooksQuery(int size) implements Query<ResponseEntity<IResponse>> {
}

@Service
@RequiredArgsConstructor
class FindIsFeaturedBooksQueryHandler implements QueryHandler<FindIsFeaturedBooksQuery, ResponseEntity<IResponse>> {
    private final BookRepository bookRepository;
    @Override
    public ResponseEntity<IResponse> handle(FindIsFeaturedBooksQuery query) {
        List<Book> books  = bookRepository.findIsFeaturedBooks(PageRequest.of(1, query.size())).stream().toList();

        if (books.isEmpty()) {
            return ResponseEntity.status(404).body(new ErrorResponse(404, "No books found"));
        }

        return ResponseEntity.ok().body(new ApiResponse(200, "Books found", books));
    }
}