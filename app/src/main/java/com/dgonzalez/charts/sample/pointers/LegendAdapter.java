package com.dgonzalez.charts.sample.pointers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.dgonzalez.charts.pointers.PointerUtilities;
import com.dgonzalez.charts.sample.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dav on 28/03/2016.
 */
public class LegendAdapter extends ArrayAdapter<String> {

    List<Bitmap> icons;
    public LegendAdapter(Context context) {
        super(context, R.layout.pointers_legend_row, R.id.text);
        icons = new ArrayList<>();
    }

    public void add(String text, Character symbol, Paint paint){
        add(text);
        icons.add(getCount()-1, PointerUtilities.createEndPointer(
                paint,
                PointerUtilities.DEFAULT_POINTER_END_SIZE,
                symbol));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        ImageView img = (ImageView) v.findViewById(R.id.image);
        img.setImageBitmap(icons.get(position));
        return v;
    }
}
