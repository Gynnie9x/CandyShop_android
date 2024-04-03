package com.example.CandyShop_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.project2_gdoan.R;

import java.util.List;

public class CustomAdap extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> itemNames;
    private final List<String> itemPrices;
    private final List<String> itemStocks;

    public CustomAdap(Context context, List<String> itemNames, List<String> itemPrices, List<String> itemStocks) {
        super(context, R.layout.list_layout, itemNames);
        this.context = context;
        this.itemNames = itemNames;
        this.itemPrices = itemPrices;
        this.itemStocks = itemStocks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_layout, parent, false);

        TextView itemNameView = rowView.findViewById(R.id.item_name);
        TextView itemPriceView = rowView.findViewById(R.id.item_price);
        TextView itemStockView = rowView.findViewById(R.id.item_stock);

        itemNameView.setText(itemNames.get(position));

        // Format the item price
        String priceFormat = "Cost per item: $" + itemPrices.get(position);
        itemPriceView.setText(priceFormat);

        // Format the item stock
        String stockFormat = "In stock: " + itemStocks.get(position);
        itemStockView.setText(stockFormat);

        return rowView;
    }
}
