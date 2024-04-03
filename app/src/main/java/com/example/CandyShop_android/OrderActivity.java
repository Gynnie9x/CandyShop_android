package com.example.CandyShop_android;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2_gdoan.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrderActivity extends AppCompatActivity {

    private Spinner spinner;
    private EditText howText;
    private TextView textView7;
    private int totalCost = 0;
    private InventDB inventDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_handle);

        spinner = findViewById(R.id.spinner);
        howText = findViewById(R.id.howText);
        textView7 = findViewById(R.id.textView7);
        inventDB = new InventDB(this);

        Cursor cursor = inventDB.readAllItems();
        if (cursor == null || !cursor.moveToFirst()) {
            Toast.makeText(this, "No candy in the store", Toast.LENGTH_SHORT).show();
        } else {

            spinnerDisplay();

            Button addToOrderButton = findViewById(R.id.addOButton);
            addToOrderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addToOrder();
                }
            });

            Button removeFromOrderButton = findViewById(R.id.removeOButton);
            removeFromOrderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeFromOrder();
                }
            });

            Button finishOrderButton = findViewById(R.id.finButton);
            finishOrderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishOrder();
                }
            });
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    private void spinnerDisplay() {
        Cursor cursor = inventDB.readAllItems();
        List<String> itemList = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            int itemNameIndex = cursor.getColumnIndex(InventDB.ITEM_NAME);

            do {
                if (itemNameIndex != -1) {
                    String itemName = cursor.getString(itemNameIndex);
                    itemList.add(itemName);
                } else {
                    Toast.makeText(this, "Column 'ITEM_NAME' not found in cursor.", Toast.LENGTH_SHORT).show();
                }
            } while (cursor.moveToNext());

            cursor.close();
        } else {
            Toast.makeText(this, "No candy in the store.", Toast.LENGTH_SHORT).show();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void addToOrder() {
        String selectedItem = spinner.getSelectedItem().toString();
        String quantityText = howText.getText().toString();

        if (selectedItem.isEmpty() || quantityText.isEmpty()) {
            Toast.makeText(this, "Please select an item and enter a quantity.", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid quantity input. Please enter a valid number.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Take the item details from the database
        String itemPrice = inventDB.getItemPrice(selectedItem);
        String itemStock = inventDB.getItemStock(selectedItem);

        if (itemPrice == null || itemPrice.isEmpty() || itemStock == null || itemStock.isEmpty()) {
            Toast.makeText(this, "Stock or price is empty in the database. Please edit it first.", Toast.LENGTH_SHORT).show();
            return;
        }

        int price = Integer.parseInt(itemPrice);
        int stock = Integer.parseInt(itemStock);

        String existingText = textView7.getText().toString();
        String[] orderDetails = existingText.split("\n");
        boolean itemFound = false;

        for (int i = 0; i < orderDetails.length; i++) {
            String detail = orderDetails[i];
            if (detail.contains(selectedItem)) {
                // Update quantity and cost
                String[] parts = detail.split("\\$");
                int oldQuantity = Integer.parseInt(parts[0].replaceAll("\\D+", ""));
                int newQuantity = oldQuantity + quantity;

                if (newQuantity > stock) {
                    Toast.makeText(this, "Item is out of stock. Can't add any more.", Toast.LENGTH_SHORT).show();
                    return;
                }

                orderDetails[i] = selectedItem + " (" + newQuantity + ") $ " + (price);
                itemFound = true;
                break;
            }
        }

        if (!itemFound) {
            if (quantity > stock) {
                Toast.makeText(this, "Item is out of stock. Can't add any more.", Toast.LENGTH_SHORT).show();
                return;
            }

            orderDetails = Arrays.copyOf(orderDetails, orderDetails.length + 1);
            orderDetails[orderDetails.length - 1] = selectedItem + " (" + quantity + ") $ " + (price);
        }

        existingText = String.join("\n", orderDetails);
        textView7.setText(existingText);

        // Update the totalCost
        totalCost += price * quantity;
        TextView totalCostTextView = findViewById(R.id.totalCost);
        totalCostTextView.setText("Total Cost: $" + totalCost);
    }

    private void removeFromOrder() {
        String selectedItem = spinner.getSelectedItem().toString();

        String existingText = textView7.getText().toString();

        String[] orderDetails = existingText.split("\n");

        StringBuilder updatedText = new StringBuilder();
        boolean itemRemoved = false;

        for (int i = orderDetails.length - 1; i >= 0; i--) {
            String detail = orderDetails[i];
            if (!detail.contains(selectedItem)) {
                updatedText.insert(0, detail + "\n");
            } else if (!itemRemoved) {
                itemRemoved = true;
            }
        }

        if (!itemRemoved) {
            Toast.makeText(this, "Item not found in order.", Toast.LENGTH_SHORT).show();
            return;
        }

        textView7.setText(updatedText.toString().trim());
    }

    private void finishOrder() {
        String orderDetails = textView7.getText().toString();

        if (orderDetails.isEmpty()) {
            Toast.makeText(this, "No quantity input. Please input the quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] orderItems = orderDetails.split("\n");

        for (String orderItem : orderItems) {
            String[] itemComponents = orderItem.split("\\(");

            if (itemComponents.length < 2) {
                continue;
            }

            String selectedItem = itemComponents[0].trim();
            int quantity = Integer.parseInt(itemComponents[1].split("\\)")[0].trim());

            // Take the current stock from the database
            String itemStock = inventDB.getItemStock(selectedItem);
            if (itemStock != null) {
                int stock = Integer.parseInt(itemStock);

                // Update the stock for Inventory
                int newStock = stock - quantity;

                // Update the database with the new stock value
                inventDB.updateStock(selectedItem, String.valueOf(newStock));
            }
        }

        textView7.setText("");
        totalCost = 0;
        TextView totalCostTextView = findViewById(R.id.totalCost);
        totalCostTextView.setText("Total Cost: $" + totalCost);

        Toast.makeText(this, "Order finished successfully.", Toast.LENGTH_SHORT).show();
    }


}
