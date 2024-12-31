package com.dattruongdev.bookstore_cqrs.core.catalog.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
public class Category {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    @Indexed(unique = true)
    private String name;
}
