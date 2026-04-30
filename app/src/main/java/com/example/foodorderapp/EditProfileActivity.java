package com.example.foodorderapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    private EditText etFullname, etPhone;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etFullname = findViewById(R.id.et_edit_fullname);
        etPhone    = findViewById(R.id.et_edit_phone);
        ImageButton btnBack = findViewById(R.id.btn_edit_back);
        Button btnSave      = findViewById(R.id.btn_edit_save);
        Button btnHelp      = findViewById(R.id.btn_help);

        apiService = ApiClient.getClient().create(ApiService.class);
        SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        etFullname.setText(prefs.getString("fullname", ""));
        etPhone.setText(prefs.getString("phone", ""));

        btnBack.setOnClickListener(v -> finish());

        // Nut Tro giup: goi dien 1800
        btnHelp.setOnClickListener(v -> {
            android.content.Intent callIntent = new android.content.Intent(android.content.Intent.ACTION_DIAL);
            callIntent.setData(android.net.Uri.parse("tel:1800"));
            startActivity(callIntent);
        });

        btnSave.setOnClickListener(v -> {
            String fullname = etFullname.getText().toString().trim();
            String phone    = etPhone.getText().toString().trim();

            if (fullname.isEmpty()) {
                Toast.makeText(this, "Vui long nhap ho ten!", Toast.LENGTH_SHORT).show();
                return;
            }

            String username = prefs.getString("username", "");
            Map<String, String> body = new HashMap<>();
            body.put("fullname", fullname);
            body.put("phone", phone);

            btnSave.setEnabled(false);
            apiService.updateUser(username, body).enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                    btnSave.setEnabled(true);
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        prefs.edit()
                                .putString("fullname", fullname)
                                .putString("phone", phone)
                                .apply();
                        Toast.makeText(EditProfileActivity.this, "Cap nhat thanh cong!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Cap nhat that bai!", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                    btnSave.setEnabled(true);
                    Toast.makeText(EditProfileActivity.this, "Loi ket noi!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}