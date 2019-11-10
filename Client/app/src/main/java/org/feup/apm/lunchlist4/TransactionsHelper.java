package org.feup.apm.lunchlist4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TransactionsHelper extends SQLiteOpenHelper {
  private static final String DATABASE_NAME = "transactions.db";
  private static final int SCHEMA_VERSION = 1;

  public TransactionsHelper(Context context) {
    super(context, DATABASE_NAME, null, SCHEMA_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE Transactions(_id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, price FLOAT)");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
    // no-op; dealing with version 1 only
  }

  public long insert(String date, float price) {
    ContentValues cv=new ContentValues();
    cv.put("date", date);
    cv.put("price", price);
    return getWritableDatabase().insert("Transactions", "date", cv);
  }


  public void update(String id, String date, Float price) {
    ContentValues cv=new ContentValues();
    String[] args={id};
    cv.put("date", date);
    cv.put("price", price);
    getWritableDatabase().update("Transactions", cv, "_id=?", args);
  }

  public Cursor getAll() {
    return(getReadableDatabase().rawQuery("SELECT _id, date, price  FROM Transactions ORDER BY date", null));
  }

  public Cursor getById(String id) {
    String[] args={id};
    return(getReadableDatabase().rawQuery("SELECT _id, date, price FROM Transactions WHERE _id=?", args));
  }

  public String getDate(Cursor c) {
    return(c.getString(1));
  }

  public Float getPrice(Cursor c) {
    return(Float.parseFloat(c.getString(2)));
  }



}
