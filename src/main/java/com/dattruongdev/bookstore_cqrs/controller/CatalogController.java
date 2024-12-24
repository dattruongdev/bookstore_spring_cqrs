package com.dattruongdev.bookstore_cqrs.controller;

import com.dattruongdev.bookstore_cqrs.core.catalog.command.AddBookCommand;
import com.dattruongdev.bookstore_cqrs.core.catalog.query.FindAllBooksQuery;
import com.dattruongdev.bookstore_cqrs.core.catalog.query.FindAllCategoriesQuery;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.DispatchableHandler;
import com.dattruongdev.bookstore_cqrs.response.IResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.root}/catalog")
@RequiredArgsConstructor
public class CatalogController {
    private final DispatchableHandler dispatchableHandler;

    @GetMapping("/books")
    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<IResponse> getAllBooks(FindAllBooksQuery query) {
        return dispatchableHandler.dispatch(query);
    }

    @GetMapping("/categories")
    public ResponseEntity<IResponse> getAllCategories(FindAllCategoriesQuery query) {
        return dispatchableHandler.dispatch(query);
    }

    @PostMapping("/books/add")
    public ResponseEntity<IResponse> addBook(@RequestBody AddBookCommand command) {
        return dispatchableHandler.dispatch(command);
    }

}
