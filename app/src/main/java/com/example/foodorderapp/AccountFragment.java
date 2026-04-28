package com.example.foodorderapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class AccountFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        TextView tvFullname = view.findViewById(R.id.tv_fullname);
        TextView tvUsername = view.findViewById(R.id.tv_username);
        Button btnLogout    = view.findViewById(R.id.btn_logout);
        Button btnBooking   = view.findViewById(R.id.btn_booking);
        Button btnSupport   = view.findViewById(R.id.btn_support);

        tvFullname.setText(prefs.getString("fullname", ""));
        tvUsername.setText("@" + prefs.getString("username", ""));

        btnBooking.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), BookingActivity.class))
        );

        btnSupport.setOnClickListener(v -> showSupportDialog());

        btnLogout.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            CartManager.getInstance().clear();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void showSupportDialog() {
        String content =
                "📋 QUY ĐỊNH & HỖ TRỢ KHÁCH HÀNG\n\n" +
                        "1️⃣  Thời gian phục vụ: 10:00 - 22:00 hàng ngày.\n\n" +
                        "2️⃣  Đặt bàn trước ít nhất 2 giờ để đảm bảo chỗ ngồi.\n\n" +
                        "3️⃣  Trẻ em dưới 5 tuổi không tính phí chỗ ngồi.\n\n" +
                        "4️⃣  Hotline hỗ trợ: 1800-xxxx (miễn phí, 8:00-22:00).\n\n" +
                        "5️⃣  Mọi khiếu nại vui lòng liên hệ quản lý tại quầy hoặc qua hotline.\n\n" +
                        "🎁 VOUCHER:\n" +
                        "• Nhập mã WELCOME10 giảm 10% cho lần đặt đầu tiên.\n" +
                        "• Sinh nhật giảm 15% khi xuất trình CCCD.\n" +
                        "• Nhóm từ 10 người giảm 5% toàn bộ thực đơn.";

        new AlertDialog.Builder(requireContext())
                .setTitle("💬 Hỗ trợ & Ưu đãi")
                .setMessage(content)
                .setPositiveButton("Đóng", null)
                .show();
    }
}