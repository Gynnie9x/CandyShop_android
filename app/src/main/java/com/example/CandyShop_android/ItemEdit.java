package com.example.CandyShop_android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project2_gdoan.R;

public class ItemEdit extends AppCompatActivity {

    private EditText editTextDescription;
    private EditText editTextPrice;
    private EditText editTextStock;
    private int position;
    private InventDB dbHelper;
    private String itemName;
    private String itemDescription;
    private String itemPrice;
    private String itemStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_edit);

        dbHelper = new InventDB(this);

        editTextDescription = findViewById(R.id.descriptBlank);
        editTextPrice = findViewById(R.id.costBlank);
        editTextStock = findViewById(R.id.itemQuantity);

        itemName = getIntent().getStringExtra("itemName");
        itemDescription = getIntent().getStringExtra("itemDescription");
        itemPrice = getIntent().getStringExtra("itemPrice");
        itemStock = getIntent().getStringExtra("itemStock");

        editTextDescription.setHint(itemDescription);
        editTextPrice.setHint(itemPrice);
        editTextStock.setHint(itemStock);


        TextView itemNameTextView = findViewById(R.id.itemName);
        itemNameTextView.setText(itemName);

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editedDescription = editTextDescription.getText().toString();
                String editedPrice = editTextPrice.getText().toString();
                String editedStock = editTextStock.getText().toString();

                // Use previous values when any of the fields are empty
                if (editedDescription.isEmpty()) {
                    editedDescription = itemDescription;
                }

                if (editedPrice.isEmpty()) {
                    editedPrice = itemPrice;
                }

                if (editedStock.isEmpty()) {
                    editedStock = itemStock;
                }

                dbHelper.updateItemByName(itemName, editedDescription, editedPrice, editedStock);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("editedDescription", editedDescription);
                resultIntent.putExtra("editedPrice", editedPrice);
                resultIntent.putExtra("editedStock", editedStock);
                resultIntent.putExtra("position", getIntent().getIntExtra("position", -1)); // Send back the position

                setResult(RESULT_OK, resultIntent);

                finish();
            }
        });


        Button delButton = findViewById(R.id.delButton);
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteItem();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        // AlertDialog Generate
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteItem() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("deleteItem", true);
        resultIntent.putExtra("position", getIntent().getIntExtra("position", -1)); // Send back the position
        setResult(RESULT_OK, resultIntent);
        finish();
    }


}
