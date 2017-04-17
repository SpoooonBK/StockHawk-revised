package com.udacity.stockhawk.utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by spoooon on 3/27/17.
 */

public class DateManager {

    public static final String MONTH_DATE_YEAR_FORMAT = "MM-dd-yy";
    public static final String LOG_TAG = DateManager.class.getSimpleName();


    public static String getMonthDateYearFormat(long timeInMillis) {

        Date date = new Date(timeInMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MONTH_DATE_YEAR_FORMAT);

        return simpleDateFormat.format(date);
    }

    public static String getMonthDateYearFormat(String timeInMillis) {

        return getMonthDateYearFormat(Long.parseLong(timeInMillis));
    }

    //Determines the correct dates for fiscal quarters
    public static List<String> getAvailableQuarters() {
        Calendar today = Calendar.getInstance();

        List<String> quarters = new ArrayList<>();


        Calendar quarter1End = Calendar.getInstance();
        quarter1End.set(Calendar.MONTH, Calendar.MARCH);
        quarter1End.set(Calendar.DATE, 31);
        if (quarter1End.compareTo(today) > 0) {
            quarter1End.add(Calendar.YEAR, -1);
        }


        Calendar quarter2End = Calendar.getInstance();
        quarter2End.set(Calendar.MONTH, Calendar.JUNE);
        quarter2End.set(Calendar.DATE, 30);
        if (quarter2End.compareTo(today) > 0) {
            quarter2End.add(Calendar.YEAR, -1);
        }

        Calendar quarter3End = Calendar.getInstance();
        quarter3End.set(Calendar.MONTH, Calendar.SEPTEMBER);
        quarter3End.set(Calendar.DATE, 30);
        if (quarter3End.compareTo(today) > 0) {
            quarter3End.add(Calendar.YEAR, -1);
        }


        Calendar quarter4End = Calendar.getInstance();
        quarter4End.set(Calendar.MONTH, Calendar.DECEMBER);
        quarter4End.set(Calendar.DATE, 31);
        if (quarter4End.compareTo(today) > 0) {
            quarter4End.add(Calendar.YEAR, -1);
        }


        quarters.add("Q1 " + quarter1End.get(Calendar.YEAR));
        quarters.add("Q2 " + quarter2End.get(Calendar.YEAR));
        quarters.add("Q3 " + quarter3End.get(Calendar.YEAR));
        quarters.add("Q4 " + quarter4End.get(Calendar.YEAR));

        // Sorts quarters into reverse chronological order
        Collections.sort(quarters, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {

                int o1Year = Integer.valueOf(o1.substring(3));
                int o2Year = Integer.valueOf(o2.substring(3));
                if (o1Year == o2Year) {
                    return o2.substring(1, 2).compareTo(o1.substring(1, 2));
                } else {
                    return o2.substring(3).compareTo(o1.substring(3));
                }
            }
        });

        return quarters;

    }

}
