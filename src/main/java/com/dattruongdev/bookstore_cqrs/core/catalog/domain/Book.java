package com.dattruongdev.bookstore_cqrs.core.catalog.domain;

import com.dattruongdev.bookstore_cqrs.core.lending.domain.Copy;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
public class Book {
    @Id
    private String id;
    private String title;
    @Transient
    private int totalBooks;
    private double rating;
    private boolean isFeatured;
    private String publisher;
    private String publishedDate;
    private String description;
    private Cost cost;
    private String imageUrl;
    private List<String> authors;
//
    @DocumentReference
    private List<Category> categories;

//    @DocumentReference
//    private List<Copy> copies;
}
