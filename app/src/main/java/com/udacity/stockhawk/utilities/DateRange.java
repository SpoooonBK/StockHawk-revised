package com.udacity.stockhawk.utilities;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by spoooon on 3/28/17.
 */

public class DateRange {
    private Date to;
    private Date from;

    public DateRange(Date from, Date to) {
        this.to = to;
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }
}
