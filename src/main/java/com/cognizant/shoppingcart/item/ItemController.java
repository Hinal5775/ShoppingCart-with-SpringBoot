package com.cognizant.shoppingcart.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/item")
public class ItemController {

    @Autowired
    private ItemRepository repository;

    @GetMapping
    public List<Item> getAllItems() {
        return (List<Item>) repository.findAll();
    }

    @PostMapping
    public Item addItem(@RequestBody Item item) {
        return repository.save(item);
    }

    @GetMapping("/{id}")
    public Item getItem(@PathVariable Integer id) {
        return repository.findById(id).orElseThrow(RuntimeException::new);
    }

    @PutMapping("/{id}")
    public Item updateItem(@RequestBody Item item, @PathVariable Integer id) {
        Item storedItem = repository.findById(id).orElseThrow(RuntimeException::new);
        storedItem.setPrice(item.getPrice());
        storedItem.setOnSale(item.isOnSale());
        storedItem.setName(item.getName());

        return repository.save(storedItem);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Integer id) {
        repository.deleteById(id);
    }
}
