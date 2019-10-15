package org.feup.acme;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomProductView extends ArrayAdapter<String> {

    private String[] name;
    private String[] description;
    private Integer[] imgid;
    private Activity context;
    public CustomProductView(Activity context, String[] name,String[] description,Integer[] imgid) {
        super(context, R.layout.content_scrolling,name);
        this.context=context;
        this.name=name;
        this.description=description;
        this.imgid=imgid;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View r=convertView;
        ViewHolder viewHolder=null;
        if (r==null)
        {
            LayoutInflater layoutInflater=context.getLayoutInflater();
            r=layoutInflater.inflate(R.layout.content_scrolling,null,true);
            viewHolder=new ViewHolder(r);
            r.setTag(viewHolder);
        }
        else
        {
            viewHolder=(ViewHolder) r.getTag();
        }
        viewHolder.ivw.setImageResource(imgid[position]);
        viewHolder.tvw1.setText(name[position]);
        viewHolder.tvw2.setText(description[position]);

        return r;
    }

    class ViewHolder
    {
        TextView tvw1;
        TextView tvw2;
        ImageView ivw;
        ViewHolder(View v)
        {
            tvw1=(TextView) v.findViewById(R.id.tvname);
            tvw2=(TextView) v.findViewById(R.id.tvdescription);
            ivw=(ImageView) v.findViewById(R.id.imageview);
        }
    }
}
