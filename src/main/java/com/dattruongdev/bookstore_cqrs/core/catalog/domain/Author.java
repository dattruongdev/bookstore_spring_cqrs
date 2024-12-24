package com.dattruongdev.bookstore_cqrs.core.catalog.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class Author {
    private String fullName;
}
