package com.example.foodorderapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class AccountFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String fullname = prefs.getString("fullname", "");
        String role     = prefs.getString("role", "customer");

        TextView tvFullname    = view.findViewById(R.id.tv_fullname);
        TextView tvUsername    = view.findViewById(R.id.tv_username);
        TextView tvTotalOrders = view.findViewById(R.id.tv_total_orders);
        TextView tvTotalSpent  = view.findViewById(R.id.tv_total_spent);
        Button btnLogout       = view.findViewById(R.id.btn_logout);
        Button btnManageMenu   = view.findViewById(R.id.btn_manage_menu);
        View layoutAdmin       = view.findViewById(R.id.layout_admin);

        tvFullname.setText(fullname);
        tvUsername.setText("@" + prefs.getString("username", ""));

        if (role.equals("admin")) layoutAdmin.setVisibility(View.VISIBLE);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        NumberFormat fmt = NumberFormat.getInstance(new Locale("vi", "VN"));

        apiService.getStats().enqueue(new Callback<ApiResponse<StatsApi>>() {
            @Override
            public void onResponse(Call<ApiResponse<StatsApi>> call, Response<ApiResponse<StatsApi>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    StatsApi stats = response.body().getData();
                    tvTotalOrders.setText(String.valueOf(stats.getTotalOrders()));
                    tvTotalSpent.setText(fmt.format(stats.getTotalRevenue()) + "đ");
                }
            }
            @Override public void onFailure(Call<ApiResponse<StatsApi>> call, Throwable t) {}
        });

        // Admin bấm vào mở AdminActivity
        btnManageMenu.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), AdminActivity.class))
        );

        btnLogout.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            CartManager.getInstance().clear();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}