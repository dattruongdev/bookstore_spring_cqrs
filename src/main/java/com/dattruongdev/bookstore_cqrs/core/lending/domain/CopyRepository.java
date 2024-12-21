package com.dattruongdev.bookstore_cqrs.core.lending.domain;

import com.dattruongdev.bookstore_cqrs.core.catalog.domain.Isbn;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CopyRepository extends MongoRepository<Copy, String> {
    List<Copy> findAvailableCopies();
    List<Copy> findByIsbn(Isbn isbn);
}
