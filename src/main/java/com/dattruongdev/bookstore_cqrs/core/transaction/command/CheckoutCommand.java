package com.dattruongdev.bookstore_cqrs.core.transaction.command;

import com.dattruongdev.bookstore_cqrs.core.catalog.domain.Book;
import com.dattruongdev.bookstore_cqrs.core.catalog.domain.BookRepository;
import com.dattruongdev.bookstore_cqrs.core.catalog.domain.Copy;
import com.dattruongdev.bookstore_cqrs.core.catalog.domain.CopyRepository;
import com.dattruongdev.bookstore_cqrs.core.transaction.domain.BookSale;
import com.dattruongdev.bookstore_cqrs.core.transaction.domain.BookSaleRepository;
import com.dattruongdev.bookstore_cqrs.core.transaction.factory.BookMethodFactory;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.HandledBy;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.command.Command;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.command.CommandHandler;
import com.dattruongdev.bookstore_cqrs.request.BookDTO;
import com.dattruongdev.bookstore_cqrs.response.ApiResponse;
import com.dattruongdev.bookstore_cqrs.response.ErrorResponse;
import com.dattruongdev.bookstore_cqrs.response.IResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@HandledBy(handler = CheckoutCommandHandler.class)
public record CheckoutCommand(List<BookDTO> books) implements Command<ResponseEntity<IResponse>> {
}

@Service
@RequiredArgsConstructor
class CheckoutCommandHandler implements CommandHandler<CheckoutCommand, ResponseEntity<IResponse>> {
    private final BookSaleRepository bookSaleRepository;
    private final BookRepository bookRepository;
    private final CopyRepository copyRepository;
    @Value("${client.url}")
    private String clientUrl;
    @Value("${stripe.secret}")
    private String stripeSecret;

    @Override
    public ResponseEntity<IResponse> handle(CheckoutCommand command) {
        for (BookDTO book : command.books()) {
            Integer count = copyRepository.findCountCopiesAvailableByBookId(new ObjectId(book.id()));
            int copyCount = count == null ? 0 : count;

            if (copyCount < book.quantity()) {
                return ResponseEntity.badRequest().body(new ErrorResponse(400, "Not enough copies for book " + book.id()));
            }
        }
        List<Book> books = bookRepository.findAllById(command.books().stream().map(book -> new ObjectId(book.id())).toList());
        if (books.size() != command.books().size()) {
            return ResponseEntity.badRequest().body(new ErrorResponse(404, "Some books are not found"));
        }


        Stripe.apiKey = stripeSecret;

        SessionCreateParams.Builder paramBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(clientUrl + "/books/payment/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(clientUrl + "/books/payment/cancel");


        for (int i = 0; i < command.books().size(); i++) {
            Book book = books.get(i);
            int quantity = command.books().get(i).quantity();
            String method = command.books().get(i).method();
            DecimalFormat df = new DecimalFormat("#");

            paramBuilder.addLineItem(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity((long) quantity)
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("vnd")
                                            .setUnitAmount(
                                                    Long.parseLong(
                                                            df.format(BigDecimal.valueOf(Objects.requireNonNull(BookMethodFactory.createMethod(method)).execute(book.getPrice().getCost().getAmount())))))
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .putMetadata("book_id", book.getId().toString())
                                                            .putMetadata("method", method)
                                                            .addImage(book.getImageUrl())
                                                            .setName(book.getTitle())
                                                            .setDescription(book.getDescription())
                                                            .build()
                                            )
                                            .build()
                            )
                            .build()
            );
        }


        try {
            Session session = Session.create(paramBuilder.build());
            session.setMetadata(Map.of(
                            "books", "Something"
                    )
            );

            return ResponseEntity.ok().body(new ApiResponse(Map.of(
                    "message", "Verify complete",
                    "url", session.getUrl()
            )));
        } catch (StripeException e) {
            return ResponseEntity.status(500).body(new ErrorResponse(500, e.getMessage()));
        }


    }
}
