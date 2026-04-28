package com.example.foodorderapp;

public class BookingApi {
    private String fullname;
    private String phone;
    private String date;
    private String time;
    private int guests;
    private String table_number;
    private String note;

    public BookingApi(String fullname, String phone, String date,
                      String time, int guests, String table_number, String note) {
        this.fullname = fullname;
        this.phone = phone;
        this.date = date;
        this.time = time;
        this.guests = guests;
        this.table_number = table_number;
        this.note = note;
    }
}