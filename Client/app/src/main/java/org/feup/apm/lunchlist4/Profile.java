package org.feup.apm.lunchlist4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class Profile extends AppCompatActivity {

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        user = (User) getIntent().getSerializableExtra("user");

        TextView name= (TextView) findViewById(R.id.name);
        name.setText(user.getUsername());
        TextView email= (TextView) findViewById(R.id.email);
        email.setText(user.getEmail());
        TextView balance= (TextView) findViewById(R.id.balance);
        balance.setText(user.getTotal_spent().toString());
        TextView discount= (TextView) findViewById(R.id.discount);
        discount.setText(user.getStored_discount().toString());
       // TextView vouchers= (TextView) findViewById(R.id.vouchers);
       // name.setText(user.getTotal_spent().toString());
        Button back;
        back = findViewById(R.id.back);
        back.setOnClickListener((v)->goback());

    }

    public void goback() {
       /* Intent i = new Intent(this, MainActivity.class);
        i.putExtra("user", user);
        startActivity(i);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);*/
       finish();
    }
}
