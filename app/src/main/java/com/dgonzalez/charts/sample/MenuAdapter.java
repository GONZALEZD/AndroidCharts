package com.dgonzalez.charts.sample;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * @author david.gonzalez (gonzalez.david31@gmail.com)
 * @since 28/08/2016
 */
public class MenuAdapter extends SimpleAdapter {

    private Context context;
    /**
     * Constructor
     */
    public MenuAdapter(Context context, List<Map<String, Integer>> data, int resource, String[] from, int[] to) {
        super(context, data, R.layout.menu_item, null, null);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        switch (position){
            case 0:
                // pie charts
                v.setBackgroundColor(ContextCompat.getColor(context, R.color.menu_pie_chart));
                ((TextView)v.findViewById(R.id.text)).setText(R.string.menu_piechart);
                break;
            case 1:
                // pointers
                v.setBackgroundColor(ContextCompat.getColor(context, R.color.menu_pointers));
                ((TextView)v.findViewById(R.id.text)).setText(R.string.menu_pointers);
                break;
        }
        return v;
    }
}
