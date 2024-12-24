package com.dattruongdev.bookstore_cqrs.core.catalog.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cost {
    private double amount;
    private String currency;
}
