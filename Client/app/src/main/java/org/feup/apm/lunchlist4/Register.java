package org.feup.apm.lunchlist4;

import androidx.appcompat.app.AppCompatActivity;

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


public class Register extends AppCompatActivity {
    EditText username,name,password,card_number,card_cvs;
    Button register,login;
    private RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        queue = Volley.newRequestQueue(this);

        register = findViewById(R.id.register);
        register.setOnClickListener((v)->verifyregister());

        login = findViewById(R.id.login);
        login.setOnClickListener((v)->backlogin());
    }

    public void backlogin()
    {
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);

    }

    public void verifyregister()
    {
        username= findViewById(R.id.username);
        name= findViewById(R.id.name);
        password= findViewById(R.id.password);
        card_number= findViewById(R.id.cardnumber);
        card_cvs= findViewById(R.id.cardcvs);

        HashMap info= new HashMap();
        info.put("username", username.getText().toString() );
        info.put("name", name.getText().toString() );
        info.put("password", password.getText().toString());
        info.put("card_number", card_number.getText().toString() );
        info.put("card_cvs", card_cvs.getText().toString() );

        String url = "http:/"+getString(R.string.ip_address)+":3000/user/register"; //IP Address
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
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                i.putExtra("user", new User(jsonObj));
                                   startActivity(i);
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

    public void redirectregister()
    {
        startActivityForResult(new Intent(this, LoginActivity.class),123);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}
