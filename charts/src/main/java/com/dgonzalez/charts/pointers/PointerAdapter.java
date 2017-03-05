package com.dgonzalez.charts.pointers;

import android.content.Context;
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
public class PointerAdapter<T extends PointerObject> extends BaseAdapter {

    protected List<T> data;
    private Context context;
    protected Map<Integer, PointerRenderer> customRenderers;
    private PointerRenderer defaultRenderer;

    private float endPointerSize = PointerUtilities.DEFAULT_POINTER_END_SIZE;

    public PointerAdapter(Context context) {
        this.data = new ArrayList<>();
        this.context = context;
        customRenderers = new HashMap<>();
    }

    public void setDefaultRenderer(PointerRenderer renderer) {
        this.defaultRenderer = renderer;
    }

    public final PointerRenderer getDefaultRenderer() {
        return this.defaultRenderer;
    }

    public void addItem(T obj, PointerRenderer customRenderer){
        data.add(obj);
        customRenderers.put(data.size()-1, customRenderer);
    }

    public void setEndPointerSize(float endPointerSize) {
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

    public PointerRenderer getPointerRenderer(T object){
        int index = data.indexOf(object);
        if(index>=0 && index < data.size()){
            return getPointerRenderer(index);
        }
        return null;
    }

    public boolean setCustomRendererForPointer(PointerObject pointer, PointerRenderer renderer) {
        int position = data.indexOf(pointer);
        if(position >=0 && position < data.size()){
            customRenderers.put(position, renderer);
            return true;
        }
        return false;
    }

    private PointerRenderer getPointerRenderer(int position) {
        if(customRenderers.containsKey(position)){
            return customRenderers.get(position);
        }
        return defaultRenderer;
    }

    @Override
    public final View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = new PointerView(context);
        }
        PointerView pointerView = (PointerView) convertView;
        pointerView.setRenderer(getPointerRenderer(position));

        T item = getItem(position);

        pointerView.setPointerObject(item);
        pointerView.setEndPointerSize(endPointerSize);
        return pointerView;
    }

    public void removeItem(PointerObject pointer) {
        data.remove(pointer);
        notifyDataSetChanged();
    }

    public Context getContext(){
        return context;
    }
}
