package com.dattruongdev.bookstore_cqrs.core.catalog.domain;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CategoryRepository extends MongoRepository<Category, ObjectId> {
    Category findByName(String name);
    List<Category> findByNameIn(List<String> names);
}
