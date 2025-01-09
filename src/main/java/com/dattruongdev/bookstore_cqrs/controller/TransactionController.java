package com.dattruongdev.bookstore_cqrs.controller;

import com.dattruongdev.bookstore_cqrs.core.transaction.command.CheckoutCommand;
import com.dattruongdev.bookstore_cqrs.core.transaction.command.CreateBookSaleCommand;
import com.dattruongdev.bookstore_cqrs.core.transaction.domain.BookSale;
import com.dattruongdev.bookstore_cqrs.core.transaction.query.FindBorrowByCopyIdQuery;
import com.dattruongdev.bookstore_cqrs.core.transaction.query.FindBorrowByUserIdQuery;
import com.dattruongdev.bookstore_cqrs.core.transaction.query.FindCopyAvailableQuery;
import com.dattruongdev.bookstore_cqrs.core.transaction.query.FindCopyByIsbnQuery;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.DispatchableHandler;
import com.dattruongdev.bookstore_cqrs.response.IResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.ApiResource;
import com.stripe.param.PriceRetrieveParams;
import com.stripe.param.checkout.SessionListLineItemsParams;
import com.stripe.param.checkout.SessionRetrieveParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.stripe.net.ApiResource.*;

@RestController
@RequestMapping("${api.root}/transaction")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class TransactionController {
    private final DispatchableHandler dispatchableHandler;
    @Value("${stripe.secret}")
    private String stripeSecret;

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

    @PostMapping("/checkout")
    public ResponseEntity<IResponse> checkout(@RequestBody CheckoutCommand request) {
        return dispatchableHandler.dispatch(request);
    }

    @PostMapping("/webhook")
    public String checkoutWebhook(@RequestBody String payload) {
        CreateBookSaleCommand command = new CreateBookSaleCommand(payload);
        dispatchableHandler.dispatch(command);
        return "";
    }
}
