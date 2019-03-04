package com.cognizant.shoppingcart.cart;

import com.cognizant.shoppingcart.item.Item;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
public class LineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToOne
    private Item item;
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    public LineItem() {
    }

    public LineItem(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Transient
    @JsonIgnore
    public BigDecimal getSubTotal() {
        return this.item.getPrice().multiply(new BigDecimal(this.quantity));
    }

    public Integer getId() {
        return id;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineItem lineItem = (LineItem) o;
        return quantity == lineItem.quantity &&
                id.equals(lineItem.id) &&
                Objects.equals(item.getId(), lineItem.item.getId()) &&
                Objects.equals(cart.getId(), lineItem.cart.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, item, quantity, cart);
    }
}

