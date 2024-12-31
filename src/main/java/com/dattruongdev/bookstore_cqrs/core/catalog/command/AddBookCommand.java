package com.dattruongdev.bookstore_cqrs.core.catalog.command;

import com.dattruongdev.bookstore_cqrs.core.catalog.domain.*;
import com.dattruongdev.bookstore_cqrs.core.lending.domain.Copy;
import com.dattruongdev.bookstore_cqrs.core.lending.domain.CopyRepository;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.HandledBy;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.command.Command;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.command.CommandHandler;
import com.dattruongdev.bookstore_cqrs.response.ApiResponse;
import com.dattruongdev.bookstore_cqrs.response.IResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@HandledBy(handler = AddBookCommandHandler.class)
public record AddBookCommand(String title, List<String> authors, String publisher, Cost cost, List<Category> categories) implements Command<ResponseEntity<IResponse>> {

}

@RequiredArgsConstructor
@Service
class AddBookCommandHandler implements CommandHandler<AddBookCommand, ResponseEntity<IResponse>> {
    private final BookRepository bookRepository;
    private final CopyRepository copyRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public ResponseEntity<IResponse> handle(AddBookCommand command) {
        Book book = new Book();


        BookPricing bookPricing = new BookPricing();
        bookPricing.changeCost(command.cost(), null, 0);

        List<Author> authors = command.authors().stream().map(author -> {
            Author a = new Author();
            a.setFullName(author);
            return a;
        }).toList();
        book.setAuthors(authors);
        book.setBookPricing(bookPricing);

        book.setPublisher(command.publisher());
        book.setTitle(command.title());
        book.setPublisher(command.publisher());
        book.setPublishedDate("2021-01-01");
        book.setDescription("Description");
        book.setImageUrl("https://www.google.com");
        ArrayList<String> categories = new ArrayList<>();
        command.categories().forEach(category -> categories.add(category.getName()));
        List<Category> cats = categoryRepository.findByNameIn(categories);
        book.setCategories(cats);
        try {
            book = bookRepository.save(book);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(Map.of(
                    "statusCode", 500,
                    "message", "Error occurred while adding book",
                    "error", e.getMessage(
            ))));
        }

        for (int i = 0; i < 3; i++) {
            Copy copy = new Copy();
            copy.setAvailable(true);
            copy.setIsbn(book.getId().toString());
            copyRepository.save(copy);
        }

        return ResponseEntity.ok().body(new ApiResponse(Map.of(
                "statusCode", 201,
                "message", "Book added successfully"
        )));
    }
}