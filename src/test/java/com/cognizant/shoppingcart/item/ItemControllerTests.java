package com.cognizant.shoppingcart.item;

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
public class ItemControllerTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ItemRepository repository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void before() {
        repository.deleteAll();
    }

    @After
    public void after() {
        repository.deleteAll();
    }

    @Test
    public void getItemsShouldReturnAllItems() throws Exception {
        //Set up
        Item item = new Item("watch", new BigDecimal("350.00"), false);
        repository.save(item);
        List<Item> expected = Arrays.asList(item);

        //Exercise
        String result = mvc.perform(get("/api/item"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Item> actual = objectMapper.readValue(result,
                new TypeReference<List<Item>>() {
                });

        //assert
        Assert.assertEquals("Expected to get all items", expected, actual);
    }

    @Test
    public void addItemShouldReturnItem() throws Exception {
        //Setup
        Item expected = new Item("bag", new BigDecimal("999.99"), true);
        String jsonString = "{\"name\":\"" + expected.getName() + "\",\"price\":\""
                + expected.getPrice() + "\",\"onSale\":" + expected.isOnSale() + "}";

        //Exercise
        String response = mvc.perform(post("/api/item")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn().getResponse().getContentAsString();

        Item actual = objectMapper.readValue(response, new TypeReference<Item>() {
        });

        //assert
        Assert.assertEquals("Item Should be the Same", expected.getName(), actual.getName());
    }

    @Test
    public void getItemById() throws Exception {
        //Setup
        Item expected = new Item("chocalate", new BigDecimal("40.00"), false);
        repository.save(expected);
        Integer id = expected.getId();

        //Exercise
        String result = mvc.perform(get("/api/item/" + id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn().getResponse().getContentAsString();

        Item actual = objectMapper.readValue(result, new TypeReference<Item>() {
        });

        //Assert
        Assert.assertEquals("Expected to get Item By its Id", expected, actual);
    }

    @Test
    public void updateItemById() throws Exception {
        //Setup
        Item expected = new Item("cap", new BigDecimal("20.00"), true);
        repository.save(expected);
        expected.setPrice(new BigDecimal("400.00"));
        String jsonString = "{\"name\":\"" + expected.getName() + "\",\"price\":\"" + expected.getPrice() + "\",\"onSale\":" + expected.isOnSale() + "}";

        //Exercise
        String response = mvc.perform(put("/api/item/" + expected.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn().getResponse().getContentAsString();

        Item actual = objectMapper.readValue(response, new TypeReference<Item>() {
        });

        //Assert
        Assert.assertEquals("Update Item by Id", expected, actual);

    }

    @Test
    public void deleteItemById() throws Exception {
        //Setup
        Item item = new Item("iphone", new BigDecimal("800.00"), false);
        repository.save(item);
        String expected = null;


        //Exercise
        String result = mvc.perform(delete("/api/item/" + item.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();


        Item actual = repository.findById(item.getId()).orElse(null);
        //Assert
        Assert.assertEquals("Should Delete Item by Id", expected, actual);
        Assert.assertEquals("Expected delete object and return nothing", "", result);

    }
}

