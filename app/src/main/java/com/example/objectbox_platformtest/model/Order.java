package com.example.objectbox_platformtest.model;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

@Entity
public class Order {

    @Id
    public long id;

    public long tag;

    public ToOne<Customer> customer;

    @Override
    public String toString() {
        return "Order(id=" + id + " tag=" + tag + ")";
    }
}
