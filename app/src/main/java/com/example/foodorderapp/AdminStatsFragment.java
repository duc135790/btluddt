package com.example.foodorderapp;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import java.text.NumberFormat;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminStatsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvOrders  = view.findViewById(R.id.tv_stats_orders);
        TextView tvRevenue = view.findViewById(R.id.tv_stats_revenue);
        NumberFormat fmt = NumberFormat.getInstance(new Locale("vi", "VN"));

        ApiClient.getClient().create(ApiService.class).getStats()
                .enqueue(new Callback<ApiResponse<StatsApi>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<StatsApi>> call, Response<ApiResponse<StatsApi>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            StatsApi stats = response.body().getData();
                            tvOrders.setText(String.valueOf(stats.getTotalOrders()));
                            tvRevenue.setText(fmt.format(stats.getTotalRevenue()) + "đ");
                        }
                    }
                    @Override public void onFailure(Call<ApiResponse<StatsApi>> call, Throwable t) {
                        Toast.makeText(requireContext(), "Lỗi tải thống kê!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}