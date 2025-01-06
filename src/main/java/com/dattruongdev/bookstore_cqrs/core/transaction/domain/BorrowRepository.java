package com.dattruongdev.bookstore_cqrs.core.transaction.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface BorrowRepository extends MongoRepository<Borrow, String> {
    Borrow findByCopyId(String copyId);
    Borrow findByUserId(String userId);
}
