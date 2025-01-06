package com.dattruongdev.bookstore_cqrs.controller;

import com.dattruongdev.bookstore_cqrs.core.transaction.query.FindBorrowByCopyIdQuery;
import com.dattruongdev.bookstore_cqrs.core.transaction.query.FindBorrowByUserIdQuery;
import com.dattruongdev.bookstore_cqrs.core.transaction.query.FindCopyAvailableQuery;
import com.dattruongdev.bookstore_cqrs.core.transaction.query.FindCopyByIsbnQuery;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.DispatchableHandler;
import com.dattruongdev.bookstore_cqrs.response.IResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.root}/lending")
@RequiredArgsConstructor
public class LendingController {
    private final DispatchableHandler dispatchableHandler;

    @GetMapping("/copies-available")
    public ResponseEntity<IResponse> findAvailableCopies(FindCopyAvailableQuery query) {
        return dispatchableHandler.dispatch(query);
    }

    @GetMapping("/copies/{isbn}")
    public ResponseEntity<IResponse> findCopiesByIsbn(@PathVariable String isbn) {
        FindCopyByIsbnQuery query = new FindCopyByIsbnQuery(isbn);
        return dispatchableHandler.dispatch(query);
    }


    @GetMapping("/borrow/{copyId}")
    public ResponseEntity<IResponse> findBorrowByCopyId(@PathVariable String copyId) {
        FindBorrowByCopyIdQuery query = new FindBorrowByCopyIdQuery(copyId);
        return dispatchableHandler.dispatch(query);
    }

    @GetMapping("/borrow/{userId}")
    public ResponseEntity<IResponse> findBorrowByUserId(@PathVariable String userId) {
        FindBorrowByUserIdQuery query = new FindBorrowByUserIdQuery(userId);
        return dispatchableHandler.dispatch(query);
    }
}
