package com.dattruongdev.bookstore_cqrs.core.lending.domain;

import com.dattruongdev.bookstore_cqrs.core.catalog.domain.Book;
import com.dattruongdev.bookstore_cqrs.core.catalog.domain.Isbn;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Date;

@Data
public class Copy {
    @Id
    private String id;
    private Isbn isbn;
    private boolean available;
    private Date createdAt;
    private Date updatedAt;

    public void makeAvailable() {
        this.available = true;
    }

    public void makeUnavailable() {
        this.available = false;
    }
}
