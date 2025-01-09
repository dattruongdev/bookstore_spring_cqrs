package com.dattruongdev.bookstore_cqrs.core.transaction.domain;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookSaleRepository extends MongoRepository<BookSale, ObjectId>{
}
