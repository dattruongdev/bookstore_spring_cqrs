package com.dattruongdev.bookstore_cqrs.core.catalog.query;

import com.dattruongdev.bookstore_cqrs.core.catalog.domain.Author;
import com.dattruongdev.bookstore_cqrs.core.catalog.domain.AuthorRepository;
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
import java.util.Map;

@HandledBy(handler = FindAuthorsQueryHandler.class)
public record FindAuthorsQuery(int page, int size) implements Query<ResponseEntity<IResponse>> {
}

@Service
@RequiredArgsConstructor
class FindAuthorsQueryHandler implements QueryHandler<FindAuthorsQuery, ResponseEntity<IResponse>>{
    private final AuthorRepository authorRepository;
    @Override
    public ResponseEntity<IResponse> handle(FindAuthorsQuery query) {
        List<Author> authors = authorRepository.findAll(PageRequest.of(query.page(), query.size())).toList();

        if (authors.isEmpty()) {
            return ResponseEntity.status(404).body(new ErrorResponse(404, "No authors found"));
        }

        return ResponseEntity.ok().body(new ApiResponse(Map.of(
                "status", 200,
                "message", "Authors found",
                "data", authors,
                "count", authors.size(),
                "totalAuthors", authorRepository.count()
        )));
    }
}