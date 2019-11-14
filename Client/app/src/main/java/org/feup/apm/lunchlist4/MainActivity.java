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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

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
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
  public final static String ID_EXTRA="org.feup.apm.lunchlist4.POS";
  private static final String FILE_NAME = "transactions.txt";

  static long currentId = -1;

  TransactionAdapter adapter;
  private RequestQueue queue;
  User user;
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

    adapter = new TransactionAdapter(this, R.layout.row, user.getTransactions());

    ListView transactionList = findViewById(R.id.listview);
    transactionList.setAdapter(adapter);

    //transactionList.setEmptyView(findViewById(R.id.empty_list));
    //transactionList.setOnItemClickListener(this);

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

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
    Intent i=new Intent(this, DetailsTransaction.class);
    currentId = id;
    i.putExtra(ID_EXTRA, String.valueOf(id));
    startActivity(i);
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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

      View v = convertView;

      if (v == null) {
        LayoutInflater vi;
        vi = LayoutInflater.from(mContext);
        v = vi.inflate(layoutResource, null);
      }

      Transaction p = getItem(position);

      if (p != null) {
        TextView date = v.findViewById(R.id.title);
        TextView price = v.findViewById(R.id.price);

        if (date != null) {
          date.setText(p.getDate());
        }

        if (price != null) {
          price.setText(p.getTotal_value() + "");
        }
      }

      return v;
    }

  }


}
