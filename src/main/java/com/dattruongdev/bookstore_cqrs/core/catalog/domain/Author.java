package com.dattruongdev.bookstore_cqrs.core.catalog.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class Author {
    @Id
    private String id;
    private String fullName;
}
