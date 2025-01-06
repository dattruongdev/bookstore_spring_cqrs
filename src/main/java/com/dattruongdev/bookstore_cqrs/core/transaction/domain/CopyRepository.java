package com.dattruongdev.bookstore_cqrs.core.transaction.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CopyRepository extends MongoRepository<Copy, String> {
    List<Copy> findByAvailable(boolean available);
    List<Copy> findByIsbn(String isbn);
}
