package com.example.foodorderapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity extends AppCompatActivity {

    private EditText etName, etPhone, etDate, etTime, etGuests, etNote;
    private Spinner spTable;
    private List<TableApi> tableList = new ArrayList<>();
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        etName   = findViewById(R.id.et_book_name);
        etPhone  = findViewById(R.id.et_book_phone);
        etDate   = findViewById(R.id.et_book_date);
        etTime   = findViewById(R.id.et_book_time);
        etGuests = findViewById(R.id.et_book_guests);
        etNote   = findViewById(R.id.et_book_note);
        spTable  = findViewById(R.id.sp_book_table);
        ImageButton btnBack = findViewById(R.id.btn_book_back);
        Button btnSend = findViewById(R.id.btn_book_send);

        apiService = ApiClient.getClient().create(ApiService.class);
        loadTables();

        // Date picker
        etDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, y, m, d) ->
                    etDate.setText(d + "/" + (m+1) + "/" + y),
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // Time picker
        etTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(this, (view, h, min) ->
                    etTime.setText(String.format("%02d:%02d", h, min)),
                    c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true
            ).show();
        });

        btnBack.setOnClickListener(v -> finish());

        btnSend.setOnClickListener(v -> {
            String name   = etName.getText().toString().trim();
            String phone  = etPhone.getText().toString().trim();
            String date   = etDate.getText().toString().trim();
            String time   = etTime.getText().toString().trim();
            String guests = etGuests.getText().toString().trim();
            String note   = etNote.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty() || date.isEmpty()
                    || time.isEmpty() || guests.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            String tableNumber = null;
            if (!tableList.isEmpty()) {
                int pos = spTable.getSelectedItemPosition();
                if (pos >= 0 && pos < tableList.size())
                    tableNumber = tableList.get(pos).getTableNumber();
            }

            BookingApi booking = new BookingApi(name, phone, date, time,
                    Integer.parseInt(guests), tableNumber, note);

            btnSend.setEnabled(false);
            apiService.createBooking(booking).enqueue(new Callback<ApiResponse<Map<String, Integer>>>() {
                @Override
                public void onResponse(Call<ApiResponse<Map<String, Integer>>> call,
                                       Response<ApiResponse<Map<String, Integer>>> response) {
                    btnSend.setEnabled(true);
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Toast.makeText(BookingActivity.this,
                                "🎉 Đặt bàn thành công! Chúng tôi sẽ xác nhận sớm.", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(BookingActivity.this, "Lỗi đặt bàn!", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse<Map<String, Integer>>> call, Throwable t) {
                    btnSend.setEnabled(true);
                    Toast.makeText(BookingActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadTables() {
        apiService.getTables().enqueue(new Callback<ApiResponse<List<TableApi>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<TableApi>>> call,
                                   Response<ApiResponse<List<TableApi>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    tableList = response.body().getData();
                    List<String> names = new ArrayList<>();
                    names.add("-- Chưa chọn bàn --");
                    for (TableApi t : tableList)
                        names.add("Bàn " + t.getTableNumber() + " (" + t.getCapacity() + " người)");
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(BookingActivity.this,
                            android.R.layout.simple_spinner_item, names);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spTable.setAdapter(adapter);
                }
            }
            @Override public void onFailure(Call<ApiResponse<List<TableApi>>> call, Throwable t) {}
        });
    }
}