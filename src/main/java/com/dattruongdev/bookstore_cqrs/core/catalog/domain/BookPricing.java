package com.dattruongdev.bookstore_cqrs.core.catalog.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("id")
    private ObjectId id;
    private Cost cost;
    private Cost originalCost;
    private double discount;
    private Date beginDate;
    private Date endDate;
    private boolean isWeekDeal;
    private Book book;

    public void changeCost(Cost cost, Date endDate, double discount) {
        this.beginDate = new Date();
            this.endDate = endDate;
            this.originalCost = cost;

            Cost newCost = new Cost();
            newCost.setAmount(cost.getAmount() * (1 - discount));
            newCost.setCurrency(cost.getCurrency());
            this.cost = newCost;
            this.discount = discount;
    }
}
