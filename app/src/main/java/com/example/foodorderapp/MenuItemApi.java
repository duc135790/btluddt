package com.example.foodorderapp;

import com.google.gson.annotations.SerializedName;

public class MenuItemApi {
    private int id;
    private String name;
    private double price;
    private String description;
    @SerializedName("category_id")
    private int categoryId;
    @SerializedName("is_available")
    private int isAvailable;

    public MenuItemApi() {}

    public MenuItemApi(String name, double price, String description, int categoryId) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.categoryId = categoryId;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public int getCategoryId() { return categoryId; }
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
}