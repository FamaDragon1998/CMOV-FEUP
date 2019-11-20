package org.feup.apm.lunchlist4.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.feup.apm.lunchlist4.Entities.User;
import org.feup.apm.lunchlist4.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    Button login,register;
    EditText username,pass;
    private RequestQueue queue;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        queue = Volley.newRequestQueue(this);

        try {
            User user = Util.loadUser(getApplicationContext());
            if (user != null)
                enterApp(user);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        login = findViewById(R.id.login);
        login.setOnClickListener((v)->verifyLogin());

        register = findViewById(R.id.register);
        register.setOnClickListener((v)->redirectRegister());
    }

    public void verifyLogin()
    {
        username= findViewById(R.id.username);
        pass= findViewById(R.id.password);

        HashMap info= new HashMap();
        info.put("username", username.getText().toString() );
        info.put("password", pass.getText().toString());

        String url = "http:/"+getString(R.string.ip_address)+":3000/user/login"; //IP Address
        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(info),
                response -> {
                    try {
                        Object obj = response.get("user");
                        if (obj.toString().equals("null")){
                            setAndShowAlertDialog("Login Error", "Wrong username/password combination");
                            Log.d("login", "WRONG LOGIN");
                        }
                        else {
                            JSONObject jsonObj = response.getJSONObject("user");
                            User user = new User(jsonObj);
                            getTransactions(user);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    setAndShowAlertDialog("Server Error", "Unexpected Server Error");
                    Log.d("login error", error.toString());

                }
        ) {
        };
        queue.add(jsonobj);
    }


    public void getTransactions(User user) throws JSONException {
        Map info= new HashMap();
        info.put("UserId", user.getId());
        List list = new ArrayList();
        list.add(new JSONObject(info));

        String url = "http:/"+getString(R.string.ip_address)+":3000/user/transactionsAll"; //IP Address
        JsonArrayRequest jsonobj = new JsonArrayRequest(Request.Method.POST, url, new JSONArray(list),
                response -> {

                    Log.d("transactions response", response.toString());
                    user.setTransactions(response);
                    try {
                        getVouchers(user);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    setAndShowAlertDialog("Server Error", "Unexpected Server Error");
                    Log.d("transactions error", error.toString());

                }
        ) {
        };
        queue.add(jsonobj);
    }

    public void getVouchers(User user) throws JSONException {
        Map info= new HashMap();
        info.put("UserId", user.getId());
        List list = new ArrayList();
        list.add(new JSONObject(info));

        String url = "http:/"+getString(R.string.ip_address)+":3000/user/vouchers"; //IP Address
        JsonArrayRequest jsonobj = new JsonArrayRequest(Request.Method.POST, url, new JSONArray(list),
                response -> {
                    Log.d("vouchers response", response.toString());
                    user.setVouchers(response);
                    enterApp(user);
                },
                error -> {
                    setAndShowAlertDialog("Server Error", "Unexpected Server Error");
                    Log.d("vouchers error", error.toString());

                }
        ) {
        };
        queue.add(jsonobj);
    }


    public void redirectRegister()
    {
        startActivityForResult(new Intent(this, Register.class),123);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void setAndShowAlertDialog(String title, String message){
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setMessage(message);
        dialog.setTitle(title);
        alertDialog=dialog.create();
        alertDialog.show();
    }

    private void enterApp(User user){
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.putExtra("user", user);
        startActivity(i);
    }


}
