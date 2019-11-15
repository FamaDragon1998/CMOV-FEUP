package org.feup.apm.lunchlist4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class NewTransaction extends AppCompatActivity {
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    final static int DIMENSION = 500;
    final static String CH_SET = "ISO-8859-1";
    //String qrResult;

    private Transaction basket;
    private Float total;

    Button finishbutton;

    private String ids[];
    User user;

    Util.ProductAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (User) getIntent().getSerializableExtra("user");
        basket = new Transaction();
        total = 0f;

        ListView productsListView = findViewById(R.id.productsNew);
        adapter = new Util.ProductAdapter(this, R.layout.row, basket.getProducts());
        productsListView.setAdapter(adapter);



        setContentView(R.layout.activity_new_transaction);

        Button addProductButton;
        addProductButton = findViewById(R.id.scan);
        addProductButton.setOnClickListener((v) -> scan(true, basket));

        finishbutton = findViewById(R.id.generateQRcode);
        finishbutton.setOnClickListener((v) -> generateQRcode(""));

        TextView total = findViewById(R.id.total);
        total.setText(basket.getTotal_value() + "");

        voucherAdapter();

    }



    public void generateQRcode(String sz) {
        if (basket.getProducts().isEmpty()) {
            //TODO: DIzer que basket tá vazio
            return;
        }

        int size;
        String content = "";

        if (sz.isEmpty())
            size = 256;
        else
            size = Integer.valueOf(sz);
        if (size < 1)
            size = 1;
        else if (size > 1536)
            size = 1536;
        byte[] bContent = new byte[size];
        for (int b = 0; b < size; b++) {
            bContent[b] = (byte) (b % 256);
        }
        //   try {
        //content = new String(bContent, CH_SET);
        CheckBox boxdiscount = (CheckBox) findViewById(R.id.discount);
        content = parseTransaction(basket.getProducts(), 10f, "12");
        String print = Util.byteArrayToHex(bContent);
        if (size > 400)
            print = "(too big)";


        final String QRcodeContents = content;
        // convert in a separate thread to avoid possible ANR
        Thread t = new Thread(() -> {
            final Bitmap bitmap = encodeAsBitmap(QRcodeContents);
            // runOnUiThread(()->qrCodeIv.setImageBitmap(bitmap));
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);


            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();


            Intent intent = new Intent(this, QrCodeActivity.class);

            intent.putExtra("image", byteArray); //Put your id to your next Intent
            startActivity(intent);
            finish();
        });
        t.start();
    }

    Bitmap encodeAsBitmap(String str) {
        BitMatrix result;

        Hashtable<EncodeHintType, String> hints = new Hashtable<>();
        hints.put(EncodeHintType.CHARACTER_SET, CH_SET);
        try {
            result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, DIMENSION, DIMENSION, hints);
        } catch (Exception exc) {
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


    public String parseTransaction(List<Product> products, Float discount, String voucher) {
        String contents = "";
        for (int i = 0; i < products.size(); i++) {
            contents += products.get(i).getId() + ";" + products.get(i).getPrice().toString();
            if (i < products.size() - 1)
                contents += "|";
        }
        contents += "," + voucher + "," + discount;
        return contents;
    }

    public void backButton(View view) {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("user", user);
        startActivity(i);
        overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);

    }


    public void scan(boolean qrcode, Transaction basket) {
        if (this.basket.getProducts().size() >=10) {
            //TODO: Dizer que so dá 10 produtos
            return;
        }
        try {
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            //TODO: Pedir para instalar Barcode Scanner
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        byte[] baMess;
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                String format = data.getStringExtra("SCAN_RESULT_FORMAT");
                try {
                    Product productScanned = new Product(contents);
                    // baMess = contents.getBytes(StandardCharsets.ISO_8859_1);
                    basket.addProducts(productScanned);
                    adapter.add(productScanned);
                    adapter.notifyDataSetChanged();

                    Log.d("basket", productScanned.getName());


                } catch (Exception ex) {
                    Log.d("error", "exception", ex);
                }
            }
        }
    }

    private void voucherAdapter(){

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setIcon(R.drawable.logo_icon);
        builderSingle.setTitle("Select One Voucher:-");
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);

        for (int i = 0; i < user.getVouchers().size(); i++)
            arrayAdapter.add(user.getVouchers().get(i));

        builderSingle.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
            String strName = arrayAdapter.getItem(which);
            AlertDialog.Builder builderInner = new AlertDialog.Builder(getApplicationContext());
            builderInner.setMessage(strName);
            builderInner.setTitle("Your Voucher is");
            builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog,int which) {
                    dialog.dismiss();
                }
            });
            Button selectedVoucherButton = findViewById(R.id.selectVoucherButton);
            selectedVoucherButton.setOnClickListener((v) -> builderInner.show());
        });
    }

}
