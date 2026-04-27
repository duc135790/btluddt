package com.example.foodorderapp;

import com.google.gson.annotations.SerializedName;

public class StatsApi {
    @SerializedName("total_orders")
    private int totalOrders;
    @SerializedName("total_revenue")
    private double totalRevenue;

    public int getTotalOrders() { return totalOrders; }
    public double getTotalRevenue() { return totalRevenue; }
}