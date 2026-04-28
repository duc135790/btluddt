package com.example.foodorderapp;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodDetailActivity extends AppCompatActivity {
    private int quantity = 1;
    private MenuItemApi item;
    private Spinner spTable;
    private List<TableApi> tableList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        item = new MenuItemApi(
                getIntent().getStringExtra("name"),
                getIntent().getDoubleExtra("price", 0),
                getIntent().getStringExtra("desc"),
                getIntent().getIntExtra("category_id", 1)
        );
        item.setId(getIntent().getIntExtra("id", 0));

        NumberFormat fmt = NumberFormat.getInstance(new Locale("vi", "VN"));
        TextView tvName  = findViewById(R.id.tv_detail_name);
        TextView tvPrice = findViewById(R.id.tv_detail_price);
        TextView tvDesc  = findViewById(R.id.tv_detail_desc);
        TextView tvQty   = findViewById(R.id.tv_quantity);
        ImageView ivFood = findViewById(R.id.iv_detail_food);
        ImageButton btnMinus  = findViewById(R.id.btn_minus);
        ImageButton btnPlus   = findViewById(R.id.btn_plus);
        CheckBox cbSpicy      = findViewById(R.id.cb_spicy);
        CheckBox cbExtraSauce = findViewById(R.id.cb_extra_sauce);
        CheckBox cbNoOnion    = findViewById(R.id.cb_no_onion);
        EditText etNote       = findViewById(R.id.et_note);
        Button btnAdd         = findViewById(R.id.btn_add_to_cart);
        ImageButton btnBack   = findViewById(R.id.btn_back);
        spTable = findViewById(R.id.sp_table);

        tvName.setText(item.getName());
        tvPrice.setText(fmt.format(item.getPrice()) + "đ");
        tvDesc.setText(item.getDescription());
        tvQty.setText(String.valueOf(quantity));

        // Load ảnh từ drawable theo image_name
        String imageName = getIntent().getStringExtra("image_name");
        if (imageName != null && !imageName.isEmpty()) {
            int resId = getResources().getIdentifier(imageName, "drawable", getPackageName());
            if (resId != 0) ivFood.setImageResource(resId);
            else ivFood.setImageResource(R.drawable.food_default);
        } else {
            ivFood.setImageResource(R.drawable.food_default);
        }

        // Load danh sách bàn
        ApiClient.getClient().create(ApiService.class).getTables()
                .enqueue(new Callback<ApiResponse<List<TableApi>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<TableApi>>> call,
                                           Response<ApiResponse<List<TableApi>>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            tableList = response.body().getData();
                            List<String> names = new ArrayList<>();
                            names.add("-- Chưa chọn bàn --");
                            for (TableApi t : tableList)
                                names.add("Bàn " + t.getTableNumber() + " (" + t.getCapacity() + " người)");
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(FoodDetailActivity.this,
                                    android.R.layout.simple_spinner_item, names);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spTable.setAdapter(adapter);
                        }
                    }
                    @Override public void onFailure(Call<ApiResponse<List<TableApi>>> call, Throwable t) {}
                });

        btnBack.setOnClickListener(v -> finish());
        btnMinus.setOnClickListener(v -> { if (quantity > 1) { quantity--; tvQty.setText(String.valueOf(quantity)); } });
        btnPlus.setOnClickListener(v -> { quantity++; tvQty.setText(String.valueOf(quantity)); });

        btnAdd.setOnClickListener(v -> {
            StringBuilder extras = new StringBuilder();
            if (cbSpicy.isChecked())      extras.append("Cay, ");
            if (cbExtraSauce.isChecked()) extras.append("Thêm sốt, ");
            if (cbNoOnion.isChecked())    extras.append("Không hành, ");

            // Lấy bàn đã chọn
            String tableNumber = null;
            int pos = spTable.getSelectedItemPosition();
            if (pos > 0 && pos <= tableList.size())
                tableNumber = tableList.get(pos - 1).getTableNumber();

            CartItem ci = new CartItem();
            ci.setMenuItemApi(item);
            ci.setQuantity(quantity);
            ci.setNote(etNote.getText().toString().trim());
            ci.setExtras(extras.toString());
            ci.setTableNumber(tableNumber);
            CartManager.getInstance().addItem(ci);
            Toast.makeText(this, "✅ Đã thêm " + item.getName() + " x" + quantity, Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}