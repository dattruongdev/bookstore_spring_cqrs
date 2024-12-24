package com.dattruongdev.bookstore_cqrs.core.catalog.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
public class Category {
    @Id
    private String id;
    @Indexed(unique = true)
    private String name;
}
