package org.feup.apm.lunchlist4;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
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
    User user;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);


       // helper = new TransactionsHelper(this);

       // model = helper.getAll();
        startManagingCursor(model);
        user = (User) getIntent().getSerializableExtra("user");
        id = (String) getIntent().getStringExtra("id");

        ListView listp = findViewById(R.id.products);
        Log.d("id",user.getTransaction(Integer.parseInt(id)).toString());
        Log.d("id",user.getTransaction(Integer.parseInt(id)).getProducts().toString());
        adapter = new ProductAdapter(this, R.layout.row, user.getTransaction(Integer.parseInt(id)).getProducts());
        listp.setAdapter(adapter);
        listp.setEmptyView(findViewById(R.id.empty_list));
        listp.setOnItemClickListener(this);

        Button back = findViewById(R.id.back);
        back.setOnClickListener((v)->backButton());

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
                TextView price = line.findViewById(R.id.price);
                TextView id = line.findViewById(R.id.id);


                if (date != null) {
                    date.setText(p.getName());
                }


                if (price != null) {
                    price.setText(p.getPrice() + "€");
                }

                if (id != null){
                    id.setText(p.getId());
                }
            }

            return line;
        }
    }



    public void backButton() {

                  //  Intent i = new Intent(getApplicationContext(), MainActivity.class);
                  //  i.putExtra("user", user);
                 //   startActivity(i);
                    finish();
                  //  Log.d("transactions response", user.getName());

    }
}
