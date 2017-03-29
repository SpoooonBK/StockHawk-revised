package com.udacity.stockhawk.utilities;

import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by spoooon on 3/28/17.
 */

public class DateRangeFactory {

    public static final String WEEK = "Week";
    public static final String MONTH = "Month";
    public static final String YEAR_TO_DATE = "Year-to-Date";
    public static final String YEAR = "Year";
    public static final String JAN = "January";
    public static final String FEB = "February";

    public static DateRange getCalendarRange(String rangeConstant, @Nullable Date date){

        Calendar today = Calendar.getInstance();

        Date startingDate = null;
        Date endDate = null;

        if(date == null){
            endDate = new Date(today.getTimeInMillis());
        }

        switch (rangeConstant){

            case WEEK:{
                if(date == null){
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -7);
                    startingDate = cal.getTime();
                } else {
                    startingDate = date;
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    cal.add(Calendar.DATE, 7);
                    endDate = cal.getTime();

                }
                break;
            }

            case YEAR_TO_DATE:{
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, Calendar.JANUARY, 1);
                startingDate = cal.getTime();
            }

            case MONTH: {
                if(date == null){
                    Calendar cal = Calendar.getInstance();


                }
            }

        }


        return new DateRange(startingDate, endDate);
    }


}
