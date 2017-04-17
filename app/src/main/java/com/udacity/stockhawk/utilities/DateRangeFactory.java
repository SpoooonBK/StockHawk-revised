package com.udacity.stockhawk.utilities;

import android.content.Context;

import com.udacity.stockhawk.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by spoooon on 3/28/17.
 */

public class DateRangeFactory {


    public String LAST_SEVEN_DAYS;
    public String LAST_THIRTY_DAYS;
    public String YEAR_TO_DATE;
    public String Q_1;
    public String Q_2;
    public String Q_3;
    public String Q_4;


    public DateRangeFactory(Context context) {

        LAST_SEVEN_DAYS = context.getString(R.string.last_seven_days);
        LAST_THIRTY_DAYS = context.getString(R.string.last_thirty_days);
        YEAR_TO_DATE = context.getString(R.string.year_to_date);
        Q_1 = context.getString(R.string.q1);
        Q_2 = context.getString(R.string.q2);
        Q_3 = context.getString(R.string.q3);
        Q_4 = context.getString(R.string.q4);
    }

    public DateRange getDateRange(String rangeConstant) {


        Calendar today = Calendar.getInstance();
        Calendar quarterStart = Calendar.getInstance();
        Calendar quarterEnd = Calendar.getInstance();


        Date startingDate = null;
        Date endDate = new Date(today.getTimeInMillis());


        //using if else instead of switch in order to use strings.xml values as constants
        if (rangeConstant.equals(LAST_SEVEN_DAYS)) {

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -7);
            startingDate = cal.getTime();

        } else if (rangeConstant.equals(YEAR_TO_DATE)) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MONTH, Calendar.JANUARY);
            cal.set(Calendar.DATE, 1);
            startingDate = cal.getTime();

        } else if (rangeConstant.equals(LAST_THIRTY_DAYS)) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -30);
            startingDate = cal.getTime();

        } else if (rangeConstant.equals(Q_1)) {
            quarterStart.set(Calendar.MONTH, Calendar.JANUARY);
            quarterStart.set(Calendar.DATE, 1);
            quarterEnd.set(Calendar.MONTH, Calendar.MARCH);
            quarterEnd.set(Calendar.DATE, 31);
            if (quarterEnd.compareTo(today) > 0) {
                quarterStart.add(Calendar.YEAR, -1);
                quarterEnd.add(Calendar.YEAR, -1);
            }
            startingDate = quarterStart.getTime();
            endDate = quarterEnd.getTime();

        } else if (rangeConstant.equals(Q_2)) {
            quarterStart.set(Calendar.MONTH, Calendar.APRIL);
            quarterStart.set(Calendar.DATE, 1);
            quarterEnd.set(Calendar.MONTH, Calendar.JUNE);
            quarterEnd.set(Calendar.DATE, 30);
            if (quarterEnd.compareTo(today) > 0) {
                quarterStart.add(Calendar.YEAR, -1);
                quarterEnd.add(Calendar.YEAR, -1);
            }
            startingDate = quarterStart.getTime();
            endDate = quarterEnd.getTime();

        } else if (rangeConstant.equals(Q_3)) {
            quarterStart.set(Calendar.MONTH, Calendar.JULY);
            quarterStart.set(Calendar.DATE, 1);
            quarterEnd.set(Calendar.MONTH, Calendar.SEPTEMBER);
            quarterEnd.set(Calendar.DATE, 30);
            if (quarterEnd.compareTo(today) > 0) {
                quarterStart.add(Calendar.YEAR, -1);
                quarterEnd.add(Calendar.YEAR, -1);
            }
            startingDate = quarterStart.getTime();
            endDate = quarterEnd.getTime();

        } else if (rangeConstant.equals(Q_4)) {
            quarterStart.set(Calendar.MONTH, Calendar.OCTOBER);
            quarterStart.set(Calendar.DATE, 1);
            quarterEnd.set(Calendar.MONTH, Calendar.DECEMBER);
            quarterEnd.set(Calendar.DATE, 31);
            if (quarterEnd.compareTo(today) > 0) {
                quarterStart.add(Calendar.YEAR, -1);
                quarterEnd.add(Calendar.YEAR, -1);
            }
            startingDate = quarterStart.getTime();
            endDate = quarterEnd.getTime();
        }


        return new DateRange(startingDate, endDate);
    }


}
