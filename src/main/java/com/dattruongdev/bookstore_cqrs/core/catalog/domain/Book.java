package com.dattruongdev.bookstore_cqrs.core.catalog.domain;

import com.dattruongdev.bookstore_cqrs.core.transaction.domain.BookSale;
import com.dattruongdev.bookstore_cqrs.core.transaction.events.BookSoldEvent;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.List;

@Data
@Document
public class Book extends AbstractAggregateRoot<Book> {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("_id")
    private ObjectId id;
    private String title;
    @Transient
    @JsonIgnore
    private int totalBooks;
    private double rating;
    private boolean isFeatured;
    private String publisher;
    private String publishedDate;
    private String description;
    private String imageUrl;
    private int numberOfCopies;
    private int numberOfPages;
    @DocumentReference
    private List<Author> authors;
//    @DocumentReference
    private ObjectId bookPricing;
    private BookPricing price;
    private BookPricing originalPrice;
//
    @DocumentReference
    private List<Category> categories;

//    @DocumentReference
    private List<ObjectId> copies = new ArrayList<>();
    private List<Copy> copiesList = new ArrayList<>();

    @DocumentReference
    private List<BookSale> bookSales = new ArrayList<>();

    public void addCopy(ObjectId copyId) {
        this.copies.add(copyId);
    }

    public void sell(int quantity, double amount, String currency) {

        BookSale bookSale = new BookSale();
        bookSale.setBookId(this.id);
        bookSale.setTotalPrice(amount);
        bookSale.setQuantity(quantity);
        bookSale.setCurrency(currency);
        this.bookSales.add(bookSale);
        List<ObjectId> copyIds = this.copies;

        this.registerEvent(new BookSoldEvent(this, copyIds));
    }
}
