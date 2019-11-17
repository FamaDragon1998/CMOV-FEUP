package org.feup.apm.scanqr;

import android.content.Intent;
import android.os.Build;
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
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.UUID;

import javax.crypto.Cipher;


public class MainActivity extends AppCompatActivity {
  static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
  TextView message;
  PublicKey pub;
  boolean hasKey = false;

  private RequestQueue queue;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    queue = Volley.newRequestQueue(this);
    super.onCreate(savedInstanceState);
    Log.d("cert","nibba");
    setContentView(R.layout.activity_main);
    message = findViewById(R.id.message);

    Button addProductButton = findViewById(R.id.scan1);
    addProductButton.setOnClickListener((v) -> scan());
    //readKey();
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

  void readKey() {
    try {
      KeyStore keyStore = KeyStore.getInstance(Util.ANDROID_KEYSTORE);
      Log.d("keystore", keyStore.toString());
      keyStore.load(null);
      Log.d("keystore load", keyStore.toString());

      Certificate cert = keyStore.getCertificate(Util.keyAlias);
      if (cert != null) {
        Log.d("pubkey",cert.getPublicKey().toString());
        pub = cert.getPublicKey();
        Log.d("pubkey",pub.toString());
      }
      else
        Log.d("cert","null");
    }
    catch(Exception e) {
      Log.d("error",e.getMessage());
    }
  }


  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 0) {
      if (resultCode == RESULT_OK) {
        String contents = data.getStringExtra("SCAN_RESULT");

        //if (contents != null)
          //decodeAndShow(contents.getBytes(StandardCharsets.ISO_8859_1));
        //parse contents
       HashMap info = parseContentToServer(contents);

        checkoutBasket(info);
      }
    }
  }

  private HashMap parseContentToServer(String content) {
    HashMap info = new HashMap();

    String[] splits = content.split(",");
    String products = splits[0]; String voucher = splits[1]; String discount = splits[2]; String user = splits[3];



    splits = products.split("|");
    int count = products.length() - products.replace("|", "").length();
    Log.d("count", String.valueOf(count));
    for (int i = 0; i < (count+1); i++){
      String[] product = splits[i].split(";");
      String id = product[0];
      Log.d("id", id);
      String price = product[1];
      Log.d("price", price);

    }


    if (splits.length != 0)
      for (int i = 0; i < splits.length; i++){
        String[] product = splits[i].split(";");
        //info.
       // Log.d("product", product[0]);
      }
    else{

    }

    info.put("UserId", user); //userID
    info.put("discount", discount); //discount use
    if (voucher == null || voucher.equals(""))
      voucher = "0";
    info.put("voucher", voucher); //or null

    Log.d("terminal", info.toString());
    //info.put(1212, 2); //products
    //info.put(23232, 4);

    return info;
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
    Log.d("Decoding", "Decoding");
    try {
      Cipher cipher;
      //cipher = Cipher.getInstance(Util.ENC_ALGO);
      try {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { // below android m
          cipher= Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL"); // error in android 6: InvalidKeyException: Need RSA private or public key
        }
        else { // android m and above
          cipher= Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidKeyStoreBCWorkaround"); // error in android 5: NoSuchProviderException: Provider not available: AndroidKeyStoreBCWorkaround
        }
      } catch(Exception exception) {
        throw new RuntimeException("getCipher: Failed to get an instance of Cipher", exception);
      }
      Log.d("cypher",cipher.toString());
      Log.d("key",pub.toString());
      cipher.init(Cipher.DECRYPT_MODE, pub);
      Log.d("a","3");
      clearTag = cipher.doFinal(encTag);
      Log.d("a","4");
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
