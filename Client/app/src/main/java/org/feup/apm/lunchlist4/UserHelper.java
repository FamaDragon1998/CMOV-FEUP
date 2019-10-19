package org.feup.apm.lunchlist4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserHelper extends SQLiteOpenHelper {
  private static final String DATABASE_NAME = "user.db";
  private static final int SCHEMA_VERSION = 1;

  public UserHelper(Context context) {
    super(context, DATABASE_NAME, null, SCHEMA_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE User(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, shoppingCart INTEGER, balance FLOAT, voucher INTEGER)");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
    // no-op; dealing with version 1 only
  }

  public long insert(String name, Integer cart, Float balance, Integer voucher) {
    ContentValues cv=new ContentValues();
    cv.put("name", name);
    cv.put("cart", cart);
    cv.put("balance", balance);
    cv.put("voucher", voucher);
    return getWritableDatabase().insert("User", "name", cv);
  }

  public void update(String id, String name, Integer cart, Float balance, Integer voucher) {
    ContentValues cv=new ContentValues();
    String[] args={id};
    cv.put("name", name);
    cv.put("cart", cart);
    cv.put("balance", balance);
    cv.put("voucher", voucher);
    getWritableDatabase().update("User", cv, "_id=?", args);
  }

  public Cursor getAll() {
    return(getReadableDatabase().rawQuery("SELECT _id, name, shoppingCart, balance, voucher FROM User ORDER BY name", null));
  }

  public Cursor getById(String id) {
    String[] args={id};
    return(getReadableDatabase().rawQuery("SELECT _id, name, shoppingCart, balance, voucher FROM User WHERE _id=?", args));
  }

  public String getName(Cursor c) {
    return(c.getString(1));
  }

  public String getShoppingCart(Cursor c) {
    return(c.getString(2));
  }

  public String getBalance(Cursor c) {
    return(c.getString(3));
  }

  public String getVoucher(Cursor c) {
    return(c.getString(4));
  }
}
