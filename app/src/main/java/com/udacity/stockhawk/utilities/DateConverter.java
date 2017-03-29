package com.udacity.stockhawk.utilities;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by spoooon on 3/27/17.
 */

public class DateConverter {

    public static final String MONTH_DATE_YEAR_FORMAT = "MM-dd-yy";
    public static final String LOG_TAG = DateConverter.class.getSimpleName();




    public static String getMonthDateYearFormat(long timeInMillis){

        Date date = new Date(timeInMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MONTH_DATE_YEAR_FORMAT);

        return simpleDateFormat.format(date);
    }

    public static String getMonthDateYearFormat(String timeInMillis){

        return getMonthDateYearFormat(Long.parseLong(timeInMillis));
    }

}
