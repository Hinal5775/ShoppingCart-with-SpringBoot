package com.cognizant.shoppingcart.cart;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CartService {


    public List<String> itemQuantities(List<LineItem> items) {
        List<String> lineItems = new ArrayList<>();
        return items.stream()
                .map((item) -> String.format("%s - x%s", item.getItem().getName(), item.getQuantity()))
                .collect(Collectors.toList());
    }

    public List<String> itemizedList(List<LineItem> items) {
        return items.stream()
                .map(CartService::itemizedFormat)
                .collect(Collectors.toList());
    }

    public List<String> onSaleItems(List<LineItem> items) {
        return items.stream()
                .filter((item) -> item.getItem().isOnSale())
                .map(CartService::itemizedFormat)
                .collect(Collectors.toList());
    }

    private static String itemizedFormat(LineItem item) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.getDefault());
        return String.format("%s x%d - %s",
                item.getItem().getName(),
                item.getQuantity(),
                formatter.format(item.getSubTotal()));
    }
}
