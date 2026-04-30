package com.example.foodorderapp;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
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
        Button btnEdit      = view.findViewById(R.id.btn_edit_profile);
        Button btnLogout    = view.findViewById(R.id.btn_logout);
        tvFullname.setText(prefs.getString("fullname", ""));
        tvUsername.setText("@" + prefs.getString("username", ""));
        btnEdit.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), EditProfileActivity.class))
        );
        btnLogout.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            CartManager.getInstance().clear();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        TextView tvFullname = getView().findViewById(R.id.tv_fullname);
        TextView tvUsername = getView().findViewById(R.id.tv_username);
        if (tvFullname != null) tvFullname.setText(prefs.getString("fullname", ""));
        if (tvUsername != null) tvUsername.setText("@" + prefs.getString("username", ""));
    }
}