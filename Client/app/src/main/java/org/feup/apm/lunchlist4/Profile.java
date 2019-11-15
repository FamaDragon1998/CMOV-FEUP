package org.feup.apm.lunchlist4;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class Profile extends AppCompatActivity {

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        user = (User) getIntent().getSerializableExtra("user");

        TextView username=  findViewById(R.id.username);
        username.setText(user.getUsername());

        TextView name= findViewById(R.id.name);
        name.setText(user.getName());

        TextView balance= findViewById(R.id.balance);
        balance.setText(user.getTotal_spent().toString()+" €");

        TextView discount= findViewById(R.id.discount);
        discount.setText(user.getStored_discount().toString()+" €");

        TextView vouchers=  findViewById(R.id.vouchers);
        vouchers.setText(user.getVouchers().size() + "");

        Button back = findViewById(R.id.back);
        back.setOnClickListener((v)->finish());

    }

}
