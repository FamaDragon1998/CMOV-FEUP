package org.feup.apm.scanqr;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
  static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
  TextView message;

  private RequestQueue queue;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Button QRButton;
    queue = Volley.newRequestQueue(this);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    message = findViewById(R.id.message);
    QRButton = findViewById(R.id.scan1);
    QRButton.setOnClickListener((v) -> scan(true));
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

  public void scan(boolean qrcode) {
    HashMap info = new HashMap();
    info.put("email", "email");
    info.put("password", "password");
    postData(info);
    try {
      Intent intent = new Intent(ACTION_SCAN);
      intent.putExtra("SCAN_MODE", qrcode ? "QR_CODE_MODE" : "PRODUCT_MODE");
      startActivityForResult(intent, 0);
    } catch (ActivityNotFoundException anfe) {
      showDialog(this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
    }
  }

  private static AlertDialog showDialog(final AppCompatActivity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
    AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
    downloadDialog.setTitle(title);
    downloadDialog.setMessage(message);
    downloadDialog.setPositiveButton(buttonYes, (dialogInterface, i) -> {
      Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
      Intent intent = new Intent(Intent.ACTION_VIEW, uri);
      act.startActivity(intent);
    });
    downloadDialog.setNegativeButton(buttonNo, null);
    return downloadDialog.show();
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    byte[] baMess;
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 0) {
      if (resultCode == RESULT_OK) {
        String contents = data.getStringExtra("SCAN_RESULT");
        String format = data.getStringExtra("SCAN_RESULT_FORMAT");
        try {
          baMess = contents.getBytes(StandardCharsets.ISO_8859_1);
          HashMap info = new HashMap();
          info.put("email", "email");
          info.put("password", "password");

          postData(info);

        } catch (Exception ex) {
          message.setText(ex.getMessage());
          return;
        }
        message.setText("Format: " + format + "\nMessage: " + contents + "\n\nHex: " + byteArrayToHex(baMess));
      }
    }
  }

  public void postData(HashMap data) {
    Log.d("posting", "posting data");
    String url = "http:/192.168.1.5:3000/user/checkout"; //IP Address
    JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
            new Response.Listener<JSONObject>() {
              @Override
              public void onResponse(JSONObject response) {
                Log.d("sucess", response.toString());
              }
            },
            new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());

              }
            }
    ) {
      //here I want to post data to sever
    };
    queue.add(jsonobj);

  }

  String byteArrayToHex(byte[] ba) {
    StringBuilder sb = new StringBuilder(ba.length * 2);
    for (byte b : ba)
      sb.append(String.format("%02x", b));
    return sb.toString();
  }
}
