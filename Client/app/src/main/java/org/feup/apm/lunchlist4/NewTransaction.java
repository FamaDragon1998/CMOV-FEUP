package org.feup.apm.lunchlist4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class NewTransaction extends AppCompatActivity {
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    TransactionsHelper helper;
    static long currentId = -1;
    Cursor model;
    NewTransaction.ProductAdapter adapter;
    //String qrResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_transaction);

        helper = new TransactionsHelper(this);

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

            ImageView symbol = row.findViewById(R.id.symbol);
            symbol.setImageResource(R.drawable.batmanoutfit);

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

    public void addProduct(View view)
    {
        //startActivity(new Intent(this, ScanActivity.class));
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }

    public void scan(boolean qrcode) {
        try {
            Intent intent = new Intent(ACTION_SCAN);
            //intent.putExtra("SCAN_MODE", qrcode ? "QR_CODE_MODE" : "PRODUCT_MODE");
            startActivityForResult(intent, 0);
        }
        catch (ActivityNotFoundException anfe) {
          //  showDialog(this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }
}
