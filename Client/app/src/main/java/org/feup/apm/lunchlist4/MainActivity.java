package org.feup.apm.lunchlist4;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.security.KeyPairGeneratorSpec;
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

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.security.auth.x500.X500Principal;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
  TransactionAdapter adapter;
  private RequestQueue queue;
  User user;
  private String ids[];

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    user = (User) getIntent().getSerializableExtra("user");

    try {
      Util.saveUser(user, getApplicationContext());
    } catch (IOException e) {
      e.printStackTrace();
    }

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
    generateAndStoreKeys();

  }



  class PubKey {
    byte[] modulus;
    byte[] exponent;
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
    else if (item.getItemId() == R.id.logout) {
      Intent i = new Intent(this, LoginActivity.class);
      startActivity(i);
      overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
      return (true);
    }

    return(super.onOptionsItemSelected(item));
  }


  private void openDetails(String id) {
      Intent i = new Intent(getApplicationContext(), DetailsTransaction.class);
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
        String[] data = Util.parseDate(p.getDate());

        if (date != null) {
          date.setText(data[0]);
        }

        if (hora != null) {
          hora.setText(data[1]);
        }

        if (price != null) {
          price.setText(p.getTotal_value() + "€");
        }

        if (id != null) {
          Log.d("id", p.getId());
          id.setText(p.getId());
        }
      }

      return line;
    }

  }

  private void generateAndStoreKeys(){
    try {
      KeyStore ks = KeyStore.getInstance(Util.ANDROID_KEYSTORE);
      ks.load(null);
      KeyStore.Entry entry = ks.getEntry(Util.keyname, null);
      if (entry == null) {
        Calendar start = new GregorianCalendar();
        Calendar end = new GregorianCalendar();
        end.add(Calendar.YEAR, 20);
        KeyPairGenerator kgen = KeyPairGenerator.getInstance(Util.KEY_ALGO, Util.ANDROID_KEYSTORE);
        AlgorithmParameterSpec spec = new KeyPairGeneratorSpec.Builder(this)
                .setKeySize(Util.KEY_SIZE)
                .setAlias(Util.keyname)
                .setSubject(new X500Principal("CN=" + Util.keyname))   // Usually the full name of the owner (person or organization)
                .setSerialNumber(BigInteger.valueOf(12121212))
                .setStartDate(start.getTime())
                .setEndDate(end.getTime())
                .build();
        kgen.initialize(spec);
        kgen.generateKeyPair();
      }
    }
    catch (Exception ex) {
      Log.d("coise", ex.getMessage());
    }
  }

  PubKey getPubKey() {
    PubKey pkey = new PubKey();
    try {
      KeyStore ks = KeyStore.getInstance(Util.ANDROID_KEYSTORE);
      ks.load(null);
      KeyStore.Entry entry = ks.getEntry(Util.keyname, null);
      PublicKey pub = ((KeyStore.PrivateKeyEntry)entry).getCertificate().getPublicKey();
      pkey.modulus = ((RSAPublicKey)pub).getModulus().toByteArray();
      pkey.exponent = ((RSAPublicKey)pub).getPublicExponent().toByteArray();
    }
    catch (Exception ex) {
      Log.d("coiso", ex.getMessage());
    }
    return pkey;
  }

  byte[] getPrivExp() {
    byte[] exp = null;

    try {
      KeyStore ks = KeyStore.getInstance(Util.ANDROID_KEYSTORE);
      ks.load(null);
      KeyStore.Entry entry = ks.getEntry(Util.keyname, null);
      PrivateKey priv = ((KeyStore.PrivateKeyEntry)entry).getPrivateKey();
      exp = ((RSAPrivateKey)priv).getPrivateExponent().toByteArray();
    }
    catch (Exception ex) {
      Log.d("coisa", ex.getMessage());
    }
    if (exp == null)
      exp = new byte[0];
    return exp;
  }

}
