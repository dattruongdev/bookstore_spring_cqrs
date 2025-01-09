package com.dattruongdev.bookstore_cqrs.core.transaction.query;

import com.dattruongdev.bookstore_cqrs.core.catalog.domain.Copy;
import com.dattruongdev.bookstore_cqrs.core.catalog.domain.CopyRepository;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.HandledBy;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.query.Query;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.query.QueryHandler;
import com.dattruongdev.bookstore_cqrs.response.ApiResponse;
import com.dattruongdev.bookstore_cqrs.response.ErrorResponse;
import com.dattruongdev.bookstore_cqrs.response.IResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@HandledBy(handler = FindByIsbnQueryHandler.class)
public record FindCopyByIsbnQuery(String Isbn) implements Query<ResponseEntity<IResponse>> {
}

@Service
@RequiredArgsConstructor
class FindByIsbnQueryHandler implements QueryHandler<FindCopyByIsbnQuery, ResponseEntity<IResponse>> {
    private final CopyRepository copyRepository;
    @Override
    public ResponseEntity<IResponse> handle(FindCopyByIsbnQuery command) {
        List<Copy> copies = copyRepository.findByBookId(command.Isbn());

        if (copies.isEmpty()) {
            return ResponseEntity.status(404).body(new ErrorResponse(404, "No copies found"));
        }

        return ResponseEntity.ok().body(new ApiResponse(Map.of(
            "statusCode", 200,
            "message", "Copies found",
            "data", copies
        )));
    }
}
