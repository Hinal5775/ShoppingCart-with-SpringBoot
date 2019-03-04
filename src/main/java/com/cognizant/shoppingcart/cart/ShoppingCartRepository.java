package com.cognizant.shoppingcart.cart;

import org.springframework.data.repository.CrudRepository;

public interface ShoppingCartRepository extends CrudRepository<Cart, Integer> {
}
