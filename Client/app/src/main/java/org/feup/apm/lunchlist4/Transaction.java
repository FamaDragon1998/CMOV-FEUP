package org.feup.apm.lunchlist4;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Transaction {


    private List<Product> products;
   // private String date;

    public Transaction(List<Product> products)
    {
        this.products=products;
    }

    public Transaction()
    {
        this.products=new ArrayList();
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void addProducts(String product) {
        this.products.add(this.products.size(),new Product(product));
    }

    public void addProducts(Product product) {
        if (this.products.size()<10)
            Log.d("size",this.products.size()+"");
        this.products.add(this.products.size(),product);
    }
/*
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }*/
}
