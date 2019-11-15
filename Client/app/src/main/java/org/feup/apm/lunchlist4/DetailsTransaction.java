package org.feup.apm.lunchlist4;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

    ProductAdapter adapter;
    private RequestQueue queue;
    Float transactionTotal = 0f;
    ArrayList<Product> products;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);

        String id = getIntent().getStringExtra("TransactionId");

        ListView listp = findViewById(R.id.products);
        products = new ArrayList();
        queue = Volley.newRequestQueue(this);

        Map info= new HashMap();
        info.put("TransactionId", id);
        List list = new ArrayList();
        list.add(new JSONObject(info));

        String url = "http:/"+getString(R.string.ip_address)+":3000/product/transaction"; //IP Address
        JsonArrayRequest jsonobj = new JsonArrayRequest(Request.Method.POST, url, new JSONArray(list),
                response -> {
                    Log.d("details response", response.toString());
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonobject = response.getJSONObject(i);
                            String name = jsonobject.getString("name");
                            Float price = Float.parseFloat(jsonobject.getString("value"));

                            products.add(new Product(name, price));
                            transactionTotal += price;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    TextView total= findViewById(R.id.sumproducts);
                    total.setText(transactionTotal.toString()+" €");
                    adapter = new ProductAdapter(this, R.layout.row, products);
                    listp.setAdapter(adapter);
                },
                error -> {
                    //TODO: unexpected error
                    Log.d("transactions error", error.toString());

                }
        ) {
        };
        queue.add(jsonobj);

        Button back = findViewById(R.id.back);
        back.setOnClickListener((v)->finish());
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
                TextView title = line.findViewById(R.id.title);
                TextView price = line.findViewById(R.id.total);

                if (title != null) {
                    title.setText(p.getName());
                }

                if (price != null) {
                    price.setText(p.getPrice()+ "€");
                }
            }

            return line;
        }
    }


}
