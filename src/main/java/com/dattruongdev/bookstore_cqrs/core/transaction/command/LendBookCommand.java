package com.dattruongdev.bookstore_cqrs.core.transaction.command;

import com.dattruongdev.bookstore_cqrs.core.transaction.domain.Borrow;
import com.dattruongdev.bookstore_cqrs.core.transaction.domain.BorrowRepository;
import com.dattruongdev.bookstore_cqrs.core.catalog.domain.Copy;
import com.dattruongdev.bookstore_cqrs.core.catalog.domain.CopyRepository;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.command.Command;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.command.CommandHandler;
import com.dattruongdev.bookstore_cqrs.response.ApiResponse;
import com.dattruongdev.bookstore_cqrs.response.ErrorResponse;
import com.dattruongdev.bookstore_cqrs.response.IResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public record LendBookCommand(String userId) implements Command<ResponseEntity<IResponse>> {
}

@Service
@RequiredArgsConstructor
class LendBookCommandHandler implements CommandHandler<LendBookCommand, ResponseEntity<IResponse>> {
    private final BorrowRepository borrowRepository;
    private final CopyRepository copyRepository;

    @Transactional
    public ResponseEntity<IResponse> handle(LendBookCommand command) {
        List<Copy> copies = copyRepository.findByAvailable(true);
        if (copies.isEmpty()) {
            return ResponseEntity.status(404).body(new ErrorResponse(404, "No available copies"));
        }
        Copy copy = copies.getFirst();
        copy.makeUnavailable();

        Borrow borrow = new Borrow();
        borrow.makeBorrow(copy.getId().toString(), command.userId());

        borrowRepository.save(borrow);
        copyRepository.save(copy);

        return ResponseEntity.ok().body(new ApiResponse(Map.of(
            "statusCode", 200,
            "message", "Book lent",
            "data", borrow
        )));
    }
}
