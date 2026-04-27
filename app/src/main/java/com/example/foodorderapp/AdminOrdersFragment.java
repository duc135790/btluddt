package com.example.foodorderapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import java.text.NumberFormat;
import java.util.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminOrdersFragment extends Fragment {

    private List<OrderApi> orderList = new ArrayList<>();
    private ApiService apiService;
    private ListView lvOrders;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lvOrders = view.findViewById(R.id.lv_admin_orders);
        apiService = ApiClient.getClient().create(ApiService.class);
        loadOrders();
    }

    private void loadOrders() {
        apiService.getKitchenOrders().enqueue(new Callback<ApiResponse<List<OrderApi>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<OrderApi>>> call,
                                   Response<ApiResponse<List<OrderApi>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    orderList = response.body().getData();
                    setupAdapter();
                }
            }
            @Override public void onFailure(Call<ApiResponse<List<OrderApi>>> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi tải đơn!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupAdapter() {
        NumberFormat fmt = NumberFormat.getInstance(new Locale("vi", "VN"));
        ArrayAdapter<OrderApi> adapter = new ArrayAdapter<OrderApi>(
                requireContext(), R.layout.item_admin_order, orderList) {
            @NonNull @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                if (convertView == null)
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_admin_order, parent, false);
                OrderApi order = orderList.get(position);
                ((TextView) convertView.findViewById(R.id.tv_aorder_id)).setText("Đơn #" + order.getId() + " — " + order.getUsername());
                ((TextView) convertView.findViewById(R.id.tv_aorder_items)).setText(order.getItems());
                ((TextView) convertView.findViewById(R.id.tv_aorder_total)).setText(fmt.format(order.getTotal()) + "đ");
                ((TextView) convertView.findViewById(R.id.tv_aorder_date)).setText(order.getCreatedAt());
                TextView tvStatus = convertView.findViewById(R.id.tv_aorder_status);
                setStatusStyle(tvStatus, order.getStatus());
                convertView.findViewById(R.id.btn_change_status).setOnClickListener(v -> showStatusDialog(order));
                return convertView;
            }
        };
        lvOrders.setAdapter(adapter);
    }

    private void setStatusStyle(TextView tv, String status) {
        switch (status) {
            case "pending":   tv.setBackgroundColor(0xFFFF9800); tv.setText("⏳ Chờ xử lý"); break;
            case "preparing": tv.setBackgroundColor(0xFF2196F3); tv.setText("🔥 Đang làm"); break;
            case "ready":     tv.setBackgroundColor(0xFF4CAF50); tv.setText("✅ Sẵn sàng"); break;
            case "done":      tv.setBackgroundColor(0xFF9E9E9E); tv.setText("✔ Hoàn thành"); break;
            case "cancelled": tv.setBackgroundColor(0xFFF44336); tv.setText("❌ Đã hủy"); break;
            default: tv.setText(status);
        }
    }

    private void showStatusDialog(OrderApi order) {
        String[] statuses = {"pending", "preparing", "ready", "done", "cancelled"};
        String[] labels   = {"⏳ Chờ xử lý", "🔥 Đang làm", "✅ Sẵn sàng", "✔ Hoàn thành", "❌ Đã hủy"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Cập nhật đơn #" + order.getId())
                .setItems(labels, (dialog, which) -> {
                    Map<String, String> body = new HashMap<>();
                    body.put("status", statuses[which]);
                    apiService.updateOrderStatus(order.getId(), body).enqueue(new Callback<ApiResponse<Void>>() {
                        @Override public void onResponse(Call<ApiResponse<Void>> c, Response<ApiResponse<Void>> r) {
                            Toast.makeText(requireContext(), "✅ Đã cập nhật!", Toast.LENGTH_SHORT).show();
                            loadOrders();
                        }
                        @Override public void onFailure(Call<ApiResponse<Void>> c, Throwable t) {}
                    });
                }).show();
    }
}