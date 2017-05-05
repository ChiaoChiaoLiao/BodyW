
package com.mccc.bodyw;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DecimalFormat;

public class XYMarkerView extends MarkerView {

    public XYMarkerView(Context context) {
        super(context, R.layout.marker_view);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        super.refreshContent(e, highlight);
        ImageView marker = (ImageView) findViewById(R.id.marker);
        marker.setColorFilter(Integer.valueOf(e.getData().toString()));
    }

    @Override
    public MPPointF getOffset() {
        ImageView marker = (ImageView) findViewById(R.id.marker);
        return new MPPointF(-(getWidth() / 2), -getHeight() + marker.getMeasuredHeight() / 2);
    }
}
