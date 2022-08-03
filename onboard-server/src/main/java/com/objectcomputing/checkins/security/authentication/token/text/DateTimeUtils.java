package com.objectcomputing.checkins.security.authentication.token.text;

import com.objectcomputing.checkins.security.authentication.token.time.TimeToLive;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Andrew Montgomery on 12/25/21.
 */
public class DateTimeUtils {
    private static DateFormat[] formaters = {
        new SimpleDateFormat("yyyyMMdd"), //20171110
        new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy"), //Fri Nov 10 14:47:24 CST 2017
        new SimpleDateFormat("MM/dd/yy"), //11/10/17
        new SimpleDateFormat("MM/dd/yy kk:mm"), //11/10/17 14:47
        new SimpleDateFormat("MM/dd/yy kk:mm:ss"), //11/10/17 14:47:33
        new SimpleDateFormat("MM/dd/yyyy kk:mm:ss"), //11/10/2017 14:47:33
        new SimpleDateFormat("dd-MMM-yy"), //11/10/17
        new SimpleDateFormat("dd-MMM-yyyy"), //11/10/2017
        new SimpleDateFormat("yyyy/MM/dd"), //2017/11/10
    };

    //"12/31/4712 23:59:59"
    private final static Date MAX_DATE = parseDateStringSafely(4712, 12, 31, 23, 59, 59);

    private static Date parseDateStringSafely(int year, int month, int day, int hourOfDay, int minute, int second) {
        try {
            return new SimpleDateFormat("MM/dd/yyyy KK:mm:ss").parse(String.format("%d/%d/%d %d:%d:%d", month, day, year, hourOfDay, minute, second));
        } catch (ParseException e) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day, hourOfDay, minute);
            c.set(Calendar.SECOND, second);
            return c.getTime();
        }
    }

    public static Date parseDate(String text) throws ParseException {
        if(null == text || "".equals(text) || "0".equals(text)) {
            return null;
        }

        Date date = null;
        for(DateFormat formater : formaters) {
            try {
                date = formater.parse(text);
                if(null != date) {
                    break;
                }
            } catch (ParseException e) {
            }
        }

        if(date == null) {
            throw new ParseException("no matching date formatter found for (" + text + ")", 1);
        }

        if(Integer.parseInt(getYear(date)) < 100) {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.YEAR,2000);
            date=c.getTime();
        }

        return date;
    }

    public static String getYear(Date date) {
        return new SimpleDateFormat("yyyy").format(date);
    }

    public static String getDate(Date date) {
        return new SimpleDateFormat("dd-MMM-yy").format(date);
    }

    public static Date getMaxDate() {
        return MAX_DATE;
    }

    public static Date getDateFromCurrentDatePlus(TimeUnit unit, int time) {
        return getDateFromDatePlus(new Date(), unit, time);
    }

    public static Date getDateFromDatePlus(Date fromDate, TimeUnit unit, int time) {
        return getDateFromDatePlus(fromDate.getTime(), unit, time);
    }

    public static Date getDateFromDatePlus(long dateTime, TimeUnit unit, int time) {
        return new Date(dateTime + unit.toMillis(time));
    }

    public static Date getDateFromDatePlus(Date fromDate, TimeToLive ttl) {
        return getDateFromDatePlus(fromDate.getTime(), ttl);
    }

    public static Date getDateFromDatePlus(long dateTime, TimeToLive ttl) {
        return new Date(dateTime + ttl.getTime());
    }

    public static boolean afterCurrentDateFromDatePlus(Date fromDate, TimeToLive ttl) {
        Date current = new Date();
        return current.after(getDateFromDatePlus(fromDate, ttl));
    }

    public static boolean afterCurrentDateFromDatePlus(long dateTime, TimeToLive ttl) {
        Date current = new Date();
        return current.after(getDateFromDatePlus(dateTime, ttl));
    }

    public static TimeToLive parseTimeToLive(String text) throws ParseException {
        return new SimpleTimeToLiveFormat().parse(text);
    }
}
