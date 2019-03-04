package com.cognizant.shoppingcart.cart;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cart")
    @JsonIgnore
    private List<LineItem> items = new ArrayList<>();

    private String name;

    @Transient
    @JsonIgnore
    public BigDecimal getTotalPrice() {
        return items.stream()
                .map(LineItem::getSubTotal)
                .reduce(new BigDecimal(0), BigDecimal::add);
    }

    public void addItem(LineItem item) {
        items.add(item);
    }

    public List<LineItem> getItems() {
        return items;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cart cart = (Cart) o;
        return Objects.equals(id, cart.id) &&
                Objects.equals(items, cart.items) &&
                Objects.equals(name, cart.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, items);
    }
}