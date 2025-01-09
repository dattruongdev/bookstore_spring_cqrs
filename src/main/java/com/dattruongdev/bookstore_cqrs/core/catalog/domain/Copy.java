package com.dattruongdev.bookstore_cqrs.core.catalog.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Data
public class Copy {
    @Id
    @JsonProperty("_id")
    private ObjectId id;
    private ObjectId bookId;
    private boolean available;
    private String status;
//    @JsonIgnore
    private String createdAt;
//    @JsonIgnore
    private String updatedAt;
    private Book book;
    @JsonIgnoreProperties("_class")

    public void makeAvailable() {
        this.available = true;
        this.updatedAt = LocalDateTime.now().toString();
    }

    public void makeUnavailable() {
        this.available = false;
        this.updatedAt = LocalDateTime.now().toString();
    }

}
