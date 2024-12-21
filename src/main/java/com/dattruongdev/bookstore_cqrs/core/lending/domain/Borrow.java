package com.dattruongdev.bookstore_cqrs.core.lending.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;

@Data
public class Borrow {
    @Id
    private String id;

    private String copyId;
    private String userId;
    private Date borrowedAt;
    private Date returnedAt;

    public void returnBook() {
        this.returnedAt = new Date();
    }

    public void makeBorrow(String copyId, String userId) {
        this.copyId = copyId;
        this.userId = userId;
        this.borrowedAt = new Date();
        this.returnedAt = null;
    }
}
