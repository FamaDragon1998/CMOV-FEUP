package org.feup.apm.lunchlist4;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
  public final static String ID_EXTRA="org.feup.apm.lunchlist4.POS";
  private static final String FILE_NAME = "transactions.txt";

  static long currentId = -1;

  TransactionAdapter adapter;
  private RequestQueue queue;
  User user;


  private String ids[];
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    user = (User) getIntent().getSerializableExtra("user");
    ActionBar bar = getSupportActionBar();
    if (bar != null) {
      bar.setIcon(R.drawable.medium_logo2);
      bar.setDisplayShowHomeEnabled(true);
    }

    ids = new String[user.getTransactions().size()+1];
    for(int i = 0; i < user.getTransactions().size() ;i++){
      ids[i] = user.getTransactions().get(i).getId();

    }

    adapter = new TransactionAdapter(this, R.layout.row, user.getTransactions());

    ListView transactionList = findViewById(R.id.listview);
    transactionList.setAdapter(adapter);

    transactionList.setOnItemClickListener((parent, view, position, id) -> {
      openDetails(ids[position]);
    });

    queue = Volley.newRequestQueue(this);

  }


  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    new MenuInflater(this).inflate(R.menu.main, menu);
    return (super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    if (item.getItemId() == R.id.add) {
      Intent i = new Intent(this, NewTransaction.class);
      i.putExtra("user", user);
      startActivity(i);
      overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
      return(true);
    }
    else if (item.getItemId() == R.id.profile) {
      Intent i = new Intent(this, Profile.class);
      i.putExtra("user", user);
      startActivity(i);
      overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
      return (true);
    }
    return(super.onOptionsItemSelected(item));
  }


  private void openDetails(String id) {
    Map info= new HashMap();
    info.put("TransactionId", id);
    List list = new ArrayList();
    list.add(new JSONObject(info));

    String url = "http:/"+getString(R.string.ip_address)+":3000/user/transactionsAll"; //IP Address
    JsonArrayRequest jsonobj = new JsonArrayRequest(Request.Method.POST, url, new JSONArray(list),
            response -> {

              Log.d("details response", response.toString());

             // Intent i = new Intent(getApplicationContext(), MainActivity.class);
             // i.putExtra("user", user);
              //startActivity(i);
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
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

  }

  class TransactionAdapter extends ArrayAdapter<Transaction> {
    private int layoutResource;
    private Context mContext;

    TransactionAdapter(@NonNull Context context, int resource, @NonNull List<Transaction> objects) {
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


      Transaction p = getItem(position);

      if (p != null) {
        TextView date = line.findViewById(R.id.title);
        TextView price = line.findViewById(R.id.price);
        TextView id = line.findViewById(R.id.id);

        if (date != null) {
          date.setText(p.getDate());
        }

        if (price != null) {
          price.setText(p.getTotal_value() + "");
        }

        if (id != null){
          Log.d("id", p.getId());
          id.setText(p.getId());
        }
      }

      return line;
    }



  }


}
