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

@HandledBy(handler = FindByIsbnQueryHandler.class)
public record FindCopyByIsbnQuery(String Isbn) implements Query<ResponseEntity<IResponse>> {
}

@Service
@RequiredArgsConstructor
class FindByIsbnQueryHandler implements QueryHandler<FindCopyByIsbnQuery, ResponseEntity<IResponse>> {
    private final CopyRepository copyRepository;
    @Override
    public ResponseEntity<IResponse> handle(FindCopyByIsbnQuery command) {
        List<Copy> copies = copyRepository.findByIsbn(command.Isbn());

        if (copies.isEmpty()) {
            return ResponseEntity.status(404).body(new ErrorResponse(404, "No copies found"));
        }

        return ResponseEntity.ok().body(new ApiResponse(200, "Copies found", copies));
    }
}
