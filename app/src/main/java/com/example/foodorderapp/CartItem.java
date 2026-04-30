package com.example.foodorderapp;
public class CartItem {
    private MenuItemApi menuItemApi;
    private int quantity;
    private String note;
    private String extras;
    private String tableNumber;
    public MenuItemApi getMenuItemApi() { return menuItemApi; }
    public void setMenuItemApi(MenuItemApi m) { this.menuItemApi = m; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int q) { this.quantity = q; }
    public String getNote() { return note; }
    public void setNote(String n) { this.note = n; }
    public String getExtras() { return extras; }
    public void setExtras(String e) { this.extras = e; }
    public String getTableNumber() { return tableNumber; }
    public void setTableNumber(String t) { this.tableNumber = t; }
}