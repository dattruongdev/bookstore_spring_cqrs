package com.dattruongdev.bookstore_cqrs.core.lending.command;

import com.dattruongdev.bookstore_cqrs.core.lending.domain.Borrow;
import com.dattruongdev.bookstore_cqrs.core.lending.domain.BorrowRepository;
import com.dattruongdev.bookstore_cqrs.core.lending.domain.Copy;
import com.dattruongdev.bookstore_cqrs.core.lending.domain.CopyRepository;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.HandledBy;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.command.Command;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.command.CommandHandler;
import com.dattruongdev.bookstore_cqrs.response.ApiResponse;
import com.dattruongdev.bookstore_cqrs.response.ErrorResponse;
import com.dattruongdev.bookstore_cqrs.response.IResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

@HandledBy(handler = ReturnCopyCommandHandler.class)
public record ReturnCopyCommand(String borrowId) implements Command<ResponseEntity<IResponse>> {
}

@Service
@RequiredArgsConstructor
class ReturnCopyCommandHandler implements CommandHandler<ReturnCopyCommand, ResponseEntity<IResponse>> {
    private final BorrowRepository borrowRepository;
    private final CopyRepository copyRepository;

    @Transactional
    public ResponseEntity<IResponse> handle(ReturnCopyCommand command) {
        Borrow borrow = borrowRepository.findById(command.borrowId()).orElse(null);
        if (borrow == null) {
            return ResponseEntity.status(404).body(new ErrorResponse(404, "No borrowed book"));
        }

        borrow.returnBook();
        borrow = borrowRepository.save(borrow);
        Copy copy = copyRepository.findById(borrow.getCopyId()).orElse(null);
        if (copy != null) {
            copy.makeAvailable();
            copy = copyRepository.save(copy);
        }

        return ResponseEntity.ok().body(new ApiResponse(Map.of(
            "statusCode", 200,
            "message", "Book returned",
            "data", copy
        )));
    }
}


