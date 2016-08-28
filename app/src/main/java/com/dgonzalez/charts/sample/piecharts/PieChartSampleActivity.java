package com.dgonzalez.charts.sample.piecharts;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;

import com.dgonzalez.charts.piechart.PieChart;
import com.dgonzalez.charts.piechart.SliceOfPie;
import com.dgonzalez.charts.sample.R;

/**
 * @author david.gonzalez (gonzalez.david31@gmail.com)
 * @since 12/08/2016
 */
public class PieChartSampleActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.piecharts_activity);

        final SettingsFragment fragment = (SettingsFragment) getSupportFragmentManager().findFragmentById(R.id.piechart_description);

        final PieChart pieChart = (PieChart) findViewById(R.id.piechart);
        // Reference 566Mt
        float ref = 566.f/100.f;
        pieChart.addSlice(new SliceOfPie("Rest of the world", 31.4f*ref, ContextCompat.getColor(this, R.color.c8)));
        pieChart.addSlice(new SliceOfPie("Russian federation", 26.8f*ref, ContextCompat.getColor(this, R.color.c7)));
        pieChart.addSlice(new SliceOfPie("Middle East", 11.9f*ref, ContextCompat.getColor(this, R.color.c6)));
        pieChart.addSlice(new SliceOfPie("Norway", 9.2f*ref, ContextCompat.getColor(this, R.color.c5)));
        pieChart.addSlice(new SliceOfPie("North Africa", 6.7f*ref, ContextCompat.getColor(this, R.color.c4)));
        pieChart.addSlice(new SliceOfPie("China", 5.5f*ref, ContextCompat.getColor(this, R.color.c3)));
        pieChart.addSlice(new SliceOfPie("United Kingdom", 4.4f*ref, ContextCompat.getColor(this, R.color.c2)));
        pieChart.addSlice(new SliceOfPie("U.S.A", 4.1f*ref, ContextCompat.getColor(this, R.color.c1)));

        pieChart.invalidate();

        pieChart.setOnSliceClickListener(new PieChart.OnSliceClickListener() {
            @Override
            public void onClick(SliceOfPie itemClicked) {
                if(fragment != null) {
                    TextView name = (TextView) findViewById(R.id.name);
                    TextView value = (TextView) findViewById(R.id.value);

                    name.setText(itemClicked.getName());
                    value.setText(""+(int)itemClicked.getValue()+"Mt");
                }
                else {
                    Log.e("COUCOU", "Fragment NULL");
                }
            }
        });

        fragment.setSettingsSetListener(new SettingsFragment.OnSettingSetListener() {
            @Override
            public void onDisplayInPercent(boolean checked) {
                pieChart.displayTextInPercent(checked);
            }

            @Override
            public void onTextSize(float newSize) {
                pieChart.setTextSize(newSize);
            }

            @Override
            public void onTextPosition(float newPosition){
                pieChart.setTextPosition(newPosition);
            }
        });
    }
}
