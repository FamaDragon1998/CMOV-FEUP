package org.feup.apm.lunchlist4;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.List;


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
    Log.d("usermain",user.getName());
    ActionBar bar = getSupportActionBar();
    if (bar != null) {
      bar.setIcon(R.drawable.medium_logo2);
      bar.setDisplayShowHomeEnabled(true);
    }

    ids = new String[user.getTransactions().size()+1];
    for(int i = 0; i < user.getTransactions().size() ;i++){
      ids[i] = user.getTransactions().get(i).getId();

    }

    adapter = new TransactionAdapter(this, R.layout.rowdate, user.getTransactions());

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
      Intent i = new Intent(getApplicationContext(), DetailsTransaction.class);
      //Bundle b = new Bundle();
      //b.putString("id",id);
      //b.putSerializable("user",user);
      i.putExtra("TransactionId", id);
      startActivity(i);

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
        TextView hora = line.findViewById(R.id.date);
        TextView price = line.findViewById(R.id.totaltransaction);
        TextView id = line.findViewById(R.id.id);
        String[] data = parseDate(p.getDate());

        if (date != null) {
          date.setText(data[0]);
        }

        if (hora != null) {
          hora.setText(data[1]);
        }

        if (price != null) {
          price.setText(p.getTotal_value() + "€");
        }

        if (id != null){
          Log.d("id", p.getId());
          id.setText(p.getId());
        }
      }

      return line;
    }

    public String[] parseDate(String date)
    {
      String[] dateaux;
      dateaux=date.split("Z");
      dateaux=dateaux[0].split("T");

      String[] data,hora;
      String dataf,horaf;
      data=dateaux[0].split("-");
      Log.d("data",data[0]);
      String mes;
      switch (data[1]){
        case "1": mes="January";
          break;
        case "2":mes="February";
          break;
        case "3":mes="March";
          break;
        case "4":mes="April";
          break;
        case "5":mes="May";
          break;
        case "6":mes="June";
          break;
        case "7":mes="July";
          break;
        case "8":mes="August";
          break;
        case "9":mes="September";
          break;
        case "10":mes="October";
          break;
        case "11":mes="November";
          break;
        case "12": mes="December";
          break;
        default: mes="Unknown";
      }
      dataf=data[2]+" " +mes+ " " + data[0];

      Log.d("dateaux",dateaux[1]);
      horaf=dateaux[1].substring(0,8);

      String[] returndate = new String[]{dataf, horaf};
      return returndate;
    }

  }


}
