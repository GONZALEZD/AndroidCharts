package com.dgonzalez.charts.sample.piecharts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dgonzalez.charts.piechart.SliceOfPie;
import com.dgonzalez.charts.sample.R;

/**
 * @author david.gonzalez (gonzalez.david31@gmail.com)
 * @since 22/08/2016
 */
public class SettingsFragment extends Fragment{

    private OnSettingSetListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.piecharts_settings_fragment, container, false);
        CheckBox inPercent = (CheckBox) v.findViewById(R.id.display_in_percent);
        inPercent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(listener != null){
                    listener.onDisplayInPercent(isChecked);
                }
            }
        });

        SeekBar textSize = (SeekBar) v.findViewById(R.id.text_size);
        textSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float textSize = 30.f;
                textSize += (progress*0.15f);
                if(listener != null){
                    listener.onTextSize(textSize);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        SeekBar textPosition = (SeekBar) v.findViewById(R.id.text_position);
        textPosition.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(listener != null){
                    listener.onTextPosition(((float)progress/100.f));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        return v;
    }

    public void setSettingsSetListener(OnSettingSetListener listener) {
        this.listener = listener;
    }

    interface OnSettingSetListener{
        public void onDisplayInPercent(boolean checked);
        public void onTextSize(float newSize);
        public void onTextPosition(float newPosition);
    }
}
