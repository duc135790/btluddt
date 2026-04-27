package com.example.foodorderapp;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.NumberFormat;
import java.util.Locale;

public class FoodDetailActivity extends AppCompatActivity {
    private int quantity = 1;
    private MenuItemApi item;

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
        ImageButton btnMinus  = findViewById(R.id.btn_minus);
        ImageButton btnPlus   = findViewById(R.id.btn_plus);
        CheckBox cbSpicy      = findViewById(R.id.cb_spicy);
        CheckBox cbExtraSauce = findViewById(R.id.cb_extra_sauce);
        CheckBox cbNoOnion    = findViewById(R.id.cb_no_onion);
        EditText etNote       = findViewById(R.id.et_note);
        Button btnAdd         = findViewById(R.id.btn_add_to_cart);
        ImageButton btnBack   = findViewById(R.id.btn_back);

        tvName.setText(item.getName());
        tvPrice.setText(fmt.format(item.getPrice()) + "đ");
        tvDesc.setText(item.getDescription());
        tvQty.setText(String.valueOf(quantity));

        btnBack.setOnClickListener(v -> finish());
        btnMinus.setOnClickListener(v -> { if (quantity > 1) { quantity--; tvQty.setText(String.valueOf(quantity)); } });
        btnPlus.setOnClickListener(v -> { quantity++; tvQty.setText(String.valueOf(quantity)); });

        btnAdd.setOnClickListener(v -> {
            StringBuilder extras = new StringBuilder();
            if (cbSpicy.isChecked())      extras.append("Cay, ");
            if (cbExtraSauce.isChecked()) extras.append("Thêm sốt, ");
            if (cbNoOnion.isChecked())    extras.append("Không hành, ");
            CartItem ci = new CartItem();
            ci.setMenuItemApi(item);
            ci.setQuantity(quantity);
            ci.setNote(etNote.getText().toString().trim());
            ci.setExtras(extras.toString());
            CartManager.getInstance().addItem(ci);
            Toast.makeText(this, "✅ Đã thêm " + item.getName() + " x" + quantity, Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}