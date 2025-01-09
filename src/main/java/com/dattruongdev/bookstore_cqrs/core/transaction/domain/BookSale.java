package com.dattruongdev.bookstore_cqrs.core.transaction.domain;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document
@Data
public class BookSale {
    @Id
    private ObjectId id;
    private ObjectId userId;
    private ObjectId bookId;
    private List<ObjectId> copyIds;
    private Date boughtAt;
    private int quantity;
    private double totalPrice;
    private String currency;
    private String stripeSessionId;
}