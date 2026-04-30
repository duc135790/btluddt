package com.example.foodorderapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        NumberFormat fmt = NumberFormat.getInstance(new Locale("vi", "VN"));
        EditText etName    = findViewById(R.id.et_pay_name);
        EditText etPhone   = findViewById(R.id.et_pay_phone);
        EditText etAddress = findViewById(R.id.et_pay_address);
        TextView tvTotal   = findViewById(R.id.tv_pay_total);
        TextView tvItems   = findViewById(R.id.tv_pay_items);
        RadioGroup rgPayment   = findViewById(R.id.rg_payment);
        LinearLayout layoutQr  = findViewById(R.id.layout_qr);
        Button btnConfirm      = findViewById(R.id.btn_confirm_payment);
        ImageButton btnBack    = findViewById(R.id.btn_pay_back);

        double total = CartManager.getInstance().getTotal();
        tvTotal.setText("Tổng: " + fmt.format(total) + "đ");
        tvItems.setText(CartManager.getInstance().getItemsSummary());

        rgPayment.setOnCheckedChangeListener((group, checkedId) ->
                layoutQr.setVisibility(checkedId == R.id.rb_online ? View.VISIBLE : View.GONE)
        );
        btnBack.setOnClickListener(v -> finish());

        SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String username = prefs.getString("username", "");

        btnConfirm.setOnClickListener(v -> {
            String name    = etName.getText().toString().trim();
            String phone   = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ!", Toast.LENGTH_SHORT).show();
                return;
            }
            String payMethod = rgPayment.getCheckedRadioButtonId() == R.id.rb_online ? "online" : "cash";
            Map<String, Object> body = new HashMap<>();
            body.put("username", username);
            body.put("items", CartManager.getInstance().getItemsSummary());
            body.put("total", total);
            body.put("address", address);
            body.put("phone", phone);
            body.put("payment", payMethod);
            btnConfirm.setEnabled(false);
            ApiClient.getClient().create(ApiService.class).createOrder(body)
                    .enqueue(new Callback<ApiResponse<Map<String, Integer>>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Map<String, Integer>>> call,
                                               Response<ApiResponse<Map<String, Integer>>> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                CartManager.getInstance().clear();
                                Toast.makeText(PaymentActivity.this, "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(PaymentActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            } else {
                                btnConfirm.setEnabled(true);
                                Toast.makeText(PaymentActivity.this, "Lỗi đặt hàng!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<ApiResponse<Map<String, Integer>>> call, Throwable t) {
                            btnConfirm.setEnabled(true);
                            Toast.makeText(PaymentActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}