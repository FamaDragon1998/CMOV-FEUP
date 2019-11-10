package org.feup.apm.lunchlist4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProductsHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "products.db";
    private static final int SCHEMA_VERSION = 1;

    public ProductsHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Products(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, price FLOAT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        // no-op; dealing with version 1 only
    }

    public long insert(String name, float price) {
        ContentValues cv=new ContentValues();
        cv.put("name", name);
        cv.put("price", price);
        return getWritableDatabase().insert("Products", "name", cv);
    }


    public void update(String id, String name, float price) {
        ContentValues cv=new ContentValues();
        String[] args={id};
        cv.put("name", name);
        cv.put("price", price);
        getWritableDatabase().update("Products", cv, "_id=?", args);
    }

    public Cursor getAll() {
        return(getReadableDatabase().rawQuery("SELECT _id, name, price  FROM Products ORDER BY name", null));
    }

    public Cursor getById(String id) {
        String[] args={id};
        return(getReadableDatabase().rawQuery("SELECT _id, name, price FROM Products WHERE _id=?", args));
    }

    public String getName(Cursor c) {
        return(c.getString(1));
    }

    public Float getPrice(Cursor c) {
        return(Float.parseFloat(c.getString(2)));
    }



}
