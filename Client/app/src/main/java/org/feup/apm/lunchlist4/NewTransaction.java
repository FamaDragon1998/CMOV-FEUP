package org.feup.apm.lunchlist4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class NewTransaction extends AppCompatActivity {
    TransactionsHelper helper;
    static long currentId = -1;
    Cursor model;
    NewTransaction.ProductAdapter adapter;
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
}
