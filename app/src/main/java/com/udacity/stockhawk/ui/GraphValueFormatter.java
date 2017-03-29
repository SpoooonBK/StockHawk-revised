package com.udacity.stockhawk.ui;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.udacity.stockhawk.utilities.DateConverter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by spoooon on 3/26/17.
 */

public class GraphValueFormatter implements IAxisValueFormatter {



    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        long timeInMillis =  (long) value;
        return DateConverter.getMonthDateYearFormat(timeInMillis);
    }
}

