package com.example.foodorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullname, etPhone, etUsername, etPassword;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFullname = findViewById(R.id.et_fullname);
        etPhone    = findViewById(R.id.et_phone);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        Button btnRegister = findViewById(R.id.btn_register);
        TextView tvLogin   = findViewById(R.id.tv_login);
        apiService = ApiClient.getClient().create(ApiService.class);

        btnRegister.setOnClickListener(v -> {
            String fullname = etFullname.getText().toString().trim();
            String phone    = etPhone.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (fullname.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 6) {
                Toast.makeText(this, "Mật khẩu tối thiểu 6 ký tự!", Toast.LENGTH_SHORT).show();
                return;
            }

            btnRegister.setEnabled(false);

            Map<String, String> body = new HashMap<>();
            body.put("username", username);
            body.put("password", password);
            body.put("fullname", fullname);
            body.put("phone", phone);

            apiService.register(body).enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                    btnRegister.setEnabled(true);
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Tên đăng nhập đã tồn tại!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                    btnRegister.setEnabled(true);
                    Toast.makeText(RegisterActivity.this, "Lỗi kết nối server!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        tvLogin.setOnClickListener(v -> finish());
    }
}