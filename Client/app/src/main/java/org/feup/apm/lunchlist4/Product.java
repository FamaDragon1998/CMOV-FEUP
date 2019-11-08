package org.feup.apm.lunchlist4;

public class Product {

    private int id;
    private String name;
    private float price;

    //id;name;price
    public Product(String content){
        String[] ss = content.split(";");
        id = Integer.parseInt(ss[0]);
        name = ss[1];
        price = Float.parseFloat(ss[2]);
    }


}
