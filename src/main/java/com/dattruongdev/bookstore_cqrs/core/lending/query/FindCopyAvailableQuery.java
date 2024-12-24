package com.dattruongdev.bookstore_cqrs.core.lending.query;

import com.dattruongdev.bookstore_cqrs.core.lending.domain.Copy;
import com.dattruongdev.bookstore_cqrs.core.lending.domain.CopyRepository;
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

@HandledBy(handler = FindCopyAvailableQueryHandler.class)
public record FindCopyAvailableQuery(boolean isAvailable) implements Query<ResponseEntity<IResponse>> {
}

@Service
@RequiredArgsConstructor
class FindCopyAvailableQueryHandler implements QueryHandler<FindCopyAvailableQuery, ResponseEntity<IResponse>> {
    private final CopyRepository copyRepository;

    public ResponseEntity<IResponse> handle(FindCopyAvailableQuery query) {
        List<Copy> copies = copyRepository.findByAvailable(query.isAvailable());
        if (copies.isEmpty()) {
            return ResponseEntity.status(404).body(new ErrorResponse(404, "No available copy found"));
        }

        return ResponseEntity.ok().body(new ApiResponse(200, "Copies found", copies));
    }
}