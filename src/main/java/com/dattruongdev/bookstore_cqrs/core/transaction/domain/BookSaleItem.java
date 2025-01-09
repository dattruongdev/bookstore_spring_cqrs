package com.dattruongdev.bookstore_cqrs.core.transaction.domain;

import com.dattruongdev.bookstore_cqrs.core.catalog.domain.Book;

public record BookSaleItem(Book item, int quantity, String method) {
}
