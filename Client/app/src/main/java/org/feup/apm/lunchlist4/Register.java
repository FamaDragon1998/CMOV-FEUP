package org.feup.apm.lunchlist4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.UUID;


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
        register.setOnClickListener((v)->verifyRegister());

        login = findViewById(R.id.login);
        login.setOnClickListener((v)->backLogin());
    }

    public void verifyRegister()
    {
        username= findViewById(R.id.username);
        name= findViewById(R.id.name);
        password= findViewById(R.id.password);
        card_number= findViewById(R.id.cardnumber);
        card_cvs= findViewById(R.id.cardcvs);

        HashMap info= new HashMap();
        UUID uuid = UUID.randomUUID();

        info.put("id", uuid.toString());
        info.put("username", username.getText().toString() );
        info.put("name", name.getText().toString() );
        info.put("password", password.getText().toString());
        info.put("card_number", card_number.getText().toString() );
        info.put("card_cvs", card_cvs.getText().toString() );

        String url = "http:/"+getString(R.string.ip_address)+":3000/user/register"; //IP Address
        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(info),
                response -> {
                    try {
                        Object obj = response.get("user");
                        if (obj.toString().equals("null")){
                            //TODO: avisar de register error
                            Log.d("register", "WRONG REGISTER");
                        }
                        else {
                            Log.d("user", response.toString());
                            JSONObject jsonObj = response.getJSONObject("user");
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            i.putExtra("user", new User(jsonObj));
                            startActivity(i);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    //TODO: unexpected error
                    Log.d("error", error.toString());

                }
        ) {
        };
        queue.add(jsonobj);
    }

    public void backLogin()
    {
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);

    }


}
