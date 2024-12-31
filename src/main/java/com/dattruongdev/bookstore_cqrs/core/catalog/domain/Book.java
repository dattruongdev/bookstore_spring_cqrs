package com.dattruongdev.bookstore_cqrs.core.catalog.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Data
//@AllArgsConstructor
//@NoArgsConstructor
@Document
public class Book {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String title;
    @Transient
    private int totalBooks;
    private double rating;
    private boolean isFeatured;
    private String publisher;
    private String publishedDate;
    private String description;
    private String imageUrl;
    @DocumentReference
    private List<Author> authors;
    @DocumentReference
    private BookPricing bookPricing;
//
    @DocumentReference
    private List<Category> categories;

//    @DocumentReference
//    private List<Copy> copies;
}
