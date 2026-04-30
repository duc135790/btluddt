package com.example.foodorderapp;
import java.util.ArrayList;
import java.util.List;
public class CartManager {
    private static CartManager instance;
    private List<CartItem> items = new ArrayList<>();
    private CartManager() {}

    public static CartManager getInstance() {
        if (instance == null) instance = new CartManager();
        return instance;
    }
    public void addItem(CartItem item) { items.add(item); }
    public void addApiItem(MenuItemApi apiItem) {
        for (CartItem ci : items) {
            if (ci.getMenuItemApi().getId() == apiItem.getId()) {
                ci.setQuantity(ci.getQuantity() + 1);
                return;
            }
        }
        CartItem ci = new CartItem();
        ci.setMenuItemApi(apiItem);
        ci.setQuantity(1);
        items.add(ci);
    }
    public List<CartItem> getItems() { return items; }
    public int getCount() { return items.size(); }
    public double getTotal() {
        double total = 0;
        for (CartItem i : items) total += i.getMenuItemApi().getPrice() * i.getQuantity();
        return total;
    }
    public String getItemsSummary() {
        StringBuilder sb = new StringBuilder();
        for (CartItem i : items) {
            sb.append(i.getMenuItemApi().getName()).append(" x").append(i.getQuantity());
            if (i.getNote() != null && !i.getNote().isEmpty())
                sb.append(" [").append(i.getNote()).append("]");
            sb.append(", ");
        }
        return sb.toString();
    }
    public void clear() { items.clear(); }
}