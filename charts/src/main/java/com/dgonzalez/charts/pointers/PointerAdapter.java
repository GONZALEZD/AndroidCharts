package com.dgonzalez.charts.pointers;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author david.gonzalez (gonzalez.david31@gmail.com)
 */
public final class PointerAdapter<T extends PointerObject> extends BaseAdapter {

    private List<T> data;
    private Context context;
    private Paint defaultPaint;
    private Map<Integer, Paint> paints;

    private int endPointerSize = PointerUtilities.DEFAULT_POINTER_END_SIZE;

    public PointerAdapter(Context context, Paint defaultPaint) {
        this.data = new ArrayList<>();
        this.context = context;
        paints = new HashMap<>();
        this.defaultPaint = defaultPaint;
    }

    public void addItem(T obj, Paint paint){
        data.add(obj);
        paints.put(data.size()-1,paint);
    }

    public void setEndPointerSize(int endPointerSize) {
        this.endPointerSize = endPointerSize;
    }

    public void addItem(T item){
        data.add(item);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = new PointerView(context);
        }
        PointerView pointerView = (PointerView) convertView;
        if(paints.containsKey(position)){
            pointerView.setPaint(paints.get(position));
        }
        else{
            pointerView.setPaint(defaultPaint);
        }

        T item = getItem(position);

        pointerView.setPointerObject(item);
        pointerView.setEndPointerSize(endPointerSize);

        return pointerView;
    }
}
