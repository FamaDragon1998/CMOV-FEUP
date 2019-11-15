package org.feup.apm.lunchlist4;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class Util {

    class ProductAdapter extends ArrayAdapter<Product> {
        private int layoutResource;
        private Context mContext;

        ProductAdapter(@NonNull Context context, int resource, @NonNull List<Product> objects) {
            super(context, resource, objects);
            layoutResource = resource;
            mContext = context;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View line = convertView;

            if (line == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(mContext);
                line = vi.inflate(layoutResource, null);
            }

            Product p = getItem(position);

            if (p != null) {
                TextView title = line.findViewById(R.id.title);
                TextView price = line.findViewById(R.id.total);

                if (title != null) {
                    title.setText(p.getName());
                }

                if (price != null) {
                    price.setText(p.getPrice()+ "€");
                }
            }

            return line;
        }
    }

    public static String[] parseDate(String date)
    {
        String[] dateaux;
        dateaux=date.split("Z");
        dateaux=dateaux[0].split("T");

        String[] data,hora;
        String dataf,horaf;
        data=dateaux[0].split("-");
        Log.d("data",data[0]);
        String mes;
        switch (data[1]){
            case "1": mes="January";
                break;
            case "2":mes="February";
                break;
            case "3":mes="March";
                break;
            case "4":mes="April";
                break;
            case "5":mes="May";
                break;
            case "6":mes="June";
                break;
            case "7":mes="July";
                break;
            case "8":mes="August";
                break;
            case "9":mes="September";
                break;
            case "10":mes="October";
                break;
            case "11":mes="November";
                break;
            case "12": mes="December";
                break;
            default: mes="Unknown";
        }
        dataf=data[2]+" " +mes+ " " + data[0];

        Log.d("dateaux",dateaux[1]);
        horaf=dateaux[1].substring(0,8);

        String[] returndate = new String[]{dataf, horaf};
        return returndate;
    }

}
