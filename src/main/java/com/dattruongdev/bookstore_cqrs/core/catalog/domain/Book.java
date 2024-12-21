package com.dattruongdev.bookstore_cqrs.core.catalog.domain;

import com.dattruongdev.bookstore_cqrs.core.lending.domain.Copy;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Data
public class Book {
    @Id
    private Isbn id;
    private String title;
    private String edition;
    private String author;
    private String publisher;
    private String source;
    private double cost;

    @DocumentReference
    private List<Copy> copies;
}
