package org.feup.apm.lunchlist4;

import android.util.Log;
import android.view.SurfaceControl;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class User implements Serializable {

    private int card_number;
    private int card_cvs;

    private Float total_spent;

    private Float stored_discount;

    private List<Transaction> transactions;

    private List<String> vouchers;
    private String username;
    private String name;

    public User(String username, String name, int card_number, int card_cvs, Float total_spent, Float stored_discount, List<Transaction> transactions, List<String> vouchers) {
        this.username = username;
        this.name = name;
        this.card_number = card_number;
        this.card_cvs = card_cvs;
        this.total_spent = total_spent;
        this.stored_discount = stored_discount;
        this.transactions = transactions;
        this.vouchers = vouchers;
    }

    public User(JSONObject response) {
        //parse here
       // Log.d("user", response.getString("username"));
        List<String> fields = Arrays.asList(response.toString().split(","));

        try {
            this.username = response.getString("username");
            this.name= response.getString("name");
            this.card_number=response.getInt("card_number");
            this.card_cvs=response.getInt("card_cvs");
            String s = response.getString("total_spent");
            this.total_spent= Float.parseFloat(s);
            s = response.getString("stored_discount");
            this.stored_discount= Float.parseFloat(s);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public int getCard_number() {
        return card_number;
    }

    public int getCard_cvs() {
        return card_cvs;
    }

    public Float getTotal_spent() {
        return total_spent;
    }

    public Float getStored_discount() {
        return stored_discount;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public List<String> getVouchers() {
        return vouchers;
    }




}
