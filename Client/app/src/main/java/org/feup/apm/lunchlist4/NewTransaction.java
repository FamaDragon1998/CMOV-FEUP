package org.feup.apm.lunchlist4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class NewTransaction extends AppCompatActivity {
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    ProductsHelper helper;
    static long currentId = -1;
    Cursor model;
    NewTransaction.ProductAdapter adapter;
    final static int DIMENSION=500;
    final static String CH_SET="ISO-8859-1";
    //String qrResult;

    private List<Product> basket;

    TextView vouocher;
    CheckBox vouchercheck,discountcheck;
    Button finishbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_transaction);

        helper = new ProductsHelper(this);

        model = helper.getAll();
        startManagingCursor(model);
        adapter=new NewTransaction.ProductAdapter(model);
        ListView list = findViewById(R.id.products);
        list.setAdapter(adapter);
        list.setEmptyView(findViewById(R.id.empty_list));
        //list.setOnItemClickListener(this);
        Button QRButton;
        QRButton = findViewById(R.id.scan);
        QRButton.setOnClickListener((v)->scan(true));


        vouchercheck = (CheckBox)findViewById(R.id.voucher);
        discountcheck = (CheckBox)findViewById(R.id.discount);

        finishbutton =  (Button) findViewById(R.id.generateQRcode);
        finishbutton.setOnClickListener((v)->generateQRcode(""));
        basket = new ArrayList<Product>();
    }


    class ProductAdapter extends CursorAdapter {
        ProductAdapter(Cursor c) {
            super(NewTransaction.this, c);
        }

        @Override
        public View newView(Context context, Cursor c, ViewGroup parent) {
            View row=getLayoutInflater().inflate(R.layout.row_1line, parent, false);
            ((TextView)row.findViewById(R.id.title)).setText("insert title here");
            //  ((TextView)row.findViewById(R.id.description)).setText("X"+"items);
            ((TextView)row.findViewById(R.id.price)).setText("5"+"€");


            return(row);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
        }
    }

    public void backButton(View view)
    {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);

    }


    public void scan(boolean qrcode) {
        try {
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE" );
            startActivityForResult(intent, 0);
        }
        catch (ActivityNotFoundException anfe) {
          //  showDialog(this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
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
                    Product productscanned = new Product(contents);
                   // baMess = contents.getBytes(StandardCharsets.ISO_8859_1);
                    basket.add(productscanned);

                    helper.insert(productscanned.getName(),productscanned.getPrice());

                    model = helper.getAll();
                    startManagingCursor(model);
                    adapter=new NewTransaction.ProductAdapter(model);

                    ListView list = findViewById(R.id.products);
                    list.setAdapter(adapter);


                }
                catch (Exception ex) {
                    Log.d("content","exception",ex);
                }
            }
        }
    }

    String byteArrayToHex(byte[] ba) {
        StringBuilder sb = new StringBuilder(ba.length * 2);
        for(byte b: ba)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

     public void generateQRcode(String sz) {
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
        for (int b=0; b<size; b++) {
            bContent[b] = (byte)(b%256);
        }
        try {
            content = new String(bContent, CH_SET);
            String print = byteArrayToHex(bContent);
            if (size > 400)
                print = "(too big)";

          //  messageTv.setText(getApplicationContext().getString(R.string.tv_message_template, size, print));
        }
        catch (UnsupportedEncodingException e) {
           // errorTv.setText(e.getMessage());
        }

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


            Intent intent = new Intent(NewTransaction.this, QrCodeActivity.class);

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

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

}
