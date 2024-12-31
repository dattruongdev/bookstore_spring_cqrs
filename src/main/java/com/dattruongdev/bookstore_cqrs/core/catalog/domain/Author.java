package com.dattruongdev.bookstore_cqrs.core.catalog.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class Author {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String fullName;
}
