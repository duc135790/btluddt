package com.example.foodorderapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MenuItemApiAdapter extends ArrayAdapter<MenuItemApi> {

    public interface OnActionListener {
        void onAction(MenuItemApi item);
    }

    private List<MenuItemApi> list;
    private OnActionListener editListener;
    private OnActionListener deleteListener;

    public MenuItemApiAdapter(Context context, List<MenuItemApi> list,
                              OnActionListener editListener,
                              OnActionListener deleteListener) {
        super(context, 0, list);
        this.list           = list;
        this.editListener   = editListener;
        this.deleteListener = deleteListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_menu, parent, false);
        }

        MenuItemApi item = list.get(position);
        NumberFormat fmt = NumberFormat.getInstance(new Locale("vi", "VN"));

        ((TextView) convertView.findViewById(R.id.tv_name)).setText(item.getName());
        ((TextView) convertView.findViewById(R.id.tv_price)).setText(fmt.format(item.getPrice()) + "đ");
        ((TextView) convertView.findViewById(R.id.tv_desc)).setText(item.getDescription());

        Button btnEdit   = convertView.findViewById(R.id.btn_edit);
        Button btnDelete = convertView.findViewById(R.id.btn_delete);

        if (editListener != null && btnEdit != null)
            btnEdit.setOnClickListener(v -> editListener.onAction(item));
        else if (btnEdit != null)
            btnEdit.setVisibility(View.GONE);

        if (deleteListener != null && btnDelete != null)
            btnDelete.setOnClickListener(v -> deleteListener.onAction(item));
        else if (btnDelete != null)
            btnDelete.setVisibility(View.GONE);

        return convertView;
    }
}