package com.dattruongdev.bookstore_cqrs.core.transaction.events;

import com.dattruongdev.bookstore_cqrs.core.catalog.domain.Book;
import org.bson.types.ObjectId;

import java.util.List;

public record BookSoldEvent(Book book, List<ObjectId> copyIds) {
}
