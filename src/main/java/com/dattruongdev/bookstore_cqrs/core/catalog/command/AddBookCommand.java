package com.dattruongdev.bookstore_cqrs.core.catalog.command;

import com.dattruongdev.bookstore_cqrs.core.catalog.domain.Book;
import com.dattruongdev.bookstore_cqrs.core.catalog.domain.BookRepository;
import com.dattruongdev.bookstore_cqrs.core.lending.domain.Copy;
import com.dattruongdev.bookstore_cqrs.core.lending.domain.CopyRepository;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.HandledBy;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.command.Command;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.command.CommandHandler;
import com.dattruongdev.bookstore_cqrs.response.ApiResponse;
import com.dattruongdev.bookstore_cqrs.response.IResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

@HandledBy(handler = AddBookCommandHandler.class)
public record AddBookCommand(String title, String edition, String author, String publisher, String source, double cost) implements Command<ResponseEntity<IResponse>> {

}

@RequiredArgsConstructor
class AddBookCommandHandler implements CommandHandler<AddBookCommand, ResponseEntity<IResponse>> {
    private final BookRepository bookRepository;
    private final CopyRepository copyRepository;

    @Override
    public ResponseEntity<IResponse> handle(AddBookCommand command) {
        Book book = new Book();
        book.setAuthor(command.author());
        book.setCost(command.cost());
        book.setEdition(command.edition());
        book.setSource(command.source());

        book.setPublisher(command.publisher());
        book.setTitle(command.title());
        book = bookRepository.save(book);

        for (int i = 0; i < 3; i++) {
            Copy copy = new Copy();
            copy.setAvailable(true);
            copy.setIsbn(book.getId());
            copyRepository.save(copy);
        }

        return ResponseEntity.ok().body(new ApiResponse(200, "Book added successfully", null));
    }
}