package com.dattruongdev.bookstore_cqrs.core.transaction.query;

import com.dattruongdev.bookstore_cqrs.core.transaction.domain.Borrow;
import com.dattruongdev.bookstore_cqrs.core.transaction.domain.BorrowRepository;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.query.Query;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.query.QueryHandler;
import com.dattruongdev.bookstore_cqrs.response.ApiResponse;
import com.dattruongdev.bookstore_cqrs.response.IResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

public record FindBorrowByCopyIdQuery(String copyId) implements Query<ResponseEntity<IResponse>> {
}

@Service
@RequiredArgsConstructor
class FindBorrowByCopyIdQueryHandler implements QueryHandler<FindBorrowByCopyIdQuery, ResponseEntity<IResponse>> {
    private final BorrowRepository borrowRepository;
    @Override
    public ResponseEntity<IResponse> handle(FindBorrowByCopyIdQuery query) {
        Borrow borrow = borrowRepository.findByCopyId(query.copyId());
        if (borrow == null) {
            return ResponseEntity.ok(new ApiResponse(Map.of(
                "statusCode", 404,
                "message", "No borrowed book"
            )));
        }
        return ResponseEntity.ok(new ApiResponse(Map.of(
            "statusCode", 200,
            "message", "Borrow found",
            "data", borrow
        )));
    }
}
