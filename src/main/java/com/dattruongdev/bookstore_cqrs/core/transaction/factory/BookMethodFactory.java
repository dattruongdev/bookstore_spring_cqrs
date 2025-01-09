package com.dattruongdev.bookstore_cqrs.core.transaction.factory;

public class BookMethodFactory {
    public static TransactionMethod createMethod(String method) {
        return switch (method) {
            case "borrow" -> new BorrowMethod();
            case "buy" -> new BuyMethod();
            default -> null;
        };
    }
}
