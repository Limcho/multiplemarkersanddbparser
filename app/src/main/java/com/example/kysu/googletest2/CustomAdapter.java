package com.example.kysu.googletest2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by K.Y.Su on 2016-10-01.
 */
public class CustomAdapter extends ArrayAdapter {

    int groupid;

    private ArrayList<Product> records;
    private LayoutInflater layoutInflater;
    Context context;

    public CustomAdapter(Context context, int resource, ArrayList<Product> records) {

        super(context, resource, records);

        this.context = context;

        groupid = resource;

        this.records = records;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }

    @Override
    public int getCount() {
        return (records != null) ? records.size() : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Productholder phold = new Productholder();
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item, parent, false);
            phold.htitle = (TextView) convertView.findViewById(R.id.listtitle);
            phold.hwido = (TextView) convertView.findViewById(R.id.listwido);
            phold.hgugdo = (TextView) convertView.findViewById(R.id.listgungdo);
            phold.hmlevel = (TextView) convertView.findViewById(R.id.listmlevel);
            phold.himage = (TextView) convertView.findViewById(R.id.listimage);
            convertView.setTag(phold);
        } else {
            phold = (Productholder) convertView.getTag();
        }
        Product pitem = records.get(position);
        phold.htitle.setText(pitem.getpTitle());
        phold.hwido.setText(pitem.getPwido().toString());
        phold.hgugdo.setText(pitem.getPgugdo().toString());
        phold.hmlevel.setText(pitem.getPmlevel());
        phold.himage.setText(pitem.getpUrl());


        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return (records != null && (0 <= position && position < records.size()) ? records.get(position) : null);
    }

    @Override
    public long getItemId(int position) {
        return (records != null && (0 <= position && position < records.size()) ? position : 0);


    }
}
