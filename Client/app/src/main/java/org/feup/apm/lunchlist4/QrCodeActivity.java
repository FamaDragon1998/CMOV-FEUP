package org.feup.apm.lunchlist4;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Hashtable;

public class QrCodeActivity extends AppCompatActivity {
    ImageView qrCodeIv;
    final static int DIMENSION=500;
    final static String CH_SET="ISO-8859-1";
    private final String ISO_SET = "ISO-8859-1";

    String qr_content = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_qr_code);

        Button back = findViewById(R.id.back);
        back.setOnClickListener((v)->finish());

        qrCodeIv = findViewById(R.id.qr);

        byte[] content = getIntent().getByteArrayExtra("content");

        int sign_size = Util.KEY_SIZE/8;
        int mess_size = content.length - sign_size;

        try {
            qr_content = new String(content, ISO_SET);

           /* ByteBuffer bb = ByteBuffer.wrap(content);
            byte[] mess = new byte[mess_size];
            byte[] sign = new byte[sign_size];
            bb.get(mess, 0, mess_size);
            bb.get(sign, 0, sign_size);
            boolean verified = validate(mess, sign);
            Log.d("verified", verified + "");
            String text = "Message: \"" + byteArrayToHex(mess) + "\"\nVerified: " + verified + "\nTotal bytes: " + content.length;
            Log.d("qr text", text);*/
        } catch (UnsupportedEncodingException e) {
            Log.d("gg", e.getMessage());
        }

        // do the creation in a new thread to avoid ANR Exception
        Thread t = new Thread(() -> {
            final Bitmap bitmap;
            bitmap = encodeAsBitmap(qr_content);
            runOnUiThread(() -> qrCodeIv.setImageBitmap(bitmap));
        });
        t.start();
    }


    String byteArrayToHex(byte[] ba) {
        StringBuilder sb = new StringBuilder(ba.length * 2);
        for (byte b : ba)
            sb.append(String.format("%02x", b));
        return sb.toString();
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

    boolean validate(byte[] message, byte[] signature) {
        boolean verified = false;
        try {
            KeyStore ks = KeyStore.getInstance(Util.ANDROID_KEYSTORE);
            ks.load(null);
            KeyStore.Entry entry = ks.getEntry(Util.keyname, null);
            PublicKey pub = ((KeyStore.PrivateKeyEntry)entry).getCertificate().getPublicKey();
            Signature sg = Signature.getInstance(Util.SIGN_ALGO);
            sg.initVerify(pub);
            sg.update(message);
            verified = sg.verify(signature);
        }
        catch (Exception ex) {
            Log.d("coisu", ex.getMessage());
        }
        return verified;
    }
}
