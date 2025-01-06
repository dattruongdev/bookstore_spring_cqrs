package com.dattruongdev.bookstore_cqrs.core.transaction.command;

import com.dattruongdev.bookstore_cqrs.core.transaction.domain.Copy;
import com.dattruongdev.bookstore_cqrs.core.transaction.domain.CopyRepository;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.HandledBy;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.command.Command;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.command.CommandHandler;
import com.dattruongdev.bookstore_cqrs.response.ApiResponse;
import com.dattruongdev.bookstore_cqrs.response.IResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@HandledBy(handler = AddCopiesInQuantityCommandHandler.class)
public record AddCopiesInQuantityCommand(String Isbn, int quantity) implements Command<ResponseEntity<IResponse>> {
}

@Service
@RequiredArgsConstructor
class AddCopiesInQuantityCommandHandler implements CommandHandler<AddCopiesInQuantityCommand, ResponseEntity<IResponse>> {
    private final CopyRepository copyRepository;
    public ResponseEntity<IResponse> handle(AddCopiesInQuantityCommand command) {
        List<Copy> copies = new ArrayList<>();
        for (int i = 0; i < command.quantity(); i++) {
            Copy copy = new Copy();
            copy.setIsbn(command.Isbn());
            copy.setCreatedAt(new Date());
            copy.setUpdatedAt(new Date());

            copies.add(copy);
        }

        copyRepository.saveAll(copies);

        return ResponseEntity.ok().body(new ApiResponse(Map.of(
            "statusCode", 201,
            "message", "Copies added successfully"
        )));
    }
}
