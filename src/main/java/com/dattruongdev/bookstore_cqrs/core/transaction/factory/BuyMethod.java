package com.dattruongdev.bookstore_cqrs.core.transaction.factory;

import org.springframework.stereotype.Component;

@Component
public class BuyMethod implements TransactionMethod {
    @Override
    public double execute(double price) {
        return price;
    }
}
