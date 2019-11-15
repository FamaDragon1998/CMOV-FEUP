package org.feup.apm.lunchlist4;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;

public class QrCodeActivity extends AppCompatActivity {
    ImageView qrCodeIv;
    final static int DIMENSION=500;
    final static String CH_SET="ISO-8859-1";

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_qr_code);
        user = (User) getIntent().getSerializableExtra("user");

        Button back = findViewById(R.id.back);
        back.setOnClickListener((v)->finish());

        qrCodeIv = findViewById(R.id.qr);

        byte[] byteArray = getIntent().getByteArrayExtra("content");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

     //   final String QRcodeContents = BitMapToString(bmp);


        // convert in a separate thread to avoid possible ANR
        Thread t = new Thread(() -> {
            final Bitmap bitmap = encodeAsBitmap(QRcodeContents);
            runOnUiThread(()->qrCodeIv.setImageBitmap(bmp));
        });
        t.start();


    }


    Bitmap encodeAsBitmap(String str) {
        BitMatrix result;

        Hashtable<EncodeHintType, String> hints = new Hashtable<>();
        hints.put(EncodeHintType.CHARACTER_SET, CH_SET);
        try {
            result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, DIMENSION, DIMENSION, hints);
        }
        catch (Exception exc) {
            // runOnUiThread(()->errorTv.setText(exc.getMessage()));
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int colorPrimary = Color.parseColor("#FF123454");
        int colorWhite = Color.parseColor("#FFFFFFFF");
        // int colorPrimary = getResources().getColor(R.color.colorPrimary, null);
        // int colorWhite = getResources().getColor(R.color.colorPrimaryDark, null);
        int[] pixels = new int[w * h];
        for (int line = 0; line < h; line++) {
            int offset = line * w;
            for (int col = 0; col < w; col++) {
                pixels[offset + col] = result.get(col, line) ? colorPrimary : colorWhite;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
}
