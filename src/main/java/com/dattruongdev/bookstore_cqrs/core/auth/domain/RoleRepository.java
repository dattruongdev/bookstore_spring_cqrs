package com.dattruongdev.bookstore_cqrs.core.auth.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, String> {
    Role findByRoleName(String name);
}
