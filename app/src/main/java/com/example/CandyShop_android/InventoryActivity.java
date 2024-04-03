package com.example.CandyShop_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project2_gdoan.R;
import com.google.android.material.snackbar.Snackbar;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;

public class InventoryActivity extends AppCompatActivity {

    private List<String> itemNamesList = new ArrayList<>();
    private List<String> itemDescriptList = new ArrayList<>();
    private List<String> itemPricesList = new ArrayList<>();
    private List<String> itemStocksList = new ArrayList<>();
    private static final int EDIT_ITEM_REQUEST = 1;
    private static final int ADD_ITEM_REQUEST = 2;
    private CustomAdap adapter;
    private InventDB dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invent_open);

        dbHelper = new InventDB(this);

        ListView listView = findViewById(R.id.listView);

        adapter = new CustomAdap(this, itemNamesList, itemPricesList, itemStocksList);

        listView.setAdapter(adapter);

        loadDataFromDatabase();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = itemNamesList.get(position) + ": " + itemDescriptList.get(position);
                Snackbar.make(view, selectedItem, Snackbar.LENGTH_INDEFINITE).setAction("Action", null).show();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String itemName = itemNamesList.get(position);
                String itemDescription = itemDescriptList.get(position);
                String itemPrice = itemPricesList.get(position);
                String itemStock = itemStocksList.get(position);

                Intent intent = new Intent(InventoryActivity.this, ItemEdit.class);
                intent.putExtra("position", position);
                intent.putExtra("itemName", itemName);
                intent.putExtra("itemDescription", itemDescription);
                intent.putExtra("itemPrice", itemPrice);
                intent.putExtra("itemStock", itemStock);
                startActivityForResult(intent, EDIT_ITEM_REQUEST);

                return true;
            }
        });



        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InventoryActivity.this, AddItemAct.class);
                startActivityForResult(intent, ADD_ITEM_REQUEST);
            }
        });
    }

    private void loadDataFromDatabase() {
        itemNamesList.clear();
        itemDescriptList.clear();
        itemPricesList.clear();
        itemStocksList.clear();

        Cursor cursor = dbHelper.readAllItems();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int nameIndex = cursor.getColumnIndex(InventDB.ITEM_NAME);
                int descriptionIndex = cursor.getColumnIndex(InventDB.ITEM_DESCRIPTION);
                int priceIndex = cursor.getColumnIndex(InventDB.ITEM_PRICE);
                int stockIndex = cursor.getColumnIndex(InventDB.ITEM_STOCK);

                if (nameIndex != -1 && descriptionIndex != -1 && priceIndex != -1 && stockIndex != -1) {
                    String name = cursor.getString(nameIndex);
                    String description = cursor.getString(descriptionIndex);
                    String price = cursor.getString(priceIndex);
                    String stock = cursor.getString(stockIndex);

                    itemNamesList.add(name);
                    itemDescriptList.add(description);
                    itemPricesList.add(price);
                    itemStocksList.add(stock);
                }
            } while (cursor.moveToNext());

            cursor.close();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_ITEM_REQUEST && resultCode == RESULT_OK) {
            String editedDescription = data.getStringExtra("editedDescription");
            String editedPrice = data.getStringExtra("editedPrice");
            String editedStock = data.getStringExtra("editedStock");

            int position = data.getIntExtra("position", -1);

            if (position != -1) {
                itemDescriptList.set(position, editedDescription);
                itemPricesList.set(position, editedPrice);
                itemStocksList.set(position, editedStock);
                adapter.notifyDataSetChanged();
            }
        }

        if (requestCode == ADD_ITEM_REQUEST && resultCode == RESULT_OK) {
            String itemName = data.getStringExtra("itemName");
            String itemDescription = data.getStringExtra("itemDescription");
            String itemPrice = data.getStringExtra("itemPrice");
            String itemStock = data.getStringExtra("itemStock");

            itemNamesList.add(itemName);
            itemDescriptList.add(itemDescription);
            itemPricesList.add(itemPrice);
            itemStocksList.add(itemStock);

            dbHelper.insertItem(itemName, itemDescription, itemPrice, itemStock);

            adapter.notifyDataSetChanged();

        }

        if (requestCode == EDIT_ITEM_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.getBooleanExtra("deleteItem", false)) {
                    int position = data.getIntExtra("position", -1);
                    if (position != -1) {
                        String itemName = itemNamesList.get(position);

                        itemNamesList.remove(position);
                        itemDescriptList.remove(position);
                        itemPricesList.remove(position);
                        itemStocksList.remove(position);

                        adapter.notifyDataSetChanged();

                        deleteItemFromDatabase(itemName);
                    }
                }
            }
        }

    }

    private void deleteItemFromDatabase(String itemName) {
        dbHelper.deleteItem(itemName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

