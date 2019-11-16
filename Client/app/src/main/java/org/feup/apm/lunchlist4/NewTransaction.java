package org.feup.apm.lunchlist4;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class NewTransaction extends AppCompatActivity {
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    final static int DIMENSION = 500;
    final static String CH_SET = "ISO-8859-1";
    //String qrResult;

    private Transaction basket;

    private TextView totalView;

    Button finishButton;

    private String ids[];
    User user;
    TextView currentDiscount;


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

        finishButton = findViewById(R.id.generateQRcode);
        finishButton.setOnClickListener((v) -> generateQRcode(""));

        totalView = findViewById(R.id.total);
        totalView.setText(basket.getTotal_value() + " €");
        currentDiscount=findViewById(R.id.selectedDiscountText);

        voucherAdapter();
        discountAdapter();

    }


    public void generateQRcode(String sz) {
        if (basket.getProducts().isEmpty()) {
            //TODO: DIzer que basket tá vazio
            return;
        }

        Float discountUsed;
        if (currentDiscount.getText().equals("No Discount Selected"))
            discountUsed = 0f;
        else
            discountUsed = Float.parseFloat(String.valueOf(currentDiscount.getText()));

        String voucher;
        if (basket.getVoucher().equals("No Voucher Selected"))
            voucher ="";
        else
            voucher = basket.getVoucher();

        byte[] message = parseTransaction(discountUsed, voucher);

        try {
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
        }

        Intent intent = new Intent(this, QrCodeActivity.class);
        intent.putExtra("content", message); //Put your id to your next Intent
        startActivity(intent);
        finish();
    }

    public byte[] parseTransaction(Float discount, String voucher) {
        ByteBuffer bb = ByteBuffer.allocate((basket.getProducts().size()+1)+Util.KEY_SIZE/8);
        bb.put((byte)basket.getProducts().size());
        for (int k=0; k<basket.getProducts().size(); k++) {
            bb.put((basket.getProducts().get(k).getId() + ";" + basket.getProducts().get(k).getPrice().toString()).getBytes());
            if (k<basket.getProducts().size()-1)
                bb.put("|".getBytes());
        }
        String aux = "," + voucher + "," + discount;
        bb.put(aux.getBytes());

        return bb.array();
    }

    public void backButton(View view) {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("user", user);
        startActivity(i);
        overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);

    }


    public void scan() {
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
                //TODO: avisar que foi removido
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
                // TODO Auto-generated method stub
                if (progress == 0)
                    currentDiscount.setText("No Discount Selected");
                else
                    currentDiscount.setText(progress + "");//Here is the textview with the progress number.

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
        });
    }
}
