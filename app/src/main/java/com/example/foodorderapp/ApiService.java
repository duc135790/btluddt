package com.example.foodorderapp;

import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    @GET("api/menu")
    Call<ApiResponse<List<MenuItemApi>>> getMenu(@Query("search") String search);

    @POST("api/login")
    Call<ApiResponse<UserApi>> login(@Body Map<String, String> body);

    @POST("api/register")
    Call<ApiResponse<Void>> register(@Body Map<String, String> body);

    @POST("api/orders")
    Call<ApiResponse<Map<String, Integer>>> createOrder(@Body Map<String, Object> body);

    @GET("api/orders/{username}")
    Call<ApiResponse<List<OrderApi>>> getOrders(@Path("username") String username);

    @PUT("api/orders/{id}/status")
    Call<ApiResponse<Void>> updateOrderStatus(@Path("id") int id, @Body Map<String, String> body);

    @GET("api/kitchen/orders")
    Call<ApiResponse<List<OrderApi>>> getKitchenOrders();

    @GET("api/stats")
    Call<ApiResponse<StatsApi>> getStats();
}