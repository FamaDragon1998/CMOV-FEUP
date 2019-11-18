package org.feup.apm.lunchlist4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.nio.ByteBuffer;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Hashtable;
import java.util.List;

public class NewTransaction extends AppCompatActivity {
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    final static int DIMENSION = 500;
    final static String CH_SET = "ISO-8859-1";
    //String qrResult;

    private Transaction basket;

    private TextView totalView,totalwithdiscountview;

    Button finishButton;

    User user;
    TextView currentDiscount;

    AlertDialog alertDialog;

    Util.ProductAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_transaction);

        user = (User) getIntent().getSerializableExtra("user");
        basket = new Transaction();

        ListView productsListView = findViewById(R.id.productsNew);
        adapter = new Util.ProductAdapter(this, R.layout.row, basket.getProducts());
        productsListView.setAdapter(adapter);

        Button addProductButton = findViewById(R.id.scan);
        addProductButton.setOnClickListener((v) -> scan());

        Button removeButton = findViewById(R.id.removeproduct);
        removeButton.setOnClickListener((v) -> removeproduct());

        finishButton = findViewById(R.id.generateQRcode);
        finishButton.setOnClickListener((v) -> generateQRcode(""));

        totalView = findViewById(R.id.total);
        totalView.setText(basket.getTotal_value() + " €");

        totalView = findViewById(R.id.total);
        totalView.setText(basket.getTotal_value() + " €");
        currentDiscount=findViewById(R.id.selectedDiscountText);

        totalwithdiscountview = findViewById(R.id.totalwithdiscount);

        totalwithdiscountview.setText(basket.getTotal_value() + " €");

        voucherAdapter();
        discountAdapter();
        removeproductAdapter();

    }

    private void removeproductAdapter(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Which product to Remove");
        CharSequence[] products = basket.getProducts().toArray(new CharSequence[basket.getProducts().size()]);
        builder.setItems(products, (dialog, which) -> {
            TextView productView = findViewById(R.id.selectedVoucherText);
            String voucher = products[which].toString();
            if (basket.getVoucher().equals(voucher)){
                basket.setVoucher("No Voucher Selected");
            }
            else
                basket.setVoucher(voucher);
            productView.setText(basket.getVoucher());

        });
        Button voucherButton = findViewById(R.id.selectVoucherButton);
        voucherButton.setOnClickListener((v) ->  builder.show());

    }


    public void generateQRcode(String sz) {
        if (basket.getProducts().isEmpty()) {
            setAndShowAlertDialog("Basket size", "There are no products in the basket!");
            return;
        }

        Float discountUsed;
        if (currentDiscount.getText().equals("No Discount Selected"))
            discountUsed = 0f;
        else
            discountUsed = Float.parseFloat(String.valueOf(currentDiscount.getText()));
        String voucher;
        if (basket.getVoucher().equals("No Voucher Selected") || basket.getVoucher() ==null)
            voucher ="0";
        else
            voucher = basket.getVoucher();


        byte[] message = parseTransaction(discountUsed, voucher);

       /* try {
            KeyStore ks = KeyStore.getInstance(Util.ANDROID_KEYSTORE);
            ks.load(null);
            KeyStore.Entry entry = ks.getEntry(Util.keyname, null);
            PrivateKey pri = ((KeyStore.PrivateKeyEntry)entry).getPrivateKey();
            Signature sg = Signature.getInstance(Util.SIGN_ALGO);
            sg.initSign(pri);
            sg.update(message, 0, basket.getProducts().size()+1);
            int sl = sg.sign(message, basket.getProducts().size()+1, Util.KEY_SIZE/8);
            Log.d("somethin", "Sign size = " + sl + " bytes.");
        }
        catch (Exception ex) {
            Log.d("somethin", ex.getMessage());
        }*/

        Intent intent = new Intent(this, QrCodeActivity.class);
        intent.putExtra("content", message); //Put your id to your next Intent
        startActivity(intent);
        finish();
    }

    /* public byte[] parseTransaction(Float discount, String voucher) {
        ByteBuffer bb = ByteBuffer.allocate((basket.getProducts().size()+1)+Util.KEY_SIZE/8);
        bb.put((byte)basket.getProducts().size());
        for (int k=0; k<basket.getProducts().size(); k++) {
            bb.put((basket.getProducts().get(k).getId() + ";" + basket.getProducts().get(k).getPrice().toString()).getBytes());
            if (k<basket.getProducts().size()-1)
                bb.put("|".getBytes());
        }
        String aux = "," + voucher + "," + discount + "," + user.getId();
        bb.put(aux.getBytes());

        return bb.array();
    }*/
    public byte[] parseTransaction(Float discount, String voucher) {
        String contents = "";
        for (int i = 0; i < basket.getProducts().size(); i++) {
            contents += basket.getProducts().get(i).getId() + ";" + basket.getProducts().get(i).getPrice().toString();
            if (i < basket.getProducts().size() - 1)
                contents += "|";
        }
        if (voucher.equals(""))
            voucher = "0";
        contents += "," + voucher + "," + discount+","+user.getId();
        return contents.getBytes();
    }

    public void backButton(View view) {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("user", user);
        startActivity(i);
        overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);

    }


    public void scan() {
        if (this.basket.getProducts().size() >=10) {
            setAndShowAlertDialog("Basket size", "There can only be a maximum of 10 products");
            return;
        }
        try {
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            setAndShowAlertDialog("Scan error", "Please install Barcode Scanner to continue");
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
                    adapter.updateContent(basket.getProducts());
                    totalView.setText(basket.getTotal_value() + " €");


                } catch (Exception ex) {
                    Log.d("error", "exception", ex);
                }
            }
        }
    }

    private void voucherAdapter(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a Voucher");
        CharSequence[] vouchers = user.getVouchers().toArray(new CharSequence[user.getVouchers().size()]);

        builder.setItems(vouchers, (dialog, which) -> {
            TextView voucherView = findViewById(R.id.selectedVoucherText);
            String voucher = vouchers[which].toString();
            if (basket.getVoucher().equals(voucher)){
                basket.setVoucher("No Voucher Selected");
            }
            else
                basket.setVoucher(voucher);
            voucherView.setText(basket.getVoucher());

        });
        Button voucherButton = findViewById(R.id.selectVoucherButton);
        voucherButton.setOnClickListener((v) ->  builder.show());

    }

    private void discountAdapter()
    {
        SeekBar seek = findViewById(R.id.discountSeekBar);
        seek.setMax((int)Math.floor((double) user.getStored_discount()));
        seek.setProgress(0);

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { //listener for your seekbar
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0)
                    currentDiscount.setText("No Discount Selected");
                else
                    currentDiscount.setText(progress + "");//Here is the textview with the progress number.
                totalwithdiscountview.setText(basket.getTotal_value()- progress+" €");
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void setAndShowAlertDialog(String title, String message){
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setMessage(message);
        dialog.setTitle(title);
        alertDialog=dialog.create();
        alertDialog.show();
    }




}
