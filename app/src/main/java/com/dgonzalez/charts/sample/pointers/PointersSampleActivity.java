package com.dgonzalez.charts.sample.pointers;

import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ListView;

import com.dgonzalez.charts.pointers.MutablePointerRenderer;
import com.dgonzalez.charts.pointers.PointerAdapter;
import com.dgonzalez.charts.pointers.PointersContainerView;
import com.dgonzalez.charts.pointers.PointerObject;
import com.dgonzalez.charts.sample.R;

import java.util.HashMap;
import java.util.Map;

public class PointersSampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pointers_activity);

        final PointersContainerView container = (PointersContainerView) findViewById(R.id.markersContainer);

        Paint defaultPaint = new Paint();
        defaultPaint.setStrokeWidth(4);
        defaultPaint.setAntiAlias(true);
        defaultPaint.setTypeface(Typeface.DEFAULT_BOLD);
        defaultPaint.setColor(getResources().getColor(R.color.colorPrimary));
        PointerAdapter<PointerObject> adapter = new PointerAdapter(this);
        LegendAdapter legendAdapter = new LegendAdapter(this);

        Paint paint;
        float[] hsvColor = new float[]{0, 1f, 1f};
        Map<String, PointerObject> data = new HashMap<>();
        data.put("Pollen", new PointerObject(new Point(692, 427), new Point(814,692)));
        data.put("Petal", new PointerObject(new Point(893, 377), new Point(1080,377)));
        data.put("Filament", new PointerObject(new Point(620, 374), new Point(281,255)));
        data.put("Anther", new PointerObject(new Point(654, 366), new Point(334,558)));
        data.put("Peduncle", new PointerObject(new Point(1138, 161), new Point(1210,197)));
        data.put("Pistil", new PointerObject(new Point(672, 373), new Point(447,81)));

        int i=0;
        int[] colors = new int[]{
//                R.color.p1,
//                R.color.p2,
//                R.color.p3,
                R.color.p4,
//                R.color.p5,
                R.color.p6,
                R.color.p7,
                R.color.p8,
                R.color.p9,
                R.color.p10,
        };
        for(String key : data.keySet()){
            PointerObject marker = data.get(key);
            char c = (char) ((int)('A') + i);
            marker.setEndPointer(new Character(c));
            MutablePointerRenderer renderer = new MutablePointerRenderer(container.getDefaultRenderer());
            paint = new Paint(defaultPaint);
            paint.setColor(ContextCompat.getColor(this, colors[i]));
            renderer.setLineColorAndStroke(paint);
            renderer.setSymbolColor(paint.getColor());
            adapter.addItem(marker, renderer);
            legendAdapter.add(key,c,paint);
            i++;
        }

        container.setDataAdapter(adapter);

        ListView legend = (ListView) findViewById(R.id.legendList);
        legend.setAdapter(legendAdapter);

        container.fitImageToView();
        container.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                float mid = (container.getMaximumScale() - container.getMinimumScale())*0.5f + container.getMinimumScale();
                boolean isZoomed= container.getZoom()>mid? true : false;
                if(isZoomed){
                    // unzoom
                    container.zoom(container.getMinimumScale(), e.getX(), e.getY());
                }
                else {
                    // zoom
                    container.zoom(container.getMaximumScale(), e.getX(), e.getY());
                }
                return true;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return false;
            }
        });
    }
}
