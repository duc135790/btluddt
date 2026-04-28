package com.example.foodorderapp;

import com.google.gson.annotations.SerializedName;

public class TableApi {
    private int id;
    @SerializedName("table_number")
    private String tableNumber;
    private int capacity;
    @SerializedName("is_available")
    private int isAvailable;

    public int getId() { return id; }
    public String getTableNumber() { return tableNumber; }
    public int getCapacity() { return capacity; }
    public boolean isAvailable() { return isAvailable == 1; }
}