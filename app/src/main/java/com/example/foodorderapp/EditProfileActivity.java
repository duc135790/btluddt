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
    private EditText etFullname, etPhone, etPassword;
    private ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        etFullname = findViewById(R.id.et_edit_fullname);
        etPhone    = findViewById(R.id.et_edit_phone);
        etPassword = findViewById(R.id.et_edit_password);
        ImageButton btnBack   = findViewById(R.id.btn_edit_back);
        Button btnSave        = findViewById(R.id.btn_edit_save);
        apiService = ApiClient.getClient().create(ApiService.class);
        SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        etFullname.setText(prefs.getString("fullname", ""));
        etPhone.setText(prefs.getString("phone", ""));
        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> {
            String fullname = etFullname.getText().toString().trim();
            String phone    = etPhone.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            if (fullname.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập họ tên!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.isEmpty() && password.length() < 6) {
                Toast.makeText(this, "Mật khẩu tối thiểu 6 ký tự!", Toast.LENGTH_SHORT).show();
                return;
            }
            String username = prefs.getString("username", "");
            // Nếu không đổi mật khẩu thì giữ nguyên
            String finalPassword = password.isEmpty() ? prefs.getString("password", "") : password;

            Map<String, String> body = new HashMap<>();
            body.put("fullname", fullname);
            body.put("phone", phone);
            body.put("password", finalPassword);

            btnSave.setEnabled(false);
            apiService.updateUser(username, body).enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                    btnSave.setEnabled(true);
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        // Cập nhật SharedPreferences
                        prefs.edit()
                                .putString("fullname", fullname)
                                .putString("phone", phone)
                                .putString("password", finalPassword)
                                .apply();
                        Toast.makeText(EditProfileActivity.this, "✅ Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                    btnSave.setEnabled(true);
                    Toast.makeText(EditProfileActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}