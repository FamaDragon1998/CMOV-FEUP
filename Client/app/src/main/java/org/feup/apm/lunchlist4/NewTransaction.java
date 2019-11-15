package org.feup.apm.lunchlist4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
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
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import java.util.List;

public class NewTransaction extends AppCompatActivity {
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    ProductsHelper helper;
    static long currentId = -1;
    //Cursor model;
    //NewTransaction.ProductAdapter adapter;
    final static int DIMENSION = 500;
    final static String CH_SET = "ISO-8859-1";
    //String qrResult;

    private Transaction basket;
    private float total;

    CheckBox vouchercheck, discountcheck;
    Button finishbutton;


    private String ids[];
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (User) getIntent().getSerializableExtra("user");

        setContentView(R.layout.activity_new_transaction);
        basket = new Transaction();
        total = 0;
        ListView listview = findViewById(R.id.products);


        user = (User) getIntent().getSerializableExtra("user");

        Button QRButton;
        QRButton = findViewById(R.id.scan);
        QRButton.setOnClickListener((v) -> scan(true, basket));


        vouchercheck = (CheckBox) findViewById(R.id.voucher);
        discountcheck = (CheckBox) findViewById(R.id.discount);

        finishbutton = (Button) findViewById(R.id.generateQRcode);
        finishbutton.setOnClickListener((v) -> generateQRcode(""));

        discountcheck.setOnClickListener(((v) -> togglediscount()));
        vouchercheck.setOnClickListener(((v) -> togglevoucher()));

        TextView total = findViewById(R.id.total);
        // total.setText(p);

    }


    String byteArrayToHex(byte[] ba) {
        StringBuilder sb = new StringBuilder(ba.length * 2);
        for (byte b : ba)
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
        for (int b = 0; b < size; b++) {
            bContent[b] = (byte) (b % 256);
        }
        //   try {
        //content = new String(bContent, CH_SET);
        CheckBox boxvoucher = (CheckBox) findViewById(R.id.voucher);
        CheckBox boxdiscount = (CheckBox) findViewById(R.id.discount);
        content = parsetransaction(basket.getProducts(), boxvoucher.isChecked(), 1);
        String print = byteArrayToHex(bContent);
        if (size > 400)
            print = "(too big)";

        //  messageTv.setText(getApplicationContext().getString(R.string.tv_message_template, size, print));
        //  }
        //  catch (UnsupportedEncodingException e) {
        // errorTv.setText(e.getMessage());
        //  }

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


    public String parsetransaction(List<Product> products, Boolean discount, Integer voucher) {
        String contents = "";
        for (int i = 0; i < products.size(); i++) {
            contents += products.get(i).getId() + ";" + products.get(i).getPrice().toString();
            if (i < products.size() - 1)
                contents += "|";
        }
        contents += "," + voucher + "," + discount;
        return contents;
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public void togglevoucher() {
        //reduce voucher number
    }

    public void togglediscount() {
        SeekBar sb = findViewById(R.id.seekBar);
        sb.setClickable(true);
        //reduce voucher
    }

    public void backButton(View view) {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("user", user);
        startActivity(i);
        overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);

    }


    public void scan(boolean qrcode, Transaction basket) {
        try {

            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
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
                    basket.addProducts(productscanned);


                    Log.d("basket", basket.getProducts().get(0).getName());
                    // helper.insert(productscanned.getName(),productscanned.getPrice());

                    // model = helper.getAll();
                    // startManagingCursor(model);
                    // adapter=new NewTransaction.ProductAdapter(model);

                    ListView list = findViewById(R.id.products);
                    // list.setAdapter(adapter);


                } catch (Exception ex) {
                    Log.d("error", "exception", ex);
                }
            }
        }
    }

    class ProductAdapter extends ArrayAdapter<Product> {
        private int layoutResource;
        private Context mContext;

        ProductAdapter(@NonNull Context context, int resource, @NonNull List<Product> objects) {
            super(context, resource, objects);
            layoutResource = resource;
            mContext = context;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View line = convertView;

            if (line == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(mContext);
                line = vi.inflate(layoutResource, null);
            }


            Product p = getItem(position);

            if (p != null) {
                TextView date = line.findViewById(R.id.title);
                TextView price = line.findViewById(R.id.sumproducts);
                TextView id = line.findViewById(R.id.id);

                if (date != null) {
                    date.setText(p.getName());
                }

                if (price != null) {
                    price.setText(p.getPrice() + "€");
                    total += p.getPrice();
                }

            }

            return line;
        }
    }
}
