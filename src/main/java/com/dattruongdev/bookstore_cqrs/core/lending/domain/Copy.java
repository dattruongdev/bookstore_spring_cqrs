package com.dattruongdev.bookstore_cqrs.core.lending.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
public class Copy {
    @Id
    private String id;
    private String isbn;
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
