package com.dattruongdev.bookstore_cqrs.core.catalog.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
public class BookCost {
    @Id
    private String id;
    private String bookId;
    private Cost cost;
    private Cost originalCost;
    private double discount;
    private Date beginDate;
    private Date endDate;
    private boolean isWeekDeal;

    public void changeCost(Cost cost, Date endDate, double discount) {
        this.beginDate = new Date();
            this.endDate = endDate;
            cost.setAmount(cost.getAmount() * (1 - discount));
            this.cost = cost;
            this.discount = discount;
    }
    public static BookCost createBookCost(String bookId, Cost cost, Date endDate, double discount, boolean isWeekDeal) {
        BookCost bookCost = new BookCost();
        bookCost.bookId = bookId;
        bookCost.cost = cost;
        bookCost.originalCost = cost;
        bookCost.discount = discount;
        bookCost.beginDate = new Date();
        bookCost.endDate = endDate;
        bookCost.isWeekDeal = isWeekDeal;

        return bookCost;
    }
}
