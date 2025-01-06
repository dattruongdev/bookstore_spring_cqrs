package com.dattruongdev.bookstore_cqrs.core.transaction.query;

import com.dattruongdev.bookstore_cqrs.core.transaction.domain.Borrow;
import com.dattruongdev.bookstore_cqrs.core.transaction.domain.BorrowRepository;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.query.Query;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.query.QueryHandler;
import com.dattruongdev.bookstore_cqrs.response.ApiResponse;
import com.dattruongdev.bookstore_cqrs.response.ErrorResponse;
import com.dattruongdev.bookstore_cqrs.response.IResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

public record FindBorrowByUserIdQuery(String userId) implements Query<ResponseEntity<IResponse>> {
}

@Service
@RequiredArgsConstructor
class FindByUserIdQueryHandler implements QueryHandler<FindBorrowByUserIdQuery,ResponseEntity<IResponse>> {
    private final BorrowRepository borrowRepository;
    @Override
    public ResponseEntity<IResponse> handle(FindBorrowByUserIdQuery command) {
        Borrow borrow = borrowRepository.findByUserId(command.userId());
        if (borrow == null) {
            return ResponseEntity.status(400).body(new ErrorResponse(404, "Borrow not found"));
        }

        return ResponseEntity.ok().body(new ApiResponse(Map.of(
            "statusCode", 200,
            "message", "Borrow found",
            "data", borrow
        )));
    }
}
