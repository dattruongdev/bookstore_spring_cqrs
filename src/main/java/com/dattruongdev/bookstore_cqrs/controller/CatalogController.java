package com.dattruongdev.bookstore_cqrs.controller;

import com.dattruongdev.bookstore_cqrs.core.catalog.command.AddBookCommand;
import com.dattruongdev.bookstore_cqrs.core.catalog.command.LeaveReviewCommand;
import com.dattruongdev.bookstore_cqrs.core.catalog.query.*;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.DispatchableHandler;
import com.dattruongdev.bookstore_cqrs.response.IResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.root}/catalog")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class CatalogController {
    private final DispatchableHandler dispatchableHandler;

    @GetMapping("/books")
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

    @GetMapping("/books-by-page")
    public ResponseEntity<IResponse> findBooksInPage(@RequestParam int  page) {
        FindBooksInPageQuery query = new FindBooksInPageQuery(page);
        return dispatchableHandler.dispatch(query);
    }
    @GetMapping("/featured-books")
    public ResponseEntity<IResponse> findFeaturedBooks() {
        FindIsFeaturedBooksQuery query = new FindIsFeaturedBooksQuery(6);
        return dispatchableHandler.dispatch(query);
    }

    @GetMapping("/new-release-books")
    public ResponseEntity<IResponse> findNewReleaseBooks(FindNewReleaseQuery query) {
        return dispatchableHandler.dispatch(query);
    }

    @GetMapping("/week-deals")
    public ResponseEntity<IResponse> findWeekDeals(FindWeekDealsQuery query) {
        return dispatchableHandler.dispatch(query);
    }

    @GetMapping("/readers-pick-books")
    public ResponseEntity<IResponse> findReadersPickBooks(FindByReadersPickQuery query) {
        return dispatchableHandler.dispatch(query);
    }

    @GetMapping("/related-books")
    public ResponseEntity<IResponse> findRelatedBooks( @RequestParam String bookId, @RequestParam List<String> categories) {
        FindRelatedBooksQuery query = new FindRelatedBooksQuery(bookId, categories);
        return dispatchableHandler.dispatch(query);
    }

    @GetMapping("/{bookId}/reviews")
    public ResponseEntity<IResponse> findReviewsFromBook(@PathVariable String bookId, @RequestParam int page, @RequestParam int size) {
        FindBookReviewsQuery query = new FindBookReviewsQuery(bookId, page, size);
        return dispatchableHandler.dispatch(query);
    }

    @PostMapping("/{bookId}/reviews/add")
    public ResponseEntity<IResponse> leaveReview(@RequestBody LeaveReviewCommand command) {
        return dispatchableHandler.dispatch(command);
    }

    @GetMapping("/authors")
    public ResponseEntity<IResponse> findAuthors(@RequestParam int page, @RequestParam int size) {
        FindAuthorsQuery query = new FindAuthorsQuery(page, size);
        return dispatchableHandler.dispatch(query);
    }
}
