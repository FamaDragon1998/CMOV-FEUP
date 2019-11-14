package org.feup.apm.lunchlist4;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailsTransaction extends AppCompatActivity implements AdapterView.OnItemClickListener {
    TransactionsHelper helper;
    static long currentId = -1;
    Cursor model;
    ProductAdapter adapter;
    private RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);


        helper = new TransactionsHelper(this);

        model = helper.getAll();
        startManagingCursor(model);
        adapter=new DetailsTransaction.ProductAdapter(model);
        ListView listp = findViewById(R.id.products);
        listp.setAdapter(adapter);
        listp.setEmptyView(findViewById(R.id.empty_list));
        listp.setOnItemClickListener(this);

        queue = Volley.newRequestQueue(this);

        String id = getIntent().getExtras().getString("id");
        Map info= new HashMap();
        info.put("TransactionId", id);
        List list = new ArrayList();
        list.add(new JSONObject(info));

        String url = "http:/"+getString(R.string.ip_address)+":3000/product/transaction"; //IP Address
        JsonArrayRequest jsonobj = new JsonArrayRequest(Request.Method.POST, url, new JSONArray(list),
                response -> {

                    Log.d("details response", response.toString());
                },
                error -> {
                    //TODO: unexpected error
                    Log.d("transactions error", error.toString());

                }
        ) {
        };
        queue.add(jsonobj);
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
        User user =(User) getIntent().getExtras().getSerializable("user");
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("user", user);
        startActivity(i);
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);

    }
}
