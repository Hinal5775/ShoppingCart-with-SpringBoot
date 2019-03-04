package com.cognizant.shoppingcart.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartRepository cartRepository;

    @Autowired
    private LineItemRepository lineItemRepository;


    @PostMapping
    public Cart createNewCart() {
        return cartRepository.save(new Cart());
    }


    @GetMapping("/{id}")
    public Cart getShoppingCart(@PathVariable Integer id) {
        return cartRepository.findById(id).orElseThrow(RuntimeException::new);
    }


    @DeleteMapping("/{id}")
    public void deleteShoppingCart(@PathVariable Integer id) {
        cartRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    public Cart updateShoppingCart(@PathVariable Integer id, @RequestBody Cart cart) {
        Cart cartToUpdate = cartRepository.findById(id).orElseThrow(RuntimeException::new);
        cartToUpdate.setName(cart.getName());

        //TODO
//        if(!cart.getItems().isEmpty()) {
//            if(cartToUpdate.getItems().isEmpty()){
//                cartToUpdate.addItem();
//            }
//        }
        return cartRepository.save(cartToUpdate);
    }

//    private LineItem updateLineItem(LineItem item) {
//        LineItem itemToUpdate = lineItemRepository.findById(item.getId()).orElseThrow(RuntimeException::new);
//        item.setQuantity();
//
//    }

    @GetMapping("/{id}/lineItem")
    public List<LineItem> getLineItems(@PathVariable Integer id) {
        Cart cart = cartRepository.findById(id).orElseThrow(RuntimeException::new);
        return cart.getItems();

    }


    @PostMapping("/{id}/lineItem")
    public LineItem createLineItem(@PathVariable Integer id, @RequestBody LineItem lineItem) {
        Cart cart = cartRepository.findById(id).orElseThrow(RuntimeException::new);

        cart.addItem(lineItem);
        lineItem.setCart(cart);

        return lineItemRepository.save(lineItem);
    }

    @PutMapping("/{id}/lineItem/{lineItemId}")
    public LineItem updateLineItem(@PathVariable Integer id, @PathVariable Integer lineItemId, @RequestBody LineItem lineItem) {
        Cart cart = cartRepository.findById(id).orElseThrow(RuntimeException::new);
//        cart.addItem(lineItem);
//        lineItem.setCart(cart);

        LineItem lineItemToUpdate = lineItemRepository.findById(lineItemId).orElseThrow(RuntimeException::new);
        if (lineItemToUpdate.getCart() == null || !lineItemToUpdate.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Item doesn't belong to the given cart");
        }
        lineItemToUpdate.setQuantity(lineItem.getQuantity());
        return lineItemRepository.save(lineItemToUpdate);
    }

    @DeleteMapping("/{id}/lineItem/{lineItemId}")
    public void deleteLineItem(@PathVariable Integer id, @PathVariable Integer lineItemId) {
        lineItemRepository.deleteById(lineItemId);

    }


}
