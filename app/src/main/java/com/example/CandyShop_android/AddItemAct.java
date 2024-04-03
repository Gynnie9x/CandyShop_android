package com.example.CandyShop_android;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project2_gdoan.R;

public class AddItemAct extends AppCompatActivity {

    private EditText itemNameEditText, itemDescriptionEditText, itemPriceEditText, itemStockEditText;
    private int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item);

        itemNameEditText = findViewById(R.id.editTextText);
        itemDescriptionEditText = findViewById(R.id.editTextText2);
        itemPriceEditText = findViewById(R.id.editTextText3);
        itemStockEditText = findViewById(R.id.editTextText4);

        Button addButton = findViewById(R.id.button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemName = itemNameEditText.getText().toString();
                String itemDescription = itemDescriptionEditText.getText().toString();
                String itemPrice = itemPriceEditText.getText().toString();
                String itemStock = itemStockEditText.getText().toString();

                Intent resultIntent = new Intent();

                if (isItemAlreadyExists(itemName)) {
                    setResult(RESULT_CANCELED, resultIntent);
                    Toast.makeText(AddItemAct.this, "This product name already exists.", Toast.LENGTH_SHORT).show();
                } else {
                    resultIntent.putExtra("itemName", itemName);
                    resultIntent.putExtra("itemDescription", itemDescription);
                    resultIntent.putExtra("itemPrice", itemPrice);
                    resultIntent.putExtra("itemStock", itemStock);

                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra("position")) {
            position = intent.getIntExtra("position", -1);
            String itemName = intent.getStringExtra("itemName");
            String itemDescription = intent.getStringExtra("itemDescription");
            String itemPrice = intent.getStringExtra("itemPrice");
            String itemStock = intent.getStringExtra("itemStock");

            itemNameEditText.setText(itemName);
            itemDescriptionEditText.setText(itemDescription);
            itemPriceEditText.setText(itemPrice);
            itemStockEditText.setText(itemStock);
        }
    }

    public void onBackPressed() {

        Intent resultIntent = new Intent();

        if (position != -1) {
            // Include the position for an edit,
            resultIntent.putExtra("position", position);
        }

        setResult(RESULT_CANCELED, resultIntent);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isFinishing()) {
            String itemName = itemNameEditText.getText().toString();
            String itemDescription = itemDescriptionEditText.getText().toString();
            String itemPrice = itemPriceEditText.getText().toString();
            String itemStock = itemStockEditText.getText().toString();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("itemName", itemName);
            resultIntent.putExtra("itemDescription", itemDescription);
            resultIntent.putExtra("itemPrice", itemPrice);
            resultIntent.putExtra("itemStock", itemStock);

            setResult(RESULT_OK, resultIntent);
        }
    }

    private boolean isItemAlreadyExists(String itemName) {
        InventDB db = new InventDB(this);
        SQLiteDatabase readDb = db.getReadableDatabase();

        String selection = InventDB.ITEM_NAME + "=?";
        String[] selectionArgs = {itemName};

        Cursor cursor = readDb.query(InventDB.DB_NAME, null, selection, selectionArgs, null, null, null);

        boolean itemExists = cursor != null && cursor.getCount() > 0;

        if (cursor != null) {
            cursor.close();
        }

        readDb.close();
        db.close();

        return itemExists;
    }
}
