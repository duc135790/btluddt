package com.example.foodorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartFragment extends Fragment {

    private ListView lvCart;
    private TextView tvTotal;
    private List<CartItem> cartItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lvCart  = view.findViewById(R.id.lv_cart);
        tvTotal = view.findViewById(R.id.tv_total);
        Button btnOrder = view.findViewById(R.id.btn_order);
        loadCart();
        btnOrder.setOnClickListener(v -> {
            if (CartManager.getInstance().getCount() == 0) {
                Toast.makeText(requireContext(), "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(requireContext(), PaymentActivity.class));
        });
    }

    private void loadCart() {
        cartItems = CartManager.getInstance().getItems();
        NumberFormat fmt = NumberFormat.getInstance(new Locale("vi", "VN"));

        ArrayAdapter<CartItem> adapter = new ArrayAdapter<CartItem>(
                requireContext(), R.layout.item_cart, cartItems) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                if (convertView == null)
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_cart, parent, false);
                CartItem ci = cartItems.get(position);
                MenuItemApi item = ci.getMenuItemApi();
                ((TextView) convertView.findViewById(R.id.tv_name)).setText(item.getName() + " x" + ci.getQuantity());
                ((TextView) convertView.findViewById(R.id.tv_price)).setText(fmt.format(item.getPrice() * ci.getQuantity()) + "đ");
                TextView tvNote = convertView.findViewById(R.id.tv_note);
                if (ci.getNote() != null && !ci.getNote().isEmpty()) {
                    tvNote.setVisibility(View.VISIBLE);
                    tvNote.setText("📝 " + ci.getNote());
                } else {
                    tvNote.setVisibility(View.GONE);
                }
                convertView.findViewById(R.id.btn_remove).setOnClickListener(v -> {
                    CartManager.getInstance().getItems().remove(position);
                    loadCart();
                });
                return convertView;
            }
        };
        lvCart.setAdapter(adapter);
        tvTotal.setText(fmt.format(CartManager.getInstance().getTotal()) + "đ");
    }

    @Override
    public void onResume() { super.onResume(); loadCart(); }
}