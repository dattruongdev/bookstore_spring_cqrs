package com.dattruongdev.bookstore_cqrs.core.lending.command;

import com.dattruongdev.bookstore_cqrs.core.catalog.query.FindCopyAvailableQuery;
import com.dattruongdev.bookstore_cqrs.core.lending.domain.Borrow;
import com.dattruongdev.bookstore_cqrs.core.lending.domain.BorrowRepository;
import com.dattruongdev.bookstore_cqrs.core.lending.domain.Copy;
import com.dattruongdev.bookstore_cqrs.core.lending.domain.CopyRepository;
import com.dattruongdev.bookstore_cqrs.cqrs.DispatchableProcessor;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.DispatchableHandler;
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

public record LendBookCommand(String userId) implements Command<ResponseEntity<IResponse>> {
}

@Service
@RequiredArgsConstructor
class LendBookCommandHandler implements CommandHandler<LendBookCommand, ResponseEntity<IResponse>> {
    private final BorrowRepository borrowRepository;
    private final CopyRepository copyRepository;

    @Transactional
    public ResponseEntity<IResponse> handle(LendBookCommand command) {
        List<Copy> copies = copyRepository.findAvailableCopies();
        if (copies.isEmpty()) {
            return ResponseEntity.status(404).body(new ErrorResponse(404, "No available copies"));
        }
        Copy copy = copies.getFirst();
        copy.makeUnavailable();

        Borrow borrow = new Borrow();
        borrow.makeBorrow(copy.getId(), command.userId());

        borrowRepository.save(borrow);
        copyRepository.save(copy);

        return ResponseEntity.ok().body(new ApiResponse(201, "Book lent successfully", null));
    }
}
