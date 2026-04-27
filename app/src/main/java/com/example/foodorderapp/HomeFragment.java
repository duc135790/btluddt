package com.example.foodorderapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private ListView lvMenu;
    private EditText etSearch;
    private List<MenuItemApi> menuList = new ArrayList<>();
    private ApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lvMenu   = view.findViewById(R.id.lv_menu);
        etSearch = view.findViewById(R.id.et_search);
        TextView tvGreeting = view.findViewById(R.id.tv_greeting);
        apiService = ApiClient.getClient().create(ApiService.class);

        SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", requireActivity().MODE_PRIVATE);
        tvGreeting.setText("Xin chào, " + prefs.getString("fullname", "bạn") + "! 👋");

        // Banner tự cuộn
        setupBanner(view);

        loadMenu(null);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                String kw = s.toString().trim();
                loadMenu(kw.isEmpty() ? null : kw);
            }
        });

        lvMenu.setOnItemClickListener((parent, v, position, id) -> {
            MenuItemApi item = menuList.get(position);
            Intent intent = new Intent(requireContext(), FoodDetailActivity.class);
            intent.putExtra("id",   item.getId());
            intent.putExtra("name", item.getName());
            intent.putExtra("price", item.getPrice());
            intent.putExtra("desc", item.getDescription());
            intent.putExtra("category_id", item.getCategoryId());
            startActivity(intent);
        });
    }

    private void setupBanner(View view) {
        int[] bannerImages = {
                R.drawable.banner1,
                R.drawable.banner2,
                R.drawable.banner3
        };
        String[] bannerTexts = {
                "Sang trọng",
                "Uy tín",
                "Tự nhiên"
        };
        ViewFlipper flipper = view.findViewById(R.id.view_flipper);
        for (int i = 0; i < bannerImages.length; i++) {
            View bannerView = LayoutInflater.from(requireContext()).inflate(R.layout.item_banner, flipper, false);
            ImageView img = bannerView.findViewById(R.id.iv_banner);
            TextView txt = bannerView.findViewById(R.id.tv_banner_text);
            img.setImageResource(bannerImages[i]);
            txt.setText(bannerTexts[i]);
            flipper.addView(bannerView);
        }
        flipper.setAutoStart(true);
        flipper.setFlipInterval(3000);
        flipper.startFlipping();
    }

    private void loadMenu(String search) {
        apiService.getMenu(search).enqueue(new Callback<ApiResponse<List<MenuItemApi>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<MenuItemApi>>> call,
                                   Response<ApiResponse<List<MenuItemApi>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    menuList = response.body().getData();
                    MenuItemApiAdapter adapter = new MenuItemApiAdapter(requireContext(), menuList, null, null);
                    lvMenu.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<MenuItemApi>>> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi tải menu!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}