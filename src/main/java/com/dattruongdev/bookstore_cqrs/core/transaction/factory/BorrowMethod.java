package com.dattruongdev.bookstore_cqrs.core.transaction.factory;

public class BorrowMethod implements TransactionMethod {
    @Override
    public double execute(double price) {
        return price *  0.5;
    }
}
