package com.example.foodorderapp;

import com.google.gson.annotations.SerializedName;

public class OrderApi {
    private int id;
    private String username;
    private String items;
    private double total;
    private String status;
    @SerializedName("created_at")
    private String createdAt;

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getItems() { return items; }
    public double getTotal() { return total; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
}