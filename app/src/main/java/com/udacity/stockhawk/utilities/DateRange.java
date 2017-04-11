package com.udacity.stockhawk.utilities;

import java.util.Date;

/**
 * Created by spoooon on 3/28/17.
 */

public class DateRange {
    private Date mTo;
    private Date mFrom;

    public DateRange(Date from, Date to) {
        this.mTo = to;
        this.mFrom = from;
    }

    public Date getTo() {
        return mTo;
    }

    public void setTo(Date to) {
        this.mTo = to;
    }

    public Date getFrom() {
        return mFrom;
    }

    public void setFrom(Date from) {
        this.mFrom = from;
    }

    @Override
    public String toString() {

        return "Date Range : " + DateManager.getMonthDateYearFormat(getFrom().getTime()) + " - "
                + DateManager.getMonthDateYearFormat(getTo().getTime());

    }
}
