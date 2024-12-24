package com.dattruongdev.bookstore_cqrs.core.catalog.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CategoryRepository extends MongoRepository<Category, String> {
    Category findByName(String name);
    List<Category> findByNameIn(List<String> names);
}
