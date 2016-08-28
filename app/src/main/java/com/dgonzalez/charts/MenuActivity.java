package com.dgonzalez.charts;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dgonzalez.charts.sample.R;
import com.dgonzalez.charts.sample.piecharts.PieChartSampleActivity;
import com.dgonzalez.charts.sample.pointers.PointersSampleActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author david.gonzalez (gonzalez.david31@gmail.com)
 * @since 28/08/2016
 */
public class MenuActivity extends FragmentActivity {

    List<MenuItem> menuItems = buildData();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);
        ListView lv = (ListView) findViewById(R.id.menu_list);

        lv.setAdapter(new MenuAdapter(this, menuItems));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MenuActivity.this, menuItems.get(position).activityToStart);
                startActivity(i);
            }
        });
    }

    private class MenuAdapter extends ArrayAdapter<MenuItem> {

        private Context context;
        /**
         * Constructor
         */
        public MenuAdapter(Context context, List<MenuItem> data) {
            super(context, R.layout.menu_item, R.id.text, data);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            MenuItem item = (MenuItem) getItem(position);
            TextView tv = (TextView)v.findViewById(R.id.text);
            tv.getBackground().setColorFilter(ContextCompat.getColor(context, item.colorId), PorterDuff.Mode.SRC_IN);
            tv.setText(item.textId);
            return v;
        }
    }
    private static List<MenuItem> buildData(){
        ArrayList<MenuItem> data = new ArrayList<>();
        data.add(new MenuItem(R.string.menu_piechart, R.color.menu_pie_chart, PieChartSampleActivity.class));
        data.add(new MenuItem(R.string.menu_pointers, R.color.menu_pointers, PointersSampleActivity.class));
        return data;
    }

    private static class MenuItem{
        int textId;
        int colorId;
        Class activityToStart;

        public MenuItem(int textId, int colorId, Class activityToStart) {
            this.textId = textId;
            this.colorId = colorId;
            this.activityToStart = activityToStart;
        }
    }
}
