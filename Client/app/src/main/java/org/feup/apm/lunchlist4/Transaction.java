package org.feup.apm.lunchlist4;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Transaction implements Serializable {

    private int id;
    private float total_value;
    private String date;
    private List<Product> products;

    public Transaction(int id, float total_value, String date) {
        this.id = id;
        this.total_value = total_value;
        this.products = new ArrayList();
        this.date = date;
    }

    public Transaction()
    {
        this.products=new ArrayList();
    }


    public void addProducts(String product) {
        this.products.add(this.products.size(),new Product(product));
    }

    public void addProducts(Product product) {
        if (this.products.size()<10)
            Log.d("size",this.products.size()+"");
        this.products.add(this.products.size(),product);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public float getTotal_value() {

        return total_value;
    }

    public void setTotal_value(float total_value) {
        this.total_value = total_value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Product> getProducts() {

        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }


}
