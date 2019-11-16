package org.feup.apm.scanqr;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.UUID;

import javax.crypto.Cipher;


public class MainActivity extends AppCompatActivity {
  static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
  TextView message;
  PublicKey pub;

  private RequestQueue queue;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    queue = Volley.newRequestQueue(this);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    message = findViewById(R.id.message);


    Button addProductButton = findViewById(R.id.scan1);
    addProductButton.setOnClickListener((v) -> scan());
  }

  @Override
  public void onSaveInstanceState(Bundle bundle) {
    super.onSaveInstanceState(bundle);
    bundle.putCharSequence("Message", message.getText());
  }

  public void onRestoreInstanceState(Bundle bundle) {
    super.onRestoreInstanceState(bundle);
    message.setText(bundle.getCharSequence("Message"));
  }

  public void scan() {
    Intent intent = new Intent(ACTION_SCAN);
    intent.putExtra("SCAN_MODE", "QR_CODE_MODE" );
    startActivityForResult(intent, 0);
  }


  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 0) {
      if (resultCode == RESULT_OK) {
        String contents = data.getStringExtra("SCAN_RESULT");
        String format = data.getStringExtra("SCAN_RESULT_FORMAT");

        Log.d("format", format);
        if (contents != null)
          decodeAndShow(contents.getBytes(StandardCharsets.ISO_8859_1));
        //parse contents

        HashMap info = new HashMap();
        info.put("userID", "1"); //userID
        info.put("discount", "true"); //discount use
        info.put("voucher", "232323"); //or null
        info.put(1212, 2); //products
        info.put(23232, 4);

//        checkoutBasket(info);
      }
    }
  }

  public void checkoutBasket(HashMap data) {
    Log.d("posting", "posting data");
    String url = "http:/" + getString(R.string.ip_address) + ":3000/user/checkout";
    JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
            response -> {
              try {
                if (response.getString("response").equals("ACK"))
                  Log.d("success", response.toString());
                else
                  Log.d("not successful", response.toString());

              } catch (JSONException e) {
                e.printStackTrace();
              }
            },
            error -> Log.d("error", error.toString())
    ) {
    };
    queue.add(jsonobj);

  }


  void decodeAndShow(byte[] encTag) {
    byte[] clearTag;

    try {
      Cipher cipher = Cipher.getInstance(Util.ENC_ALGO);
      cipher.init(Cipher.DECRYPT_MODE, pub);
      clearTag = cipher.doFinal(encTag);
    }
    catch (Exception e) {
      Log.e("Decode:", e.getMessage());
      return;
    }
    ByteBuffer tag = ByteBuffer.wrap(clearTag);
    int tId = tag.getInt();
    long most = tag.getLong();
    long less = tag.getLong();
    UUID id = new UUID(most, less);
    int euros = tag.getInt();
    int cents = tag.getInt();
    byte l = tag.get();
    byte[] bName = new byte[l];
    tag.get(bName);
    String name = new String(bName, StandardCharsets.ISO_8859_1);

    String text = "Read Tag (" + clearTag.length + "):\n" + byteArrayToHex(clearTag) + "\n\n" +
            ((tId== Util.tagId)?"correct":"wrong") + "\n" +
            "ID: " + id.toString() + "\n" +
            "Name: " + name + "\n" +
            "Price: €" + euros + "." + cents;
    Log.d("Tag Decode", text);
  }

  String byteArrayToHex(byte[] ba) {                              // converter
    StringBuilder sb = new StringBuilder(ba.length * 2);
    for (byte b : ba)
      sb.append(String.format("%02x", b));
    return sb.toString();
  }

}
