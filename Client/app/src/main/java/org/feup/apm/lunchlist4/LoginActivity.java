package org.feup.apm.lunchlist4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    Button login,register;
    EditText username,pass;
    private RequestQueue queue;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = getApplicationContext();

        queue = Volley.newRequestQueue(this);

        login = findViewById(R.id.login);
        login.setOnClickListener((v)->verifylogin());

        register = findViewById(R.id.register);
        register.setOnClickListener((v)->redirectregister());
    }

    public void verifylogin()
    {
        username= findViewById(R.id.username);
        pass= findViewById(R.id.password);

        HashMap info= new HashMap();
        info.put("username", username.getText().toString() );
        info.put("password", pass.getText().toString());

        String url = "http:/"+getString(R.string.ip_address)+":3000/user/login"; //IP Address
        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(info),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Object obj = response.get("user");
                            if (obj.toString().equals("null")){
                                //TODO: avisar de login error
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
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //TODO: unexpected error
                        Log.d("error", error.toString());

                    }
                }
        ) {
        };
        queue.add(jsonobj);
    }


    public void getTransactions(User user)
    {
        HashMap info= new HashMap();
        info.put("UserId", user.getId());

        String url = "http:/"+getString(R.string.ip_address)+":3000/user/transactionsAll"; //IP Address
        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(info),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("response", response.toString());
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        i.putExtra("user", user);
                        startActivity(i);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TODO: unexpected error
                        Log.d("error", error.toString());

                    }
                }
        ) {
        };
        queue.add(jsonobj);
    }

    public void redirectregister()
    {
        startActivityForResult(new Intent(this, Register.class),123);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


}
