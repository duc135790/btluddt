package com.example.foodorderapp;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        Button btnLogin = findViewById(R.id.btn_login);
        TextView tvRegister = findViewById(R.id.tv_register);
        apiService = ApiClient.getClient().create(ApiService.class);

        // Kiểm tra đã login chưa
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        if (prefs.contains("username")) {
            goToMain();
            return;
        }

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ!", Toast.LENGTH_SHORT).show();
                return;
            }

            btnLogin.setEnabled(false);
            btnLogin.setText("Đang đăng nhập...");

            Map<String, String> body = new HashMap<>();
            body.put("username", username);
            body.put("password", password);

            apiService.login(body).enqueue(new Callback<ApiResponse<UserApi>>() {
                @Override
                public void onResponse(Call<ApiResponse<UserApi>> call, Response<ApiResponse<UserApi>> response) {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Đăng nhập");
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        UserApi user = response.body().getData();
                        prefs.edit()
                                .putString("username", user.getUsername())
                                .putString("fullname", user.getFullname())
                                .putString("role", user.getRole())
                                .apply();
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        goToMain();
                    } else {
                        Toast.makeText(LoginActivity.this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<UserApi>> call, Throwable t) {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Đăng nhập");
                    Toast.makeText(LoginActivity.this, "Lỗi kết nối server!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }

    private void goToMain() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}