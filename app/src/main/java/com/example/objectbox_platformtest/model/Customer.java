package com.example.objectbox_platformtest.model;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;

@Entity
public class Customer {

    @Id
    public long id;

    public long tag;

    public String name;

    public ToMany<Order> orders;

    public Double size;
    public byte[] bits;

    @Override
    public String toString() {
        return "Customer(id=" + id + " tag=" + tag + " name=" + name + ")";
    }
}
