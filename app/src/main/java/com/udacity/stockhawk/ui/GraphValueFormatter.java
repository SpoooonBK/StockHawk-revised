package com.udacity.stockhawk.ui;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.utilities.DateManager;

/**
 * Created by spoooon on 3/26/17.
 */

public class GraphValueFormatter implements IAxisValueFormatter {



    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        long timeInMillis =  (long) value;
        return DateManager.getMonthDateYearFormat(timeInMillis);
    }
}

