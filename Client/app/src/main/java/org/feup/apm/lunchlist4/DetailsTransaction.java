package org.feup.apm.lunchlist4;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetailsTransaction extends AppCompatActivity implements AdapterView.OnItemClickListener {
    TransactionsHelper helper;
    static long currentId = -1;
    Cursor model;
    ProductAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);


        helper = new TransactionsHelper(this);

        model = helper.getAll();
        startManagingCursor(model);
        adapter=new DetailsTransaction.ProductAdapter(model);
        ListView list = findViewById(R.id.products);
        list.setAdapter(adapter);
        list.setEmptyView(findViewById(R.id.empty_list));
        list.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    class ProductAdapter extends CursorAdapter {
        ProductAdapter(Cursor c) {
            super(DetailsTransaction.this, c);
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
}
