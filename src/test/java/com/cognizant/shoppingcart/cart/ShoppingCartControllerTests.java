package com.cognizant.shoppingcart.cart;

import com.cognizant.shoppingcart.item.Item;
import com.cognizant.shoppingcart.item.ItemRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ShoppingCartControllerTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ShoppingCartRepository cartRepository;

    @Autowired
    private LineItemRepository lineItemRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void before() {
        lineItemRepository.deleteAll();
        cartRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @After
    public void after() {
//        lineItemRepository.deleteAll();
//        cartRepository.deleteAll();
//        itemRepository.deleteAll();
    }

    @Test
    public void createShoppingCartShouldReturnCreateCart() throws Exception {
        //Setup
        Cart expected = new Cart();

        //exercise
        String response = mvc.perform(post("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn().getResponse().getContentAsString();

        Cart actual = objectMapper.readValue(response, new TypeReference<Cart>() {
        });

        //assert
        Assert.assertEquals("Cart Should be the empty", expected.getItems(), actual.getItems());
    }

    @Test
    public void getShoppingCartShouldReturnCart() throws Exception {
        //Setup
        Cart expected = new Cart();
        cartRepository.save(expected);

        //exercise
        String response = mvc.perform(get("/api/cart/" + expected.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn().getResponse().getContentAsString();

        Cart actual = objectMapper.readValue(response, new TypeReference<Cart>() {
        });

        //assert
        Assert.assertEquals("Carts Should be the Same", expected.getId(), actual.getId());
        Assert.assertEquals("Cart Should be the Empty", expected.getTotalPrice(), actual.getTotalPrice());
    }

    @Test
    public void updateShoppingCartShouldUpdateLineItems() throws Exception {
        //Setup
        Cart expected = new Cart();
        cartRepository.save(expected);
        expected.setName("Updated Name");

        //exercise
        String jsonString = "{\"name\":\"" + expected.getName() + "\",\"id\":\"" + expected.getId()
                + "\",\"items\":" + expected.getItems() + "}";
        String response = mvc.perform(put("/api/cart/" + expected.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn().getResponse().getContentAsString();

        Cart actual = objectMapper.readValue(response, new TypeReference<Cart>() {
        });

        //assert
        Assert.assertEquals("Carts Should be the Same", expected.getId(), actual.getId());
        Assert.assertEquals("Cart Should have the same name", expected.getName(), actual.getName());
    }

    @Test
    public void deleteCartShouldDeleteCart() throws Exception {
        //Setup
        Cart cartToDelete = new Cart();
        cartRepository.save(cartToDelete);
        Cart expected = null;

        //Exercise

        mvc.perform(delete("/api/cart/" + cartToDelete.getId()))
                .andExpect(status().isOk());

        Cart actual = cartRepository.findById(cartToDelete.getId()).orElse(null);

        //assert
        Assert.assertEquals("Cart Should be Null", expected, actual);
    }

    @Test
    public void createLineItemShouldReturnLineItem() throws Exception {
        //Setup
        Cart cart = new Cart();
        cartRepository.save(cart);
        Item item = new Item("iphone", new BigDecimal("999.99"), true);
        itemRepository.save(item);


        LineItem expected = new LineItem(item, 1);
        expected.setCart(cart);

        //lineItem.setCart(cart);
        String jsonString = "{" +
                "  \"quantity\": 1," +
                "  \"item\": {" +
                "    \"id\":" + item.getId() + "," +
                "    \"name\": \"" + item.getName() + "\"," +
                "    \"price\": \"" + item.getPrice() + "\"," +
                "    \"onSale\": " + item.isOnSale() +
                "}" +
                "}";

        //Exercise
        String response = mvc.perform(post("/api/cart/" + cart.getId() + "/lineItem")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn().getResponse().getContentAsString();

        LineItem actual = objectMapper.readValue(response, new TypeReference<LineItem>() {
        });

        //assert
        Assert.assertEquals("Cart Should not be the empty", expected.getCart().getId(), actual.getCart().getId());
        Assert.assertEquals("Quantity should be the same", expected.getQuantity(), actual.getQuantity());
        Assert.assertEquals("Line item should contain the same item", expected.getItem(), actual.getItem());
    }

    @Test
    public void getLineItemShouldReturnLineItem() throws Exception {

        //Setup
        Cart cart = new Cart();
        cartRepository.save(cart);

        Item item = new Item("watch", new BigDecimal("600"), false);
        itemRepository.save(item);

        LineItem lineItem = new LineItem(item, 2);
        lineItem.setCart(cart);
        lineItemRepository.save(lineItem);

        List<LineItem> expected = Arrays.asList(lineItem);

        //Exercise
        String response = mvc.perform(get("/api/cart/" + cart.getId() + "/lineItem/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn().getResponse().getContentAsString();

        List<LineItem> actual = objectMapper.readValue(response, new TypeReference<List<LineItem>>() {
        });

        //Assert
        Assert.assertEquals("LineItems Should be Same", expected, actual);

    }

    @Test
    public void updateLineItemShouldReturnLineItem() throws Exception {
        //Setup
        Cart cart = new Cart();
        cartRepository.save(cart);

        Item item = new Item("mac", new BigDecimal("1200"), false);
        itemRepository.save(item);

        LineItem lineitem = new LineItem(item, 1);
        lineitem.setCart(cart);
        lineItemRepository.save(lineitem);

        lineitem.setQuantity(2);

        String jsonString = "{\"id\":\"" + lineitem.getId() + "\",\"qty\":\"" + lineitem.getQuantity()
                + "\" }";

        String response = mvc.perform(put("/api/cart/" + cart.getId() + "/lineItem/" + lineitem.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn().getResponse().getContentAsString();
        LineItem actual = objectMapper.readValue(response, new TypeReference<LineItem>() {
        });


    }

    @Test
    public void deleteLineItemShouldDeleteLineItem() throws Exception {
        //Setup
        Cart cart = new Cart();
        cartRepository.save(cart);

        Item item = new Item("mac", new BigDecimal("1200"), false);
        itemRepository.save(item);

        LineItem lineitem = new LineItem(item, 1);
        lineitem.setCart(cart);
        lineItemRepository.save(lineitem);
        LineItem expected = null;

        //Exercise
        mvc.perform(delete("/api/cart/" + cart.getId() + "/lineItem/" + lineitem.getId()))
                .andExpect(status().isOk());

        LineItem actual = lineItemRepository.findById(lineitem.getId()).orElse(null);

        //Assert
        Assert.assertEquals("LineItem Should be Null", expected, actual);
    }

}
