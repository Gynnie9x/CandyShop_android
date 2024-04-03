package com.example.CandyShop_android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InventDB extends SQLiteOpenHelper {

    final static String DB_NAME = "InventoryDB";
    final static String ID = "_id";
    final static String ITEM_NAME = "ItemName";
    final static String ITEM_DESCRIPTION = "ItemDescription";
    final static String ITEM_PRICE = "ItemPrice";
    final static String ITEM_STOCK = "ItemStock";

    final private static String CREATE_CMD =
            "CREATE TABLE " + DB_NAME + " (" +
                            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            ITEM_NAME + " TEXT NOT NULL, " +
                            ITEM_DESCRIPTION + " TEXT, " +
                            ITEM_PRICE + " TEXT, " +
                            ITEM_STOCK + " TEXT)";

    final static String[] ALL_COLUMNS = {ID, ITEM_NAME, ITEM_DESCRIPTION, ITEM_PRICE, ITEM_STOCK};

    final private static Integer VERSION = 1;

    final private Context context;

    public InventDB (Context context) {
        super(context, DB_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CMD);

        addInitialItem(db, "Milky", "Melt into Happiness!", "10", "20");
        addInitialItem(db, "Minty", "Cool Bliss in Every Bite!", "5", "15");
        addInitialItem(db, "Choco Dark", "Boldly Bittersweet, Always Brilliant!", "8", "25");
    }

    private void addInitialItem(SQLiteDatabase db, String itemName, String itemDescription, String itemPrice, String itemStock) {
        ContentValues cv = new ContentValues();
        cv.put(ITEM_NAME, itemName);
        cv.put(ITEM_DESCRIPTION, itemDescription);
        cv.put(ITEM_PRICE, itemPrice);
        cv.put(ITEM_STOCK, itemStock);
        db.insert(DB_NAME, null, cv);
    }

    public Cursor readAllItems() {
        Cursor c;
        SQLiteDatabase db = this.getReadableDatabase();
        c = db.query(DB_NAME, ALL_COLUMNS, null, null, null, null, null);
        return c;
    }

    public boolean insertItem(String itemName, String itemDescription, String itemPrice, String itemStock) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (isItemAlreadyExists(db, itemName)) {
            return false;
        } else {

            ContentValues cv = new ContentValues();
            cv.put(ITEM_NAME, itemName);
            cv.put(ITEM_DESCRIPTION, itemDescription);
            cv.put(ITEM_PRICE, itemPrice);
            cv.put(ITEM_STOCK, itemStock);
            db.insert(DB_NAME, null, cv);
            return true;
        }
    }

    public void deleteItem(String itemName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DB_NAME, ITEM_NAME + " = ?", new String[]{itemName});
        db.close();
    }

    public void updateItemByName(String itemName, String itemDescription, String itemPrice, String itemStock) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ITEM_DESCRIPTION, itemDescription);
        cv.put(ITEM_PRICE, itemPrice);
        cv.put(ITEM_STOCK, itemStock);
        db.update(DB_NAME, cv, ITEM_NAME + " = ?", new String[]{itemName});
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
        onCreate(db);
    }

    public String getItemPrice(String itemName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {ITEM_PRICE};
        String selection = ITEM_NAME + "=?";
        String[] selectionArgs = {itemName};

        Cursor cursor = db.query(DB_NAME, columns, selection, selectionArgs, null, null, null);

        String itemPrice = null;

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(ITEM_PRICE);

            if (columnIndex != -1) {
                itemPrice = cursor.getString(columnIndex);
            }

            cursor.close();
        }

        return itemPrice;
    }

    public String getItemStock(String itemName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {ITEM_STOCK};
        String selection = ITEM_NAME + "=?";
        String[] selectionArgs = {itemName};

        Cursor cursor = db.query(DB_NAME, columns, selection, selectionArgs, null, null, null);

        String itemStock = null;

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(ITEM_STOCK);

            if (columnIndex != -1) {
                itemStock = cursor.getString(columnIndex);
            }

            cursor.close();
        }

        return itemStock;
    }

    public void updateStock(String itemName, String newStock) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ITEM_STOCK, newStock);

        db.update(DB_NAME, values, ITEM_NAME + "=?", new String[]{itemName});
        db.close();
    }

    private boolean isItemAlreadyExists(SQLiteDatabase db, String itemName) {
        String selection = ITEM_NAME + "=?";
        String[] selectionArgs = {itemName};

        Cursor cursor = db.query(DB_NAME, new String[]{ITEM_NAME}, selection, selectionArgs, null, null, null);

        boolean itemExists = cursor != null && cursor.getCount() > 0;

        if (cursor != null) {
            cursor.close();
        }

        return itemExists;
    }

    void deleteDatabase() {
        context.deleteDatabase(DB_NAME);
    }
}
