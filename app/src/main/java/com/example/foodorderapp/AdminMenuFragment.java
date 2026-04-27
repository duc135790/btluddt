package com.example.foodorderapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import java.text.NumberFormat;
import java.util.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminMenuFragment extends Fragment {

    private ListView lvMenu;
    private List<MenuItemApi> menuList = new ArrayList<>();
    private ApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lvMenu = view.findViewById(R.id.lv_admin_menu);
        EditText etSearch = view.findViewById(R.id.et_admin_search);
        Button btnAdd = view.findViewById(R.id.btn_admin_add);
        apiService = ApiClient.getClient().create(ApiService.class);
        loadMenu(null);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                loadMenu(s.toString().trim().isEmpty() ? null : s.toString().trim());
            }
        });
        btnAdd.setOnClickListener(v -> showMenuDialog(null));
    }

    private void loadMenu(String search) {
        apiService.getMenu(search).enqueue(new Callback<ApiResponse<List<MenuItemApi>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<MenuItemApi>>> call,
                                   Response<ApiResponse<List<MenuItemApi>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    menuList = response.body().getData();
                    setupAdapter();
                }
            }
            @Override public void onFailure(Call<ApiResponse<List<MenuItemApi>>> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi tải menu!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupAdapter() {
        NumberFormat fmt = NumberFormat.getInstance(new Locale("vi", "VN"));
        ArrayAdapter<MenuItemApi> adapter = new ArrayAdapter<MenuItemApi>(
                requireContext(), R.layout.item_admin_menu, menuList) {
            @NonNull @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                if (convertView == null)
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_admin_menu, parent, false);
                MenuItemApi item = menuList.get(position);
                ((TextView) convertView.findViewById(R.id.tv_admin_name)).setText(item.getName());
                ((TextView) convertView.findViewById(R.id.tv_admin_price)).setText(fmt.format(item.getPrice()) + "đ");
                ((TextView) convertView.findViewById(R.id.tv_admin_desc)).setText(item.getDescription());
                convertView.findViewById(R.id.btn_admin_edit).setOnClickListener(v -> showMenuDialog(item));
                convertView.findViewById(R.id.btn_admin_delete).setOnClickListener(v -> confirmDelete(item));
                return convertView;
            }
        };
        lvMenu.setAdapter(adapter);
    }

    private void showMenuDialog(MenuItemApi item) {
        boolean isEdit = item != null;
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_menu_form, null);
        EditText etName    = dialogView.findViewById(R.id.et_form_name);
        EditText etPrice   = dialogView.findViewById(R.id.et_form_price);
        EditText etDesc    = dialogView.findViewById(R.id.et_form_desc);
        Spinner spCategory = dialogView.findViewById(R.id.sp_category);

        String[] categories = {"Cơm", "Phở & Bún", "Đồ uống"};
        int[] categoryIds   = {1, 2, 3};
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, categories);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(catAdapter);

        if (isEdit) {
            etName.setText(item.getName());
            etPrice.setText(String.valueOf((long) item.getPrice()));
            etDesc.setText(item.getDescription());
            for (int i = 0; i < categoryIds.length; i++)
                if (categoryIds[i] == item.getCategoryId()) spCategory.setSelection(i);
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(isEdit ? "✏ Sửa món" : "➕ Thêm món mới")
                .setView(dialogView)
                .setPositiveButton(isEdit ? "Cập nhật" : "Thêm", (dialog, which) -> {
                    String name  = etName.getText().toString().trim();
                    String price = etPrice.getText().toString().trim();
                    String desc  = etDesc.getText().toString().trim();
                    int catId    = categoryIds[spCategory.getSelectedItemPosition()];
                    if (name.isEmpty() || price.isEmpty()) {
                        Toast.makeText(requireContext(), "Vui lòng nhập đủ!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    MenuItemApi newItem = new MenuItemApi(name, Double.parseDouble(price), desc, catId);
                    if (isEdit) {
                        apiService.updateMenu(item.getId(), newItem).enqueue(new Callback<ApiResponse<Void>>() {
                            @Override public void onResponse(Call<ApiResponse<Void>> c, Response<ApiResponse<Void>> r) {
                                Toast.makeText(requireContext(), "✅ Đã cập nhật!", Toast.LENGTH_SHORT).show();
                                loadMenu(null);
                            }
                            @Override public void onFailure(Call<ApiResponse<Void>> c, Throwable t) {}
                        });
                    } else {
                        apiService.addMenu(newItem).enqueue(new Callback<ApiResponse<Map<String, Integer>>>() {
                            @Override public void onResponse(Call<ApiResponse<Map<String, Integer>>> c,
                                                             Response<ApiResponse<Map<String, Integer>>> r) {
                                Toast.makeText(requireContext(), "✅ Đã thêm!", Toast.LENGTH_SHORT).show();
                                loadMenu(null);
                            }
                            @Override public void onFailure(Call<ApiResponse<Map<String, Integer>>> c, Throwable t) {}
                        });
                    }
                })
                .setNegativeButton("Hủy", null).show();
    }

    private void confirmDelete(MenuItemApi item) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa món")
                .setMessage("Xóa \"" + item.getName() + "\"?")
                .setPositiveButton("Xóa", (d, w) ->
                        apiService.deleteMenu(item.getId()).enqueue(new Callback<ApiResponse<Void>>() {
                            @Override public void onResponse(Call<ApiResponse<Void>> c, Response<ApiResponse<Void>> r) {
                                Toast.makeText(requireContext(), "🗑 Đã xóa!", Toast.LENGTH_SHORT).show();
                                loadMenu(null);
                            }
                            @Override public void onFailure(Call<ApiResponse<Void>> c, Throwable t) {}
                        })
                ).setNegativeButton("Hủy", null).show();
    }
}