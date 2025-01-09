package com.dattruongdev.bookstore_cqrs.core.transaction.command;

import com.dattruongdev.bookstore_cqrs.core.catalog.domain.Book;
import com.dattruongdev.bookstore_cqrs.core.catalog.domain.BookRepository;
import com.dattruongdev.bookstore_cqrs.core.catalog.domain.Copy;
import com.dattruongdev.bookstore_cqrs.core.catalog.domain.CopyRepository;
import com.dattruongdev.bookstore_cqrs.core.transaction.domain.BookSale;
import com.dattruongdev.bookstore_cqrs.core.transaction.domain.BookSaleRepository;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.HandledBy;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.command.Command;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.command.CommandHandler;
import com.dattruongdev.bookstore_cqrs.response.ApiResponse;
import com.dattruongdev.bookstore_cqrs.response.IResponse;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.ApiResource;
import com.stripe.param.PriceRetrieveParams;
import com.stripe.param.checkout.SessionListLineItemsParams;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.json.JsonObject;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@HandledBy(handler = CreateBookSaleCommandHandler.class)
public record CreateBookSaleCommand(String payload) implements Command<Integer> {
}

@Service
@RequiredArgsConstructor
class CreateBookSaleCommandHandler implements CommandHandler<CreateBookSaleCommand, Integer> {
    private final BookSaleRepository bookSaleRepository;
    private final BookRepository bookRepository;
    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;
    private final CopyRepository copyRepository;
    @Value("${stripe.secret}")
    private String stripeSecret;

    @Override
    public Integer handle(CreateBookSaleCommand command) {
        Stripe.apiKey = stripeSecret;
        Event event = null;
        try {
            event = ApiResource.GSON.fromJson(command.payload(), Event.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            return null;
        }

        // Handle the event

        switch(event.getType()) {
            case "checkout.session.completed":
                Session session = (Session) stripeObject;
                try {
                    Session resource = Session.retrieve(session.getId());
                    SessionListLineItemsParams params = SessionListLineItemsParams.builder().build();
                    LineItemCollection lineItems = resource.listLineItems(params);
                    List<LineItem> items = lineItems.getData();
                    List<ObjectId> bookIds = new ArrayList<>();
                    Map<ObjectId, LineItem> idsMap = new HashMap<>();

                    for (LineItem item : items) {
                        Price price = Price.retrieve(item.getPrice().getId(), PriceRetrieveParams.builder().addAllExpand(List.of("product")).build(), null);
                        var product = price.getProductObject();
                        ObjectId bookId = new ObjectId(product.getMetadata().get("book_id"));
                        bookIds.add(bookId);
                        idsMap.put(bookId, item);
                    }


                    Aggregation aggregation = Aggregation.newAggregation(
                            Aggregation.match(Criteria.where("_id").in(bookIds)),
                            Aggregation.lookup("copy", "_id", "bookId", "copies"),
                            Aggregation.match(Criteria.where("copies.available").is(true))
                    );

                    AggregationResults<Book> docs = mongoTemplate.aggregate(aggregation, "book", Book.class);

                    List<Book> books = docs.getMappedResults();
                    var bks = docs.getRawResults().get("results", ArrayList.class);

                    for (int i = 0; i < books.size(); i++) {
                        Document bookDoc = (Document) bks.get(i);

                        List<Document> copiesDoc = (List<Document>) bookDoc.get("copies");
                        List<Copy> copies = copiesDoc.stream().map(copy -> {
                            String value = null;
                            try {
                                value = objectMapper.writeValueAsString( copy);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                            try {
                                Copy cpy = objectMapper.readValue(value, Copy.class);
                                return cpy;
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }

                        }).toList();

                        Book book = books.get(i);
                        book.setCopies(copies.stream().map(Copy::getId).toList());
                        Long quantity = idsMap.get(book.getId()).getQuantity();
                        double amount = idsMap.get(book.getId()).getAmountTotal();
                        String currency = idsMap.get(book.getId()).getCurrency();
                        book.sell(quantity.intValue(), amount, currency);

                    }
                    bookRepository.saveAll(books);
                    bookSaleRepository.saveAll(books.stream().map(Book::getBookSales).flatMap(List::stream).toList());

                } catch (StripeException e) {
                    throw new RuntimeException(e);
                }
                // ... handle other event types
                break;
            default:
                System.out.println("Unhandled event type: " + event.getType());
                break;

        }

        return 200;
    }
}