package com.dattruongdev.bookstore_cqrs.core.catalog.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
public class BookPricing {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private Cost cost;
    private Cost originalCost;
    private double discount;
    private Date beginDate;
    private Date endDate;
    private boolean isWeekDeal;
//    @DocumentReference
//    private Book book;

    public void changeCost(Cost cost, Date endDate, double discount) {
        this.beginDate = new Date();
            this.endDate = endDate;
            cost.setAmount(cost.getAmount() * (1 - discount));
            this.cost = cost;
            this.discount = discount;
    }
//    public static BookCost createBookCost(Cost cost, Date endDate, double discount, boolean isWeekDeal) {
//        BookCost bookCost = new BookCost();
////        bookCost.book = book;
//        bookCost.cost = cost;
//        bookCost.originalCost = cost;
//        bookCost.discount = discount;
//        bookCost.beginDate = new Date();
//        bookCost.endDate = endDate;
//        bookCost.isWeekDeal = isWeekDeal;
//
//        return bookCost;
//    }
}
